package hellojpa.query;

import hellojpa.domain.Member4;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

/*
    Criteria
    - 문자가 아닌 자바 코드로 JPQL을 작성할 수 있음
    - JPQL 빌더 역할
    - JPA 공식 기능
    - 단점: 너무 복잡하고 실용성이 없다. 그래서 실무에서 사용하지 않음 (유지보수 하기에 너무 어려움)
    - Criteria 대신에 QueryDSL 사용 권장

    QueryDSL
    - 문자가 아닌 자바 코드로 JPQL을 작성할 수 있음
    - JPQL 빌더 역할
    - 컴파일 시점에 문법 오류를 찾을 수 있음
    - 동적쿼리 작성이 편리함
    - 단순하고 쉬움
    - 실무 사용 권장
*/
public class CriteriaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member4> query = cb.createQuery(Member4.class);

            // 루트 클래스 (조회를 시작할 클래스)
            Root<Member4> m = query.from(Member4.class);

            // 쿼리 생성
//            CriteriaQuery<Member4> cq = query.select(m).where(cb.equal(m.get("name"), "kim"));
            CriteriaQuery<Member4> cq = query.select(m);

            // 동적 쿼리문 작성
            String username = "test";
            if (username != null) {
                cq = cq.where(cb.equal(m.get("name"), "kim"));
            }

            List<Member4> resultList = em.createQuery(cq)
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
