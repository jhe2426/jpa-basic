package hellojpa.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/*
    다대다 매핑의 한계
    - 편리해 보이지만 실무에서 사용X
    - 연결 테이블이 단순히 연결만 하고 끝나지 않음
    - 연결 테이블에 주문시간, 수량 같은 데이터가 들어올 수 있음

    다대다 한계 극복
    - 연결 테이블용 엔티티 추가 (연결 테이블을 엔티티로 승격)
    - @ManyToMany -> @OneToMany, @ManyToOne
*/
@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;

//    @ManyToMany(mappedBy = "products")
//    private List<Member4> members = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<MemberProduct> memberProducts = new ArrayList<>();

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
}
