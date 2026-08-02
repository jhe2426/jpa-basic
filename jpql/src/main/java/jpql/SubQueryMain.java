package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;
import jpql.domain.Team;

import java.util.List;

/*
    서브 쿼리
    - 나이가 평균보다 많은 회원
        select m from Member m
        where m.age > (select avg(m2.age) from Member m2)
    - 한 건이라도 주문한 고객
        해당 쿼리는 상관 서브쿼리를 의미한다.
        상관 서브쿼리는 서브쿼리의 'm'은 서브쿼리 내부에서 선언된 별칭이 아니라 메인 쿼리의 'Member' m을 참조한다.

        일반 서브쿼리는 메인 쿼리와 독립적으로 결과를 계산할 수 있지만, 상관 서브쿼리는 메인 쿼리의 현재 행에 따라 서브쿼리 결과가 달라진다.

        아래의 상관 서브쿼리문은 회원을 조회할 때마다 현재 회원과 연관된 주문이 존재하는지 서브쿼리에서 확인해야 한다.
        따라서 논리적으로 메인 쿼리의 각 행마다 서브쿼리가 반복 평가될 수 있으며, 메인 테이블의 데이터가 많으면 성능이 저하될 가능성이 있다.

        단순히 데이터의 존재 여부만 확인할 때에는 count(o) > 0보다 한 건을 찾으면 탐색을 종료할 수 있는 exists 사용을 우선 고려할 수 있다.
        select m from Member m
        where (select count(o) from Order o where m = o.member) > 0

    서브 쿼리 지원 함수
    - [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
        - {ALL | ANY | SOME} (subquery)
        - ALL은 왼쪽 값을 서브쿼리가 반환한 각 값과 모두 비교해서 각각의 비교 결과가 전부 true일 때만 전체 조건이 true가 된다.
        - ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
    - [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

    서부 쿼리 - 예제
    - 팀A 소속인 회원
        select m from Member m
        where exists (select t from m.team t where t.name = '팀A')
    - 전체 상품 각각의 재고보다 주문량이 많은 주문들
        select o from Order o
        where o.orderAmount > ALL (select p.stockAmount from Product p)
    - 어떤 팀이든 팀에 소속된 회원
        select m from Member m
        where m.team = ANY (select t from Team t)

    서브 쿼리의 한계
    - JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
    - SELECT 절도 가능(하이버네이트에서 지원)
    - FROM 절의 서브 쿼리는 현재 JPQL에서 불가능
        - 조인으로 풀 수 있으면 풀어서 해결
*/
public class SubQueryMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            // SELECT 절 서브쿼리
            String query = "select (select avg(m1.age) From Member m1) as avgAge from Member m left join Team t on m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            System.out.println("result = " + result.size());

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
