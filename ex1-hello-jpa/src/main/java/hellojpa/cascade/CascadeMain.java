package hellojpa.cascade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


/*
    영속성 전이: CASCADE
    - 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때 사용
    - 예: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장

    * 주의
    - 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
    - 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐

    종류
    - ALL: 모두 적용
    - PERSIST: 영속
        부모를 저장할 때 연관된 새로운 자식 엔티티도 함께 저장
    - REMOVE: 삭제
        부모를 삭제할 때 연관된 자식도 함께 삭제
    - MERGE: 병합
        준영속 상태의 부모를 병합할 때 연관된 자식도 함께 병합
    - REFRESH: REFRESH
        부모를 DB의 최신 값으로 다시 조회할 때 자식도 함께 다시 조회
    - DETACH: DETACH
        부모를 영속성 컨텍스트에서 분리할 때 자식도 함께 분리

    고아 객체
    - 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
    - orphanRemoval = true
    - Parent parent1 = em.find(Parent.class, id);
        parent1.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
    - DELETE FROM CHILD WHERE ID = ?

    * 주의
    - 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
    - 참조하는 곳이 하나일 때 사용해야함
    - 특정 엔티티가 개인 소유할 때 사용
    - @OneToOne, @OneToMany만 가능
    - 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면,
        부모를 제거할 때 자식도 함께 제거된다. 이것은 CascadeType.REMOVE처럼 동작한다.


     영속성 전이 + 고아 객체, 생명주기
     - CascadeType.ALL + orphanRemoval=true를 함께 설정하면 부모 엔티티를 중심으로 자식 엔티티의 생명주기를 관리할 수 있다.
     - 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
     - 두 옵션을 모두 활성화하면 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있음
     - 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용
        Aggregate Root는 관련 객체의 묶음의 대표 엔티티이며, 외부에서는 Root를 통해서만 내부 객체를 변경한다.
        내부 객체의 생명주기까지 Root가 책임진다면 JPA 영속성 전이를 사용해 Root의 저장, 삭제 작업을 내부 객체에 함께 전달할 수 있다.

*/
public class CascadeMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
/*
        // 영속성 전이를 사용하지 않을 때
        try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            em.persist(child1);
            em.persist(child2);


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // 영속성 전이를 사용할 때
        try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

/*
        // 고아 객체
        try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
*/

        // @OneToMany(mappedBy = "parent", orphanRemoval = true) orphanRemoval 설정으로 고아 객체 삭제되는 예제
        try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            em.remove(findParent);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
