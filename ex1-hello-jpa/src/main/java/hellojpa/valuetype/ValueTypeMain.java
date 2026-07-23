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

        emf.close();
    }

}
