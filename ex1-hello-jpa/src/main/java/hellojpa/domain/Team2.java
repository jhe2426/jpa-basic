package hellojpa.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/*
    * 다대일, 일대다 이렇게 연관관계를 가질때 앞에오는 글자가 해당 연관관계의 주인임을 나타낸다. ex) 다대일이면 다쪽이 해당 연관관계의 주인임
    일대다 단방향 (실무에서는 이 연관관계를 권장하지 않고 거의 사용되어지지 않음)
    - 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인
    - 테이블 일대다 관계는 항상 다(N) 쪽에 외래 키가 있음
    - 객체와 테이블의 차이 때문에 반대편 테이블(1)의 외래 키를 관리하는 특이한 구조
    - @JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가함)

    - 일대다 단방향 매핑의 단점
        - 엔티티가 관리하는 외래 키가 다른 테이블에 있음
        - 연관관계 관리를 위해 추가로 UPDATE SQL 실행

    - 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자
*/
//@Entity
public class Team2 {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    private List<Member2> members = new ArrayList<>();

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

    public List<Member2> getMembers() {
        return members;
    }

    public void setMembers(List<Member2> members) {
        this.members = members;
    }
}
