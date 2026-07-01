package hellojpa;

import jakarta.persistence.*;

import java.util.List;

/*
    EntityManagerFactory는 사용자의 요청이 올 때마다 EntityManager를 생성하고 EntityManager는 데이터베이스의 커넥션을 사용하여
    디비를 사용함

    영속성 컨텍스트
    - 엔티티를 영구 저장하는 환경이라는 의미
    - EntityManager.persist(member)는 디비에 데이터를 저장한다는 의미가 아니라 영속성 컨텍스트를 통해서 해당 데이터인 엔티티를 영속한다는 의미임
        persist() 메서드는 디비에 데이터를 저장하는 것이 아닌 영속성 컨텍스에 저장한다.
    - 영속성 컨텍스트는 논리적인 개념이며 눈에 보이지 않다.
    - 엔티티 매니저를 통해서 영속성 컨텍스에 접근할 수 있다.

    엔티티의 생명주기
    - 비영속: 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
        객체를 생성한 상태
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");

    - 영속: 영속성 컨텍스트에 관리되는 상태
        persist(엔티티)가 실행되게 되면 해당 엔티티의 영속 상태가 됨
            em.persist(member);

    - 준영속: 영속성 컨텍스트에 저장되었다가 분리된 상태
        아래와 같이 detach()메서드를 사용해서 회원 엔티티를 영속성 컨텍스트에서 분리를 준영속 상태라고 함
            em.detach(member);

    - 삭제: 삭제된 상태
        em.remove(member);
            영속 상태의 엔티티에 em.remove()를 호출하면 해당 엔티티는 삭제 상태로 전이된다.
            이 상태에서는 즉시 데이터베이스에서 삭제되는 것이 아니라, 트랜잭션이 커밋되는 시점에
            실제로 DELETE SQL이 실행되어 데이터베이스에서 제거된다.
*/
public class JpaMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 하나만 생성해서 애플리케이션 전체에서 공유되어 진다.
        // EntityManager는 사용자의 요청이 올 때마다 생성되었다가 사용이 끝나면 닫는 동작이 이루어짐
            // 그래서 EntityManager는 쓰레드간에 공유하면 안 됨(사용하고 버려야 함)
            // 쓰레드: 쓰레드(Thread)는 프로그램의 코드를 실행하는 작업 단위이다.
                // 웹 애플리케이션에서는 같은 메서드라도 사용자의 요청마다 별도의 스레드가 해당 메서드를 실행한다.
                // 따라서 EntityManager는 여러 스레드가 동시에 공유해서 사용하면 안 된다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        /*
            Member member = new Member();

            member.setId(1L);
            member.setName("HelloA");

            em.persist(member);
            이렇게 JPA를 사용해서 데이터를 데이터베이스에 저장하려고 해도 정상적으로 저장이 안 됨
            JPA에서는 데이터를 변경하는 모든 작업(INSERT, UPDATE, DELETE)은 반드시 트랜잭션(Transaction) 안에서 수행되어야 정상적으로 저장이 됨

            왜 트랜잭션이 필요할까?
                persist()는 엔티티를 영속성 컨텍스트(Persistence Context)에 등록하는 작업
                실제로 SQL이 데이터베이스에 반영되는 시점은 보통 아래의 메서드가 호출 되었을 때에만 반영이 됨
                    flush()
                    commit()
                그런데 commit()은 진행 중인 트랜잭션이 있을 때만 호출할 수 있습니다.
        */

        EntityTransaction tx = em.getTransaction();
        tx.begin();

/*
        // Member 데이터 저장
        try {
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");

            em.persist(member);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // Member 데이터 조회
        try {
            Member findMember = em.find(Member.class, 1l);
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // Member 데이터 삭제
        try {
            Member findMember = em.find(Member.class, 1l);
            em.remove(findMember);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // Member 데이터 수정
        try {
            // JPA를 통해서 데이터베이스의 데이터를 가져오게 되면 그때부터 이 가져온 데이터를 JPA가 관리를 하게 됨
            // 그래서 해당 데이터가 변경이 됐는지 안 됐는지를 트랜잭션이 commit되는 시점에 주시를 하고 있게 됨
            // 그래서 commit하기 직전에 해당 데이터의 변경이 발생된 것을 알아차리고 자동으로 update 쿼리문을 날려서 수정해줌
            Member findMember = em.find(Member.class, 1l);
            findMember.setName("HelloJPA");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/
        
/*      JPQL은 엔티티 객체를 대상으로 쿼리문을 작성하는 방법
            - 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
            - SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음
            - JPQL을 한마디로 정의하면 객체 지향 SQL
*/
/*        try {
//            Member findMember = em.find(Member.class, 1l);
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.getName() = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }*/

        try {
            // Member 엔티티는 비영속 상태
            Member member = new Member();
            member.setId(100L);
            member.setName("HelloJPA");

            // 영속 상태
            System.out.println("=== BEFORE ===");
            em.persist(member);
            System.out.println("=== AFTER ===");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
