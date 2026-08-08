package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.domain.Member;
import jpql.domain.Team;
import jpql.domain.type.MemberType;

import java.util.List;

/*
    페치 조인(fetch join)
    - SQL 조인 종류가 아님
    - JPQL에서 성능 최적화를 위해 제공하는 기능
    - 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
    - join fetch 명령어 사용
    - 페치 조인 = [LEFT | [OUTER] | INNER] JOIN FETCH 조인경로

    엔티티 페치 조인
    - 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에)
    - SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
    - [JPQL]
        select m from Member m join fetch m.team
    - [SQL]
        SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID = T.ID

    컬렉션 페치 조인
    - 일대다 관계, 컬렉션 페치 조인
    - [JPQL]
        select t from Team t join fetch t.members where t.name = '팀A'
    - [SQL]
        SELECT T.*, M.* FROM TEAM T INNER JOIN MEMBER M ON T.ID = M.TEAM_ID WHERE T.NAME = '팀A'

    페치 조인과 DISTINCT
    - SQL의 DISTINCT는 중복된 결과를 제거하는 명령
    - JPQL의 DISTINCT는 2가지 기능을 제공
        1. SQL에 DISTINCT를 추가
        2. 애플리케이션에서 엔티티 중복 제거
            같은 식별자를 가진 엔티티를 제거해줌
    - select distinct t from Team t join fetch t.members
        [JPQL]
            select distinct t from Team t join fetch t.members where t.name = '팀A'
                SQL에 DISTINCT를 추가하지만 행 데이터 전부가 다 같은 행은 존재하지 않으므로 SQL 결과에서는 중복 제거 일어나지 않음
                    1 팀A 1 1 회원1
                    1 팀A 2 1 회원2
                    2 팀B 3 2 회원3

    페치 조인과 일반 조인의 차이
    - 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음
    - 일반 조인
        - [JPQL]
            select t from Team t join t.members m where t.name = '팀A'
        - [SQL]
            SELECT T.* FROM TEAM T INNER JOIN MEMBER M ON T.ID = M.TEAM_ID WHERE T.NAME = '팀A'
    - 페치 조인
        - [JPQL]
            select t from Team t join fetch t.members m where t.name = '팀A'
        - [SQL]
            SELECT T.*, M.* FROM TEAM T INNER JOIN MEMBER M ON T.ID = M.TEAM_ID WHERE T.NAME = '팀A'
    - 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
    - 페치 조인은 객체 그래프를 SQL 한 번에 조회하는 개념
*/
public class FetchJoinMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
/*
        // 엔티티 페치 조인
        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("팀C");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(10);
            member3.setType(MemberType.ADMIN);
            member3.changeTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            member4.setAge(10);
            member4.setType(MemberType.ADMIN);
            em.persist(member4);

            em.flush();
            em.clear();


           */
/*
                String query = "select m from Member m"; 해당 쿼리문을 사용하게 되면 아래와 같이 여러 쿼리문이 나가게 됨
                Team은 지연로딩으로 연관관계가 설정되어 있음
                그래서 해당 값이 초기화가 필요할 때 SQL문을 날려 영속성 컨텍스(1차 캐시)에 저장을 하게 됨
                회원1, 팀A(SQL문을 날려 조회)
                회원2, 팀A(1차 캐시)
                회원3, 팀B(SQL문을 날려 조회)

                위와 같은 경우를 N + 1문제가 발생된 것이다.

                N + 1문제
                    하나의 쿼리로 N개의 엔티티를 조회한 뒤, 각 엔티티의 연관 엔티티에 접근하면서 추가 쿼리가 반복적으로 실행되는 문제이다.
                    예) Member(N) : Team(1) 연관관계에서 Member.team이 LAZY인 경우
                        1. "select m from Member m" 실행 → 회원 목록을 조회하는 SQL 1번 실행

                        2. 조회된 각 회원에 대해 member.getTeam().getName() 호출 → Team이 아직 조회되지 않았다면 Team을 조회하는 추가 SQL 실행

                     따라서 회원이 N명이라면 최초 회원 조회 1번 + 연관된 Team 조회 최대 N번 → 총 최대 N + 1번의 SQL이 실행될 수 있다.

                     이를 N+1 문제라고 한다.

                     해결 방법의 대표적인 예:
                     Fetch Join을 사용하여 Member와 Team을 한 번의 SQL로 함께 조회한다.

                EAGER(즉시 로딩) 주의

                EAGER는 연관 엔티티를 즉시 로딩한다는 의미이지, 반드시 SQL JOIN을 사용하여 한 번에 조회한다는 의미는 아니다.

                특히 JPQL에서 select m from Member m 과 같이 Member만 조회하면 JPQL 자체에는 Team JOIN이 없기 때문에
                Hibernate가 Member 조회 후 EAGER로 설정된 Team을 추가 SELECT로 조회할 수 있다.

                이 경우 Member 목록 조회 1번 + 각 Member의 Team 조회 N번 = N + 1 문제가 발생할 수 있다.
                따라서 EAGER로 설정한다고 N+1 문제가 해결되는 것은 아니다.
                연관 엔티티를 한 번의 SQL로 함께 조회하려면 select m from Member m join fetch m.team
                과 같이 Fetch Join을 명시적으로 사용할 수 있다.

                em.find():
                    em.find(Member.class, memberId) 와 같이 식별자로 단일 엔티티를 조회하는 경우, Hibernate는 EAGER 연관관계를 JOIN하여
                    Member와 Team을 한 번의 SQL로 조회하는 경우가 일반적이다.

                    따라서 em.find()에서 EAGER 연관관계가 JOIN으로 조회되는 것과 JPQL에서 EAGER 연관관계가 항상 JOIN으로 조회되는 것은
                    같은 개념으로 보면 안 된다.

                LAZY + JPQL → 처음에는 Member만 조회, 이후 연관된 Team에 접근할 때 추가 쿼리가 반복되면 N+1 발생
                EAGER + JPQL → JPQL에 Fetch Join이 없다면 Team는 즉시 로딩해야 하므로 Hibernate가 추가 SELECT를 발생시켜 N+1이 발생할 수 있음
                Fetch Join → Member와 Team을 한 SQL로 조회하므로 해당 연관관계의 N+1 방지
                em.find() + EAGER → Hibernate에서는 보통 JOIN을 사용해 한 번에 조회하는 모습을 볼 수 있음
            *//*

            String query = "select m from Member m join fetch m.team";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member member : result) {
                if (member.getTeam() != null) {
                    System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                }
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/

/*
        // 컬렉션 페치 조인
        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("팀C");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(10);
            member3.setType(MemberType.ADMIN);
            member3.changeTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            member4.setAge(10);
            member4.setType(MemberType.ADMIN);
            em.persist(member4);

            em.flush();
            em.clear();


            String query = "select distinct t from Team t join fetch t.members";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();

            for (Team team : result) {
                *//*
                    String query = "select t from Team t join fetch t.members"; 이 쿼리문을 실행했을 시
                    하이버네이트 버전이 5이하이면 아래의 결과물이 아래와 같이 3번 나오게 됨
                        team = 팀A | 2
                        team = 팀A | 2
                        team = 팀B | 1

                    하이버네이트6 변경 사항
                    - DISTINCT가 추가로 애플리케이션에서 중복 제거 시도
                        -> 하이버네이트6 부터는 DISTINCT 명령어를 사용하지 않아도 애플리케이션에서 중복 제거가 자동으로 적용

                    컬렉션 Fetch Join과 중복

                    Team A에 회원1, 회원2가 존재하고 Team B에 회원3이 존재하는 경우
                        select t from Team t join fetch t.members SQL의 JOIN 결과는
                            TeamA - 회원1
                            TeamA - 회원2
                            TeamB - 회원3
                    총 3개의 행이 만들어진다. Hibernate 5에서는 이러한 JOIN 결과의 영향으로 조회 결과 List에도 TeamA가 중복되어
                    TeamA
                    TeamA
                    TeamB 처럼 반환될 수 있었다.

                    따라서 과거에는 select distinct t from Team t join fetch t.members 와 같이 distinct를 사용하여 중복을 제거했다.

                    하지만 Hibernate 6부터는 컬렉션 Fetch Join으로 인해 발생한 동일한 부모 엔티티의 중복을
                    Hibernate가 결과 List에서 자동으로 제거한다. 따라서 Hibernate 6에서는 위 쿼리의 결과가 두 개로 반환된다.
                    TeamA
                    TeamB

                    단, 실제 SQL JOIN 결과 자체에서 행이 2개만 생성되는 것은 아니다.
                    DB에서는 TeamA가 회원 수만큼 중복된 행으로 조회되고, Hibernate가 엔티티 결과를 만드는 과정에서
                    동일한 부모 엔티티의 중복을 제거하는 것이다.

                    또한 join fetch는 INNER JOIN이므로 회원이 없는 TeamC는 결과에서 제외된다.

                    회원이 없는 Team까지 조회하려면 left join fetch t.members를 사용해야 한다.

                    DB에서는 TeamA-회원1, TeamA-회원2 때문에 3개의 행이 나오지만, 객체로 변환할 때 같은 Team ID를 가진 Team 엔티티는
                    영속성 컨텍스트에서 하나의 객체로 관리한다. 대신 Member는 서로 식별자가 다르기 때문에 각각의 Member 객체로 만들어지고
                    TeamA의 members 컬렉션에 추가된다. 그리고 Hibernate 6에서는 SQL JOIN으로 인해 결과 List에 동일한 Team 객체가 반복해서
                    들어가는 것까지 자동으로 제거해준다.
                *//*
                System.out.println("team = " + team.getName() + " | " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("member = " + member);
                }
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
*/


        // 페치 조인과 일반 조인의 차이
        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("팀C");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(10);
            member3.setType(MemberType.ADMIN);
            member3.changeTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            member4.setAge(10);
            member4.setType(MemberType.ADMIN);
            em.persist(member4);

            em.flush();
            em.clear();


//            String query = "select t from Team t join t.members m";
            String query = "select t from Team t join fetch t.members m";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();

            for (Team team : result) {
                System.out.println("team = " + team.getName() + " | " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("member = " + member);
                }
            }

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
