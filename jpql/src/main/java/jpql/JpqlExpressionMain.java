package jpql;

import jakarta.persistence.*;
import jpql.domain.Member;
import jpql.domain.Team;
import jpql.domain.type.MemberType;

import java.util.List;

/*
    JPQL 타입 표현
    - 문자: 'HELLO', 'She''s'
    - 숫자: 10L(Long), 10D(Double), 10F(Float)
    - Boolean: TRUE, FALSE
    - ENUM: jpql.MemberType.Admin (패키지명 포함)
    - 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)
        "select i from Item i where type(i) = Book", Item.class
        Item을 Book이 상속받아서 사용을 하고 있음
        해당 쿼리문은 Item 상속 계층 중에서 실제 엔티티 타입이 Book인 객체만 조회를 하게 됨

    JPQL 기타
    - SQL과 문법이 같은 식
    - EXISTS, IN
    - AND, OR, NOT
    - =, >, >=, <, <=, <>
    - BETWEEN, LIKE, IS NULL
*/
public class JpqlExpressionMain {
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
            member.setType(MemberType.ADMIN);
            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

//            String query = "select m.username, 'HELLO', true from Member m " +
//                           "where m.type = jpql.domain.type.MemberType.ADMIN";
            String query = "select m.username, 'HELLO', true from Member m " +
                    "where m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
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
