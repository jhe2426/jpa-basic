package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;
import jpql.domain.Team;
import jpql.domain.type.MemberType;

import java.util.List;

/*
    페치 조인(fetch join)의 특징과 한계

    - 연견된 엔티티들은 SQL 한 번으로 조회 - 성능 최적화
    - 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
        - 글로벌 로딩 전략: @OneToMany(fetch = FetchType.LAZY)
    - 실무에서 글로벌 로딩 전략은 모두 지연 로딩설정 함
    - 최적화가 필요한 곳은 페치 조인 적용

    - 페치 조인 대상에는 별칭을 줄 수 없다.
        - 하이버네이트는 가능, 가급적 사용 X
            페치 조인을 사용하는 이유가 연관된 관계에 대해서 빠짐 없이 전부 조회하기 위함인데 조인 대상에게 별칭을 줘서 해당 별칭으로 조건을 주어
            특정 몇 개만 조회하도록 쿼리문을 작성하게 되면 페치 조인의 성격에 위배되는 결과가 반환되므로 관례상 별칭을 사용하지 말라는 것

            왜냐하면 연관된 관계에 대해서 전부 조회가 안 된 후 삭제, 수정의 작업이 일어나게 되면 예상치 못하게 조회가 되지 않은 데이터에 대해서는
            삭제가 안되어 버리거나 이러한 상황이 발생되므로 사용하지 않는 것이 관례이다.
                Fetch Join은 연관된 엔티티를 함께 조회하여 연관관계를 초기화하기 위한 목적으로 사용한다.

                그런데 Fetch Join 대상에 별칭을 주고 where m.age > 20과 같은 조건을 걸면,
                실제 Team.members에 10명의 회원이 존재하더라도 조건을 만족하는 회원만 조회될 수 있다.

                즉, DB에는 10명이 존재하지만 영속성 컨텍스트의 Team.members에는 일부 회원만 들어 있는 불완전한 컬렉션이 만들어질 수 있다.

                이후 애플리케이션에서 이 컬렉션을 전체 연관관계라고 생각하고 수정·삭제 등의 로직을 수행하면 예상하지 못한 동작이 발생할 수 있으므로
                Fetch Join 대상에 조건을 걸어 일부 데이터만 가져오는 방식은 피하는 것이 좋다.

    - 둘 이상의 컬렉션은 페치 조인을 할 수 없다.
    - 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
        - 컬렉션 일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
        - 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)
            WARN: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
            컬렉션 Fetch Join은 루트 엔티티 하나가 여러 SQL 행으로 증가하므로 DB의 LIMIT/OFFSET을 그대로 적용하면 컬렉션이 중간에 잘릴 수 있기 때문이다.
            반면 일대일·다대일 Fetch Join + 페이징은 일반적으로 DB에서 페이징이 일어남
        - 컬렉션 페치 조인(다대다, 일대다)에서는 페이징을 사용하면 안 됨
            컬렉션을 페치 조인하면 1개의 루트 엔티티가 연관된 컬렉션 원소 수만큼 SQL 결과 행으로 증가한다.
            이 상태에서 DB에서 바로 페이징을 적용하면 하나의 엔티티에 속한 컬렉션이 중간에서 잘려 불완전하게 초기화될 수 있다.
                예를 들어 Team A에 Member가 5명 존재하고 Team을 기준으로 2개를 조회하기 위해 페이징을 적용했다고 가정한다.
                페치 조인의 SQL 결과는 다음과 같이 Team 하나가 Member 수만큼 여러 행으로 증가한다.

                Team A | Member1
                Team A | Member2
                Team A | Member3
                Team A | Member4
                Team A | Member5
                Team B | Member6
                Team B | Member7

                여기서 DB가 단순히 LIMIT 2를 적용하면

                Team A | Member1
                Team A | Member2 만 조회된다.

                하지만 개발자가 요청한 것은 JOIN 결과 2행이 아니라 "Team 엔티티 2개"이다.
                또한 Fetch Join을 사용했으므로 조회된 Team의 members 컬렉션도 전체가 초기화되어 있어야 한다.

                따라서 DB의 JOIN 결과 행을 기준으로 바로 페이징하면
                원하는 Team 개수도 얻을 수 없고, Team A의 members 역시 일부만 조회되어 불완전한 컬렉션이 만들어질 수 있다.

                이러한 문제 때문에 Hibernate 5/6에서는 컬렉션 Fetch Join과 페이징을 함께 사용하면
                DB의 JOIN 결과 행에 LIMIT/OFFSET을 직접 적용하지 않고 전체 JOIN 결과를 조회한 뒤
                Hibernate가 애플리케이션 메모리에서 루트 엔티티를 기준으로 페이징한다.

                DB JOIN 결과 전체 조회
                            ↓
                Team A + Member1 ~ Member5 구성
                Team B + Member6 ~ Member7 구성
                            ↓
                각각의 컬렉션을 완전하게 초기화
                            ↓
                Hibernate가 Team이라는 루트 엔티티 기준으로 원하는 개수만 선택
                        ↓
                최종 결과 반환과 같은 방식으로 처리한다.

            페치 조인은 관례적으로 연관관계 전체가 초기화되었다고 생각하고 사용하는데, 일부 데이터만 조회되면 개발자가 해당 컬렉션을
            전체 데이터로 오인할 수 있어 데이터 정합성 문제를 야기할 수 있다.

            이때 DB에서 바로 페이징하면 하나의 엔티티에 속한 컬렉션이 중간에 잘려 불완전하게 초기화 될 수 있다.
            그래서 Hibernate는 컬렉션이 중간에 잘리는 것을 방지하기 위해 DB에서 필요한 개수만 조회하는 것이 아니라 전체 조회 결과를 가져온 뒤,
            애플리케이션 메모리에서 원하는 개수만 골라 반환할 수 있다.

            이 경우 조회해야 하는 데이터가 많아질수록 불필요한 데이터를 대량으로 가져오게 되므로 심각한 성능 문제를 발생할 수 있다.
            반면 반면 일대일, 다대일 Fetch Join은 일반적으로 루트 엔티티 1개가 SQL 결과 1행으로 유지되므로 DB에서 페이징해도
            연관관계가 중간에 잘리는 문제가 발생하지 않는다.

    - 정리
        - 모든 것을 페치 조인으로 해결할 수는 없음
        - 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적
            - 모든 연관관계 .으로 조회할 수 있는 모든 엔티티들을 초기화해주기 때문
        - 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야하면, 페치 조인보다는 일반 조인을 사용하고 필요한 데이터들만
            조회해서 DTO로 반환하는 것이 효과적
*/
public class FetchJoinMain2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();


        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("팀C");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(10);
            member3.setType(MemberType.ADMIN);
            member3.changeTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            member4.setAge(10);
            member4.setType(MemberType.ADMIN);
            em.persist(member4);

            em.flush();
            em.clear();

/*
            // 페치 조인 대상에는 별칭을 줄 지 않는 것이 관례
            String query = "select t from Team t join fetch t.members m"; // 비록 이렇게 별칭을 줘도 에러는 안 남
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
*/

//            컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
            String query = "select t from Team t join fetch t.members";
            // 컬렉션을 페이징 하는 방법 2가지
            // 1. 다대일 JPQL문을 날려서 페이징을 하여 해당 컬렉션 페이징의 결과로 사용하기 (페치 조인 방향을 뒤집어서 페이징하기)
            String query2 = "select m from Member m join fetch m.team t";

            /*
                2. @BatchSize(size = 100)를 사용
                    컬렉션 Fetch Join을 사용하지 않아 페이징을 DB에서 정상적으로 수행하고,
                    그 대신 발생하는 컬렉션 LAZY 조회의 N+1 문제를 @BatchSize로 여러 컬렉션을 IN 쿼리 한 번에 조회하여 완화한다.
            */
            String query3 = "select t from Team t";

            List<Team> result = em.createQuery(query3, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            System.out.println("result = " + result.size());

            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("member = " + member);
                }
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
