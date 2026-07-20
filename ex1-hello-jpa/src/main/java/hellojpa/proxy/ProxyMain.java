package hellojpa.proxy;

import hellojpa.proxy.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.hibernate.Hibernate;

public class ProxyMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

         /*

            프록시 기초
            - em.find() vs em.getReference()
            - em.find(): 데이터베이스를 통해서 실제 엔티티 객체 조회
            - em.getReference(): 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회

            프록시 특징
            - 실제 클래스를 상속 받아서 만들어짐
            - 실제 클래스와 겉 모양이 같다.
            - 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨 (이론상)
            - 프록시 객체는 실제 객체의 참조(target)를 보관
            - 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메서드 호출
                target에 실제 객체 인스턴스 값이 저장되어 있는데 이 target.getName() 이렇게 호출해서 실제 객체의 메서드를 호출

            프록시 객체의 초기화
            - Member member = em.getReference(Member.class, "id1");
                member에는 실제 디비에서 조회한 엔티티 인스턴스 값이 저장되어 있는 것이 아닌 해당 엔티티를 상속받고 target은 null
                을 가진 형태로 존재
                id1는 식별자 값임 식별자 값은 해당 프록시가 알고 있으므로 getter을 호출하면 영속성 컨텍스트에 초기화 요청을 하지 않음
            - member.getName();
                1. getName()을 통해서 MemberProxy에 target 값이 존재하는지 확인
                    - 식별자 getter이면 영속성 컨텍스트에서 초기화 요청을 하지 않고 값을 가져오게 되므로 target은 여전히 null 값임
                2. null이면 영속성 컨텍스트에서 해당 엔티티 값을 초기화 요청
                3. 영속성 컨텍스트에 해당 엔티티가 존재하면 프록시가 해당 엔티티를 참조
                4. 영속성 컨텍스트에 해당 엔티티가 존재하지 않는다면 DB에서 조회
                    5. 조회 결과로 실제 Member 엔티티 생성
                    6. 실제 엔티티를 1차 캐시(영속성 컨텍스트)에 저장
                    7. 프록시가 실제 엔티티를 참조
                8. 실제 엔티티의 getName() 호출


            프록시의 중요한 특징
            - 프록시 객체는 처음 사용할 때 한 번만 초기화
            - 프록시 객체를 초기화할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
            - 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야함 (== 비교 실패, 대신 instance of 사용)
            - 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
            - 영속성 컨텍스트의 도음을 받을 수 없는 준영속 상태일 때(프록시가 준영속 상태일 때), 프록시를 초기화하면 문제 발생
                프록시가 준영속이 되어버리면 해당 프록시는 영속성 컨텍스트의 연결이 끊어지므로 초기화를 요청할 수 없으므로 문제가 발생
                (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림)
                ex)
                    Member findMember = em.getReference(Member.class, member.getId());
                    em.detach(findMember);
                    findMember.getName(); // LazyInitializationException 발생

                em.clear();, em.close()를 해도 예외가 발생됨
                    단, em.close()는 하이버네이트 버전에 따라 예외가 발생되는 것도 있고 아닌 경우도 있음


            프록시 확인
            - 프록시 인스턴스의 초기화 여부 확인
                PersistenceUnitUtil.isLoaded(Object entity)
            - 프록시 클래스 확인 방법
                entity.getClass().getName() 출력(..javasist.. or HibernateProxy..)
            - 프록시 강제 초기화
                org.hibernate.Hibernate.initialize(entity);
            - 참고: JPA 표준은 강제 초기화 없음
                강제 호출:member.getName()


        */
/*

        try {

            Member member = new Member();
            member.setName("hello");

            em.persist(member);

            em.flush();
            em.clear();


//            Member findMember = em.find(Member.class, member.getId());
            Member findMember = em.getReference(Member.class, member.getId());
            System.out.println("findMember = " + findMember.getClass());
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
        // 프록시 객체 타입 비교
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Member member2 = new Member();
            member1.setName("member2");
            em.persist(member2);

            em.flush();
            em.clear();
*/
/*
            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.find(Member.class, member2.getId());
            System.out.println("m1 == m2: " + (m1.getClass() == m2.getClass())); // true
*//*


            Member m1 = em.find(Member.class, member1.getId());
            Member m3 = em.getReference(Member.class, member2.getId());
            System.out.println("m1 == m3: " + (m1.getClass() == m3.getClass())); // false
            System.out.println("m3 instanceof Member: " + (m3 instanceof Member)); // true

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*

        // 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
        */
/*
            왜 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티를 반환할까?
            1. 이미 영속성 컨텍스트에 해당 엔티티가 존재하는데 굳이 프록시로 가져와봐야 이점이 하나도 존재하지 않음
                지연 로딩을 위해서 프록시를 사용하는 것인데 이미 영속성 컨텍스테에 해당 엔티티가 조회되어 저장되어 있는데 지연로딩이 무슨 의미가 있나

            2. 영속성 컨텍스트는 같은 엔티티에 대해 하나의 인스턴스를 유지해야 하므로 기존 엔티티를 반환하는 것

        *//*

        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            System.out.println("m1.getClass() = " + m1.getClass()); // class hellojpa.proxy.domain.Member

            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference.getClass() = " + reference.getClass()); // class hellojpa.proxy.domain.Member

            System.out.println("m1 == reference: " + (m1 == reference));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*

        // 영속성 컨텍스트에 찾는 엔티티가 이미 프록시 객체로 존재하면 em.find()를 호출해도 실제 엔티티가 아닌 프록시 객체를 반환
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();
*/
/*
            Member m1 = em.getReference(Member.class, member1.getId());
            System.out.println("m1.getClass() = " + m1.getClass());

            // m1와 같은 엔티티와 식별자 값이 같으니 같은 인스턴스를 반환해줌
            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference.getClass() = " + reference.getClass());

            System.out.println("m1 == reference: " + (m1 == reference));
*//*

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("m1.getClass() = " + refMember.getClass());

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("reference.getClass() = " + findMember.getClass());

            System.out.println("refMember == findMember: " + (refMember == findMember)); // true 반환
            */
/*
                refMember == findMember가 어떻게 true일까?
                - JPA에서는 하나의 영속성 컨텍스트 안에서 같은 엔티티 타입과 기본 키(같은 식별자)에 해당하는 관리 인스턴스는 하나만 존재해야한다고 정의
                - 그래서 find() 메서드를 사용해도 무조건 새로운 실제 엔티티를 생성하지 않음
                    먼저 영속성 컨텍스트에 같은 식별자의 객체가 있는지 확인하고, 있으면 프록시 객체에 초기화가 되어있지 않으면
                        초기화된 기존 프록시 객체 자체를 반환해줌
                    따라서 먼저 getReference()로 해당 엔티티에 같은 식별자가 프록시로 생성되어 있으므로
                    findMember도 엔티티를 반환받는 것이 아닌 refMember와 같은 프록시에서 초기화가 된 프록시 객체를 반환받게 됨

                따라서 getReference()를 먼저 호출하면 프록시가 해당 엔티티를 대표하는 유일한 관리 객체가 된다.
                이후 find()는 같은 식별자의 새 실제 객체를 만들지 않고, 기존 프록시를 초기화한 뒤 그대로 반환한다.
                이것은 같은 영속성 컨텍스트에서 엔티티의 객체 동일성을 보장하고 변경 감지의 일관성을 유지하기 위해서이다.
            *//*


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // 영속성 컨텍스트의 도음을 받을 수 없는 준영속 상태일 때
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("m1.getClass() = " + refMember.getClass());

//            em.detach(refMember);
            em.clear();

*/
/*
            Hibernate 5.4.1.Final 버전부터는 em.close()가 되어도 트랜잭션이 종료되지 않았다면 프록시 초기화가 가능하게 되어 예외가 발생되지 않음
            em.close();
*//*


            refMember.getName();
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/

        // 프록시 확인
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("m1.getClass() = " + refMember.getClass());
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
//            refMember.getName(); // 강제 초기화
            // 강제로 호출하는 또 다른 방법
            Hibernate.initialize(refMember); // 강제초기화
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));

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
