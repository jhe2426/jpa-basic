package jpql;

import jakarta.persistence.*;
import jpql.domain.Member;

import java.util.List;


/*
    JPQL 문법
    - select m from Member as m where m.age > 18
    - 엔티티와 속성은 대소문자 구분O (Member, age)
    - JPQL 키워드는 대소문자 구분X (SELECT, FROM, where)
    - 엔티티 이름 사용, 테이블 이름이 아님 (Member)
        @Entity(name = "xxx") 여기에 직접 설정한 xxx를 사용하거나
        따로 name 속성을 지정하지 않았다면 해당 엔티티 클래스 이름이 엔티티 이름으로 사용됨
    - 별칭은 필수(m) (as는 생략가능)
*/
public class JpqlMain {

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

            /*
                TypeQuery, Query
                - TypeQuery: 반환 타입이 명확할 때 사용
                    TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
                - Query: 반환 타입이 명확하지 않을 때 사용
                    Query query = em.createQuery("SELECT m.username, n.age from Member m");
            */
            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");

            /*
                결과 조회 API
                - query.getResultList(): 결과가 하나 이상일 때, 리스트 반환
                    - 결과가 없으면 빈 리스트 반환
                - query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환
                    - 결과가 없으면: jakarta.persistence.NoResultException
                    - 둘 이상이면: jakarta.persistence.NonUniqueResultException
            */
            List<Member> resultList = query1.getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

/*
            TypedQuery<Member> query4 = em.createQuery("select m from Member m where m.id = 10", Member.class);
            Member result = query4.getSingleResult(); // jakarta.persistence.NoResultException 예외 발생
            System.out.println("result = " + result);
*/
/*
            TypedQuery<Member> query5 = em.createQuery("select m from Member m where m.username = :username", Member.class);
            query5.setParameter("username", "member1");
            Member singleResult = query5.getSingleResult();
*/
            Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("singleResult = " + singleResult.getUsername());

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
