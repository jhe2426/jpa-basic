package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;
import jpql.domain.Team;

import java.util.List;

public class JoinMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
/*
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

            // 내부 조인
//            String query = "select m from Member m join m.team t";

            // 외부 조인
//            String query = "select m from Member m left join m.team t";

            // 세타 조인
            String query = "select m from Member m, Team t where m.username = t.name";
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
*/

        /*
            조인 - ON절
            ON절을 활용한 조인
            1. 조인 대상 필터링
                예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
                    SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'
            2. 연관관계 없는 엔티티 외부 조인
                예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
                    SELECT m, t FROM Member m LEFT JOIN TEAM t on m.username = t.name
        */
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

            // 1. 조인 대상 필터링
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'";

            // 2. 연관관계 없는 엔티티 외부 조인
            String query = "select m from Member m left join Team t on m.username = t.name";
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
