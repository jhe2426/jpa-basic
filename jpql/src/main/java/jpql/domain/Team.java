package jpql.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;

    private String name;


    /*
        @BatchSize(size = 100)
            지연 로딩으로 설정된 연관관계를 초기화할 때, 연관관계를 하나씩 조회하지 않고 여러 개의 미초기화 연관관계를 최대 100개 단위로 묶어서 한 번의
            SQL로 조회하도록 하는 Hibernate 기능이다. 주로 size는 1000개 미만으로 지정해서 사용한다.

            size = 100의 의미는 Member 엔티티를 최대 100개까지만 조회한다는 의미가 아니고
            한 번의 조회 쿼리에서 최대 100개의 미초기화 컬렉션을 조회한다는 의미로 최대 100개의 Team.members를 묶어서 초기화할 수 있다는 의미이다.

            예를 들어 현재 영속성 컨텍스트에 Team 엔티티가 150개 조회되어 있고, 각 Team의 members가 LAZY 상태라고 가정
            Team1.members -> 미초기화
            Team2.members -> 미초기화
            ...
            Team150.members -> 미초기화

            이 상태에서 Team1의 members에 처음 접근하면 team1.getMembers().size();
            BatchSize가 없다면 Team1의 members만 조회하는 SQL이 실행될 수 있다.
                select * from member where team_id = 1;

            그리고 Team2.members에 접근하면 이때 또 조회가 되고 Team3.members에 접근을 하면 이때 조회하는 식으로 N+1 문제가 발생할 수 있다.

            하지만 @BatchSize(size = 100)가 적용되어 있다면 Team1.members의 지연 로딩 초기화가 필요한 시점에, Hibernate는 현재 영속성 컨텍스트에서
            관리되고 있는 Team들 중 아직 members 컬렉션이 초기화되지 않은 대상들을 찾아 최대 BatchSize만큼 묶어서 조회한다.
            다음과 같은 형태로 조회를 함
                select * from member where team_id in (1, 2, 3, ... , 100);
                즉 한 번의 SQL로 Team1.members Team2.members ... Team100.members를 함께 초기화할 수 있다.
            중요한 점은 Team1 ~ Team100 각각에 속한 Member의 개수에는 size = 100이라는 제한이 적용되지 않는다는 것이다.
            Team1 -> Member 500명, Team2 -> Member 300명, Team3 -> Member 20명이라고 하더라도, 해당 Team들의 컬렉션이
            배치 조회 대상에 포함되어있다면 각각의 Team에 속한 Member 전체를 조회한다.
            따라서 size = 100은 Member를 100개 가져온다가 아니라 Team.members 컬렉션을 최대 100개 묶어서 초기화한다라는 뜻이다.

            또한 Team이 총 150개 존재한다고 해서 members에 처음 접근하는 순간 무조건 100개 + 50개로 조회 쿼리가 즉시 2번 실행되는 것은 아니다.
            예를 들어 처음 Team1.members에 접근하면 최대 100개의 미초기화 컬렉션이 함께 초기화될 수 있다.
                1차 조회
                Team1.members ~ Team100.members 초기화
            이 시점에서 Team101.members ~ Team150.members는 여전히 초기화되지 않은 상태이다.

            이후 Team101.members처럼 아직 초기화되지 않은 컬렉션에 실제로 접근하는 순간 다시 Batch Fetch가 발생하여
                select * from member where team_id in (101, 102, ..., 150); 처럼 나머지 컬렉션을 묶어서 조회할 수 있다.

            정리
            - @BatchSize(size = 100) = 지연 로딩된 연관관계를 초기화할 때 현재 영속성 컨텍스트에 존재하는 미초기화 연관관계들을 최대 100개 단위로
                묶어 IN 쿼리로 한 번에 조회한다.
            - size는 조회되는 자식 엔티티(Member)의 최대  개수가 아니라, 한 번에 배치 로딩할 미초기화 연관관계(컬렉션)의 최대 개수이다.
            - 또한 BatchSize는 모든 컬렉션을 처음부터 즉시 로딩하는 EAGER 기능이 아니다. 여전히 LAZY이며, 특정 컬렉션의 초기화가 실제로 필요한 시점에
                주변의 미초기화 컬렉션들을 함께 가져오는 최적화 기능이다.
    */
//    @BatchSize(size = 100) // 이렇게 해도 되고 실무에서는 주로 persistence.xml에서 글로벌 세팅해서 사용을 함
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
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
}
