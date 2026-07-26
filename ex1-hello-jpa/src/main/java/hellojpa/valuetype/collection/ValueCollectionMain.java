package hellojpa.valuetype.collection;

import hellojpa.valuetype.domain.embedded.Address;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;

/*
    값 타입 컬렉션의 제약사항
    - 값 타입은 엔티티와 다르게 식별자 개념이 없다.
    - 값은 변경하면 추적이 어렵다.
    - 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고,
        값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
    - 값 타입 컬렉션을 매핑하는 테이블은 모두 컬럼을 묶어서 기본키를 구성해야 함: null 입력 X, 중복 저장 X
        값 타입 컬렉션을 매핑하는 테이블은 개념적으로 주인 엔티티의 FK와 값 타입 컬럼들을 묶어서 행을 식별한다.
        따라서 null 입력과 중복 저장에 제약이 생긴다.

    - 즉, 모든 컬럼을 묶어서 식별한다는 말은 별도 식별자가 없기 때문에 값 전체를 기준으로 구분해야 한다는 의미

    - 그래서 값 타입 컬렉션은 단순하고 변경이 적은 값에 사용하는 것이 좋고, 실무에서 개별 수정, 삭제, 추적이 필요하면
        값 타입 컬렉션 대신 별도 엔티티로 분리하는 것이 좋다.

    값 타입 컬렉션 대안
    - 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
    - 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
    - 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션처럼 사용

    엔티티 타입의 특징
    - 식별자 O
    - 생명 주기 관리
    - 공유

    값 타입의 특징
    - 식별자 X
    - 생명 주기를 엔티티에 의존
    - 공유하지 않는 것이 안전 (공유를 해야할 경우 복사해서 사용)
    - 불변 객체로 만드는 것이 안전

    - 값 타입은 정말 값 타입이라 판단될 때만 사용
    - 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안 됨
    - 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티
*/
public class ValueCollectionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

/*
        // 값 타입 컬렉션 사용
        // 참고: 값 타입 컬렉션은 영속성 전이(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있음
        try {

            // 값 타입 컬렉션 저장
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            // 값 타입 컬렉션 조회
            *//*
                값 타입 컬렉션도 지연 로딩 전략을 사용함

                단일 값 타입과 값 타입 컬렉션은 조회 방식이 다르다.

                @Embedded 같은 단일 값 타입은 엔티티 테이블의 컬럼으로 함께 저장되므로 Member를 조회할 때 같이 조회된다.

                반면 @ElementCollection으로 매핑한 값 타입 컬렉션은 별도의 컬렉션 테이블에 저장되므로 기본적으로 지연 로딩된다.

                직접 new Member()로 Member 인스턴스를 만들 때는 favoriteFoods 필드가 개발자가 초기화한 HashSet을 참조함
                비영속 상태의 Member에서는 일반 자바 컬렉션인 HashSet을 사용한다.

                이후 Member가 영속성 컨텍스트에서 관리되기 시작하면 Hibernate는 값 타입 컬렉션의 지연 로딩과 변경 감지를 위해
                PersistentSet 같은 영속성 컬렉션 래퍼로 컬렉션을 관리한다.

                Member를 DB에서 조회할 때는 Member 엔티티의 기본 필드만 먼저 조회하고,
                favoriteFoods 같은 값 타입 컬렉션의 실제 데이터는 즉시 조회하지 않는다.

                대신 컬렉션 필드에는 Hibernate가 만든 PersistenSet 같은 영속성 컬렉션 래퍼가 들어간다.

                이 영속성 컬렉션은 실제 데이터가 아직 로딩되지 않았는지, 컬렉션에 요소가 추가되거나 삭제되었는지,
                어떤 영속성 컨텍스트와 연결되어 있는지를 관리한다.

                이후 favoriteFoods.size(), 반복문, add/remove 등으로 컬렉션을 실제 사용할 때
                아직 초기화되지 않은 상태라면 Hibernate가 별도의 SQL을 실행하여 값 타입 컬렉션 테이블에서 데이터를 조회하고 초기화한다.

                초기화된 이후에도 PersistentSet은 계속 유지되며, 요소 추가, 삭제 같은 변경 사항을 추적해서 flush 시점에 DB에 반영한다.

                정리하면, 비영속 상테에서는 HashSet, Hibernate가 관리하는 영속 상태에서는 PersistentSet 같은 컬렉션 래퍼가 사용된다.
            *//*
            System.out.println("=========================== START ===========================");
            Member findMember = em.find(Member.class, member.getId());

            List<Address> addressHistory = findMember.getAddressHistory();
            for (Address address : addressHistory) {
                System.out.println("address.getCity() = " + address.getCity());
            }

            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/

        // 값 타입 컬렉션 수정
        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("=========================== START ===========================");
            Member findMember = em.find(Member.class, member.getId());

            /*
                값 타입은 엔티티처럼 식별자를 가지는 독립 객체가 아니라, 엔티티에 포함되어 함께 저장되는 값이다.

                DB에서 여러 엔티티를 조회했을 때 값 타입의 값이 같더라도 Hibernate가 값 타입 인스턴스를 하나로 합쳐 공유하지 않는다.
                보통 각 엔티티는 자신의 값 타입 인스턴스를 가진다.

                하지만 개발자가 같은 값 타입 인스턴스를 여러 엔티티에 직접 넣으면 자바 참조 공유가 발생할 수 있다.

                값 타입은 불변 객체로 설계하는 것이 좋다.
                값 타입은 엔티티처럼 식별자로 구분되는 객체가 아니라 값 자체가 의미 있는 객체이다.

                만약 값 타입이 변경 가능하면 여러 엔티티가 같은 값 타입 인스턴스를 공유하고 있을 때, 한 엔티티에서 값을 수정 했는데
                다른 엔티티의 값까지 함께 변경되는 부작용이 발생할 수 있다.

                따라서 setter를 만들지 않고 생성자로만 값을 설정한다.
                값 변경이 필요하면 기존 값을 수정하지 않고 새로운 값 타입 객체로 교체한다.
            */
            // 값 타입 수정
            Address oldAddress = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", oldAddress.getStreet(), oldAddress.getZipcode()));

            // 값 타입 컬렉션 수정
            // 치킨 -> 한식
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            /*
                remove()는 같은 인스턴스인지가 아니라 equals()로 같은 값인지 비교해 제거한다.
                따라서 값 타입 컬렉션에서 new Address(...)로 제거하려면 Address의 equals()와
                hashCode()가 값 기준으로 재정의되어 있어야 한다.
            */
            findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
            findMember.getAddressHistory().add(new Address("nweCity1", "street", "10000"));

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
