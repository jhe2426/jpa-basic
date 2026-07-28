package hellojpa.query;

import hellojpa.domain.Member4;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

/*
    JPQL
    - JPA를 사용하면 엔티티 객체를 중심으로 개발
    - 문제는 검새 쿼리인데, 검색을 할때에도 테이블이 아닌 엔티티 객체를 대상으로 검색을 해야함
    - 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
    - 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요
    - JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공
    - SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN을 지원
    - JPQL은 엔티티 객체를 대상으로 쿼리
    - SQL은 데이터베이스 테이블을 대상으로 쿼리
    - 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
    - SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음
    - JPQL을 한마디로 정의하면 객체 지향 SQL이다.
*/
public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            List<Member4> result = em.createQuery(
                    "select m From Member4 as m where m.name like '%kim'",
                    Member4.class
            ).getResultList();

            for (Member4 member : result) {
                System.out.println("member = " + member);
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
