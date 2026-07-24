package hellojpa.valuetype;


import hellojpa.valuetype.domain.Member;
import hellojpa.valuetype.domain.embedded.Address;
import hellojpa.valuetype.domain.embedded.Period;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/*
    값 타입 분류
    - 기본값 타입
        - 자바 기본 타입 (int, double)
        - 래퍼 클래스 (Integer, Long)
        - String

    - 임베디드 타입 (embedded type, 복합 값 타입)
        - 새로운 값 타입을 직접 정의할 수 있음
        - JPA는 임베디드 타입이라고 함
        - 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함
        - int, String과 같은 값 타입
        - 사용법
            @Embeddable: 값 타입을 정의하는 곳에 표시
            @Embedded: 값 타입을 사용하는 곳에 표시
            기본 생성자 필수
        - 장점
            재사용
            높은 응집도
            Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메서드를 만들 수 있음
            임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함

        - 임베디드 타입과 테이블 매핑
            임베디드 타입은 엔티티의 값일 뿐이다.
            임베디드 타입을 사용하기 전과 후에 매핑하는 테이블은 같다.
            객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능
                기존 테이블의 컬럼 구조는 그대로 유지하면서, 객체에서는 관련된 값들을 의미 있는 객체로 묶어
                객체지향적으로 사용할 수 있도록 매핑해준다.
            잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음


    - 컬렉션 값 타입 (collection value type)
*/
public class ValueTypeMain {

    public static void main(String[] args) {

/*
        // 자바의 기본 타입은 값을 절대 공유하지 않고 항상 값을 복사함
        int a = 10;
        int b = a;

        a = 20;

        System.out.println("a = " + a);
        System.out.println("b = " + b);

        *//*
            Integer같은 래퍼 클래스나 String 같은 특수 클래스는 공유 가능한 객체
            Integer 같은 래퍼 클래스와 String은 불변 객체이다.
            여러 변수가 같은 객체를 참조하더라도 객체 내부의 값을 변경할 수 없으므로
            공유 참조로 인한 사이드 이펙트가 발생하지 않는다.
            값을 변경하는 것처럼 보이는 연산은 기존 객체를 수정하는 것이 아니라
            새로운 객체를 참조하도록 변수를 재할당하는 것이다.
        *//*
        Integer c = 10;
        Integer d = c;

        System.out.println("c = " + c);
        System.out.println("d = " + d);
*/

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

/*
        try {

            Member member = new Member();
            member.setUsername("hello");
            member.setHomeAddress(new Address("city", "street", "10000"));
            member.setWorkPeriod(new Period());

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/
/*

        */
/*
            임베디드 타입의 같은 값을 가지는 타입을 여러 엔티티에서 공유하면 위허함
            공유하는 임베디드 타입을 수정하게 되면 공유되는 모든 엔티티에 변경된 값으로 저장이 되므로
            이러한 부작용(side effect)이 발생
            따라서 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험
            대신 값(인스턴스)를 복사해서 사용

        *//*

        try {
            Address address = new Address("city", "street", "10000");

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setHomeAddress(address);
            em.persist(member2);

            member.getHomeAddress().setCity("newCity"); // member, member2의 모든 엔티티에서 값이 변경됨

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/
/*

        */
/*
           인스턴스를 공유하지 않고 값(인스턴스)을 복사해서 사용

            객체 타입의 한계
            - 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
            - 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입이다.
            - 자바 기본 타입은 값을 대입하면 값을 복사한다.
            - 반면, 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.
            - 객체의 공유 참조는 피할 수 없다.

            이를 해결하고자 불변 객체로 만들면 됨
            불변 객체
            - 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단
            - 값 타입은 불변 객체(immutable object)로 설계해야함
            - 불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체
            - 생정자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 됨
                또는 해당 값 타입 내부에서만 변경할 수 있게 Setter 메서드의 접근 제어자를 private으로 선언하면 됨
            - 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체
        *//*

        try {
            Address address = new Address("city", "street", "10000");

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
            em.persist(member);

            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setHomeAddress(copyAddress);
            em.persist(member2);

            member.getHomeAddress().setCity("newCity");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/

        // 불변 객체인 값 타입의 값을 변경하고 싶을 때 어떻게 해야하나?
        try {
            Address address = new Address("city", "street", "10000");

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
            em.persist(member);

            Address newAddress = new Address("newCity", address.getStreet(), address.getZipcode());

            member.setHomeAddress(newAddress);

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
