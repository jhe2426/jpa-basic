package hellojpa.inheritance;

import jakarta.persistence.*;

@Entity


/*
    상속관계 매핑
     - 관계형 데이터베이스는 상속 관계가 없음
     - 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사
     - 상속관계 매핑: 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑
*/

/*
    단일 테이블 전략
    - 장점
        조인이 필요 없으므로 일반적으로 조회 성능이 빠름
        조회 쿼리가 단순함

    - 단점
        자식 엔티티가 매핑한 컬럼은 모두 null 허용
        단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있고, 상황에 따라서 조회 성능이 오히려 느려질 수 있다.

*/
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

/*
    조인 전략
    - 장점
        테이블 정규화
        외래 키 참조 무결성 제약조건 활용가능
        저장공간 효율화
    - 단점
        조회시 조인을 많이 사용, 성능 저하
        조회 쿼리가 복잡함
        테이터 저장 시 INSERT SQL을 2번 호출
*/
//@Inheritance(strategy = InheritanceType.JOINED)

/*
    구현 클래스마다 테이블 생성 전략
        자식 엔티티 클래스마다 개별 테이블을 생성하고, 각 자식 테이블에 부모 클래스에서 상속받은 필드까지 함께 저장하는 전략
        이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천X

    - 장점
        서브 타입을 명확하게 구분해서 처리할 때 효과적
        not null 제약조건 사용 가능

    - 단점
        여러 자식 테이블을 함께 조회할 때 성능이 느림 (UNION SQL)
        자식 테이블을 통합해서 쿼리하기 어려움

*/
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn // JPA 상속관계 매핑에서 부모 테이블의 각 행이 어떤 자식 엔티티 타입인지 구분하는 컬럼을 지정하는 애노테이션
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
