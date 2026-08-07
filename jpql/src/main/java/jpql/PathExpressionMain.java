package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;
import jpql.domain.Team;
import jpql.domain.type.MemberType;

import java.util.Collection;
import java.util.List;

/*
    경로 표현식은 JPQL에서 엔티티 별칭을 시작점으로 하여 .을 통해 속성이나 연관관계를 탐색하는 표현식이다.

    경로 표현식 특징
    - 상태 필드(state field): 단순히 값을 저장하기 위한 필드 (ex: m.username)
    - 연관 필드(association field): 연관관계를 위한 필드
        - 단일 값 연관 필드:
            @ManyToOne, @OneToOne, 대상이 엔티티(ex: m.team)
        - 컬렉션 값 연관 필드:
            @OneToMany, @ManyToMany, 대상이 컬렉션(ex: m.orders)

    - 상태 필드: 경로 탐색의 끝, 탐색 X
    - 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색 O
    - 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색 X
        - FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능

    참고로 실무에서는 묵시적 내부 조인이 발생하게 되면 한 눈에 쿼리문을 파악하기 힘들어 사용하지 않음
    따라서 단일 값 연관, 컬렉션 값 연관 경로 표현식을 사용할 일은 거의 없음

    명시적 조인, 묵시적 조인
    - 명시적 조인: join 키워드를 직접 사용
        select m from Member m join m.team t
    - 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL조인 발생 (내부 조인만 가능)
        select m.team from Member m

    경로 표현식 - 예제
    - select o.member.team from Order o -> 성공 (묵지적 조인이 2번 일어남)
    - select t.members from Team t -> 성공
    - select t.members.username from Team t -> 실패 (컬렉션 값 연관 경로은 탐색이 불가능)
    - select m.username from Team t join t.members m -> 성공

    경로 탐색을 사용한 묵시적 조인 시 주의사항
    - 항상 내부 조인
    - 컬렉션은 경로 탐색의 끝이므로 명시적 조인을 통해 별칭을 얻어야지만 해당 별칭으로 탐색이 가능
    - 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만 묵시적 조인으로 인해 SQL의 FROM(JOIN)절에 영향을 줌

    실무 조언
    - 가급적 묵시적 조인 대신에 명시적 조인 사용
    - 조인은 SQL 튜닝에 중요 포인트
    - 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움
*/
public class PathExpressionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();

//            String query = "select m.username from Member m"; // 상태 필드
//            String query = "select m.team from Member m"; // 단일 값 연관 경로 m.team에서 m.team.name 탐색이 가능

            /*
                컬렉션 값 연관 경로
                    t.members.name 이런 탐색이 불가능
                String query = "select t.members from Team t";
                List result = em.createQuery(query, List.class)
                        .getResultList();

                for (Object o : result) {
                    System.out.println("o = " + o);
                }
            */

/*
            컬랙션 값 사이즈 구하는 방법
            String query = "select size(t.members) from Team t";
            Integer result = em.createQuery(query, Integer.class)
                    .getSingleResult();

            System.out.println("result = " + result);
*/
            // 컬렉션 값 연관 경로에서 FROM절 명시적 조인을 통해 별칭을 얻어서 별칭을 통해 탐색
            String query = "select m.username from Team t join t.members m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
