package hellojpa.association.domain;

import jakarta.persistence.*;

/*
    프록시와 즉시 로딩 주의
    - 가급적 지연 로딩만 사용 (특히 실무에서)
    - 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
    - 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
    - @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정
    - @OneToMany, @ManyToMany는 기본이 지연 로딩

    지연 로딩 활용 - 실무
    - 모든 연관관계에 지연 로딩을 사용해라
    - 실무에서 즉시 로딩을 사용하지 마라
    - JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라
    - 즉시 로딩은 상상하지 못한 쿼리가 나간다.
*/
//@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    // 지연 로딩 LAZY을 사용해서 프록시로 조회
//    @ManyToOne(fetch = FetchType.LAZY)

    /*
       즉시 로딩 EAGER를 사용해서 함께 조회
       Member 조회 시 항상 Team도 조회
       JPA 구현체는 가능하면 조인을 사용해서 SQL 한 번에 함께 조회
    */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;


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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
