package hellojpa.query;

import hellojpa.domain.Member4;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

/*
    NativeSql 보다는 실무에서는 주로 JDBC를 직접 사용하거나 SpringJdbcTemplate 등을 사용
    - JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함께 사용 가능
    - 단, 영속성 컨텍스트를 적절한 시점에 강제로 플러시하는 것이 필요
    - 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트를 수동 플러시
*/
public class NativeSqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member4 member = new Member4();
            member.setName("member1");
            em.persist(member);

            // flush -> commit, createNativeQuery, createQuery가 실행될 때에도 자동으로 flush를 해줌
            List<Member4> resultList = em.createNativeQuery("select m.* from MEMBER4 m", Member4.class)
                    .getResultList();
            // 지금 위의 코드는 query문이 실행되므로 flush가 돼서 member의 저장이 db에도 반영이 되서 아래의 member1 조회의 값이 나오게 되는데
            // dbconn.excuteQuery("select * from member"); 이렇게 JPA를 사용하는 것이 아니라 직접 쿼리문을 실행시키게 되면 flush가 작동되지 않으므로
            // 아래의 코드인 member1을 조회하는 코드에서 위에 저장한 member는 결과로 나오지 않게 된다.
            // 그래서 이럴때에도 직접 쿼리문을 실행할 경우, JDBC 직접 사용하거나 스프링JdbcTemplate를 사용할 때에는 JPA를 작성한 뒤에 사용하게 되면 그전에
            // 적절히 수동으로 em.flush()를 해주면 됨

            for (Member4 member1 : resultList) {
                System.out.println("member1 = " + member1);
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
