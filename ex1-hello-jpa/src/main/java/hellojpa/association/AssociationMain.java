package hellojpa.association;

import hellojpa.association.domain.Member;
import hellojpa.association.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class AssociationMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();


        // 지연로딩
/*
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");
            member.setTeam(team);
            em.persist(member);


            em.flush();
            em.clear();

            Member m = em.find(Member.class, member.getId());

            System.out.println("m = " + m.getTeam().getClass()); // Team$HibernateProxy$Y6WeyO7X

            System.out.println("===============");
            m.getTeam().getName(); // 실제 team을 사용하는 시점에 초기화 (DB 조회)
            System.out.println("===============");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/
/*
        // 즉시로딩
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");
            member.setTeam(team);
            em.persist(member);


            em.flush();
            em.clear();

            Member m = em.find(Member.class, member.getId());

            System.out.println("m = " + m.getTeam().getClass()); // class hellojpa.association.domain.Team

            System.out.println("===============");
            System.out.println("teamName = " + m.getTeam().getName());
            System.out.println("===============");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/

        // 즉시 로딩 JPQL N+1 문제
        try {

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setName("member1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setName("member2");
            member2.setTeam(teamB);
            em.persist(member2);


            em.flush();
            em.clear();

            /*
                SQL: select * from Member -> Member는 Team이 즉시 로딩이므로 select * from Team where TEAM_ID = xxx 이 쿼리문도 추가로 조회되게 됨SQL: select * from Member -> Member는 Team이 즉시 로딩이므로 select * from Team where TEAM_ID = xxx 이 쿼리문도 추가로 나가게 됨
                - Member 목록 조회 후 각 Member가 참조하는 Team을 조회하게 위한 추가 SELECT가 실행된다.
                - 서로 다른 Team을 참조하는 Member가 많을수록 Team 초회 쿼리가 반될 수 있으며, 이를 N+1 문제라고 한다.
            */
            List<Member> members = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            // N+1 문제를 해결하는 방법 중 하나인 fetch join
            List<Member> members2 = em.createQuery("select m from Member m join fetch m.team", Member.class)
                    .getResultList();


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
