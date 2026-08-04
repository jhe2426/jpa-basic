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
    조건식 - CASE 식
    기본 CASE 식
        select
            case when m.age <= 10 then '학생요금'
                 when e.age >= 60 then '경로요금'
                 else '일반요금'
            end
        from Member m

    단순 CASE 식
        select
            case t.name
                when '팀A' then '인센티브110%'
                when '팀B' then '인센티브120%'
                else '인센티브105%'
            end
        from Team t

    - COALESCE: 인수로 전달된 값을 왼쪽부터 확인하여, 처음으로 NULL이 아닌 값을 반환한다.
        모두 NULL이면 NULL을 반환한다.
    - NULLIF: 두 값이 같으면 null 반환, 다르면 첫 번째 값 반환
    사용자 이름이 없으면 이름 없는 회원을 반환
        select coalesce(m.username, '이름 없는 회원') from Member m
    사용자 이름이 '관리자'이면 null을 반환하고 나머지는 본인의 이름을 반환
        select NULLIF(m.username, '관리자') from Member m
*/
public class ConditionalExpressionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
/*
        // 기본 CASE 식
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();


            String query = "select " +
                           "    case when m.age <= 10 then '학생요금' " +
                           "         when m.age >= 60 then '경로요금' " +
                           "         else '일반요금' " +
                           "    end " +
                           "from Member m";
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
*/

/*
        // COALESCE
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername(null);
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();


            String query = "select coalesce(m.username, '이름 없는 회원') as username " +
                    "from Member m";
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
*/

        // NULLIF
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();


            String query = "select nullif(m.username, '관리자') as username " +
                    "from Member m";
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
