package hellojpa.domain;

import jakarta.persistence.*;

//@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

    @ManyToOne
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

    // 메서드명에 set이 들어가면 자바의 관례처럼 setXXX일 때 XXX의 값만 변경되는 것으로 이름만 봤을 때 파악을 하므로
    // 아래와 같이 다른 변경 로직이 포함되면서 값이 변경되면 changeTeam으로 메서드명을 변경해서 사용함
/*
    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
*/

/*
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
*/

    // 이렇게 toString()이 양방향 객체에 존재하게 되면 무한으로 양쪽의 toString()을 호출하게 되는 문제가 발생
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", team=" + team + // team = team.toString()을 호출
                '}';
    }
}
