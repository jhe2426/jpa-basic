package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;

import java.util.List;

/*
    페이징 API
    - JPA는 페이징을 다음 두 API로 추상화
    - setFirstResult(int startPosition): 조회 사작 위치 (0부터 시작) -> SQL의 OFFSET
        OFFSET은 몇 번째 데이터부터 가져올지를 정하기 위해 앞의 데이터를 건너뛰는 값
    - setMaxResults(int maxResult): 조회할 데이터 수 -> SQL의 LIMIT
*/
public class PagingMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result = " + result.size());
            for (Member member1 : result) {
                System.out.println("member = " + member1);
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
