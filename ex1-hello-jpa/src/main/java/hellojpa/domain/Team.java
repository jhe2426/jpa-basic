package hellojpa.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    /*
        객체의 양방향 관계
        - 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개이다.
        - 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
            A -> B (a.getB())
            B -> A (b.getA())
            class A {
                B b;
            }

            class B {
                A a;
            }

        테이블의 양방향 연관관계
        - 테이블은 외래 키 하나로 두 테이블의 연관관계를 관리
        - MEMBER.TEAM_ID 외래 키 하나로 양방향 연관관계를 가짐 (양쪽으로 조인할 수 있다.)
            SELECT +
            FROM MEMBER M
            JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

            SELECT *
            FROM TEAM T
            JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID

        객체의 양방향 관계를 구현하려면 두 개의 객체중 하나로 외래 키를 관리해야 한다.
            Member에서는 Team을 참조값으로 가지고 있고 Team에서는 Member들의 리스트형태로 참조값을 가지고 있으므로 이 둘 중에 어느 객체를 외래 키로
            설정을 할 것인가를 결정해야하는 것
            Member의 Team의 값이 변경이 됐을 때 테이블의 TEAM_ID(FK)를 변경해야하는지 아니면 Team에 있는 Member들의 값이 변경이 되면 TEAM_ID(FK)를
            변경해야하는지에 대한 기준이 필요함 이러한 기준으로 지정되는 것이 연관관계의 주인(Owner)임

        연관관계의 주인(Owner)
        양방향 매핑 규칙
        - 객체의 두 관계중 하나를 연관관계의 주인으로 지정
        - 연관관계의 주인만이 외래 키를 관리(등록, 수정)
        - 주인이 아닌쪽은 읽기만 가능
        - 주인은 mappedBy 속성을 사용하면 안 됨
        - 주인이 아니면 mappedBy 속성으로 주인을 지정해줘야 함

        그럼 객체의 양방향 관계일 때 어떤 객체를 주인으로 설정해야하냐?
        - 외래 키가 있는 곳을 주인으로 정해라
        - Member과 Team에서는 Member.team이 연관관계의 주인이다.
            데이터베이스 테이블에 Member 테이블에 TEAM_ID (FK)가 존재함으로 Member 객체가 주인이 되는 것
    */
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", members=" + members + // member.toString() 호출
                '}';
    }
}
