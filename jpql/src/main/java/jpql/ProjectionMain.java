package jpql;

import jakarta.persistence.*;
import jpql.domain.Address;
import jpql.domain.Member;
import jpql.domain.Team;
import jpql.dto.MemberDto;

import java.util.List;


/*
    프로젝션
    - SELECT 절에 조회할 대상을 지정하는 것
    - 프로젝션 대상: 엔티티, 임베티드 타입, 스칼라 타입 (숫자, 문자 등 기본 데이터 타입)
    - SELECT m FROM Member m -> 엔티티 프로젝션
    - SELECT m.team FROM Member m -> 엔티티 프로젝션
    - SELECT m.address FROM Member m -> 임베디드 타입 프로젝션
    - SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션
    - DISTINCT로 중복 제거 가능

    프로젝션 - 여러 값 조회
    - SELECT m.username, m.age FROM Member m
    1. Query 타입으로 조회
    2. Object[] 타입으로 조회
    3. new 명령어로 조회
        - 단순 값을 DTO로 바로 조회
            SELECT new jpql.UserDTO(m.username, m.age) FROM Member m
        - 패키지 명을 포함한 전체 클래스명 입력
        - 순서와 타입이 일치하는 생성자 필요
*/
public class ProjectionMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            Member findMember = result.get(0);
            findMember.setAge(20);

/*
            List<Team> resultTeam = em.createQuery("select m.team from Member m", Team.class)
                    .getResultList();
            이렇게 작성하면 JPQL에는 조인문이 명시되어 있지 않지만, m.team처럼 Member에서 Team으로 연관관계 경로를 탐색하므로
            Team을 가져오기 위해 필수적으로 조인 SQL문이 나가게 된다.
            위와 같이 해당 조인을 명시적으로 보이도록 작성하지 않으면 코드 유지보수를 할 때
            한눈에 어떤 쿼리문이 나가는지 알아보기 힘들기 때문에, 아래와 같이 JPQL에도 join문을 직접 작성해주는 것이 좋다.
            위의 같은 JPQL문은 묵시적 조인이다.
*/

            List<Team> resultTeam = em.createQuery("select t from Member m join m.team t", Team.class)
                    .getResultList();

            em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

            em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();


            // Object[] 타입으로 조회
            List resultList = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            Object o = resultList.get(0);
            Object[] objectResult = (Object[]) o;
            System.out.println("username = " + objectResult[0]);
            System.out.println("age = " + objectResult[1]);


            // Query 타입으로 조회
            List<Object[]> resultList2 = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            Object[] objectResult2 = resultList2.get(0);
            System.out.println("username = " + objectResult2[0]);
            System.out.println("age = " + objectResult2[1]);


            // new 명령어로 조회
            List<MemberDto> resultList1 = em.createQuery("select new jpql.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                    .getResultList();

            MemberDto memberDto = resultList1.get(0);
            System.out.println("memberDto.getUsername() = " + memberDto.getUsername());
            System.out.println("memberDto.getAge() = " + memberDto.getAge());

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
