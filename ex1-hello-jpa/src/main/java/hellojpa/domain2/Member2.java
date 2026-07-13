package hellojpa.domain2;

import jakarta.persistence.*;

@Entity
public class Member2 {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    /*
        이런 매핑은 공식적으로 존재X
        @JoinColumn(insertable=false, updatable=false)
        읽기 전용 필드를 사용해서 양방향처럼 사용하는 방법
        하지만 이러한 방법을 사용하지 말고 다대일 양방향을 사용하자
    */
    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    private Team2 team;

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
