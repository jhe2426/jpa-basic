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
    JPQL 기본 함수
    - CONCAT
    - SUBSTRING
    - TRIM
    - LOWER, UPPER
    - LENGTH
    - LOCATE
    - ABS, SQRT, MOD
    - SIZE, INDEX(JPA 용도)
        INDEX 함수
            INDEX()는 @OrderColumn이 적용된 List 컬렉션에서 각 요소가 몇 번째 위치에 있는지를 조회하는 JPQL 함수이다.
            자바의 LIST와 동일하게 인덱스는 0부터 시작한다.
            예를 들어 Team 엔티티가 Member 목록을 다음과 같이 가지고 있다고 가정
            @OneToMany(mappedBy = "team")
            @OrderColumn(name = "member_order")
            private List<Member> members = new ArrayList<>();

            @OrderColumn은 List 요소의 순서를 DB의 별도 컬럼에 저장된다.

            Team.members = [A,B,C] 이렇게 List에 값이 저장되면 DB에는 다음과 같이 저장된다.
            member | member_order
            -------|-------------
            A      | 0
            B      | 1
            C      | 2
            따라서 다음 JPQL에서 INDEX(m)는 각 Member의 member_order 값을 의미
            select m from Team t join t.members = m where index(m) = 0
                위 쿼리는 Team.members 리스트에서 인덱스가 0인 첫 번째 Member인 A를 조회한다.

            부모의 List에서 자식을 제거하면 Hibernate가 * List의 구조가 변경된 것을 감지할 수 있다.
            그래서 기존 [A, B, C]에서 B를 삭제해도 Hibernate가 감지를 할 수 있어 C의 순서를 다시 조장하여
            member | member_order
            -------|-------------
            A      | 0
            C      | 1
            위와 같이 저장을 하게 된다.

            하지만, 부모의 List에서 B를 제거하지 않고 자식 엔티티만 직접 삭제를 하게 되면 Hibernate가 List의 순서 변경을 감지하지 못하게 된다.
            그래서 DB에 다음과 같이 중간 순서가 비어 있을 수 있다.
            member | member_order
            -------|-------------
            A      | 0
            C      | 2
            Hibernate는 member_order를 단순한 정렬값이 아니라 실제 List의 인덱스로 사용이 된다.
            그래서 위 데이터를 다시 List로 조회하면 [A, null, C]와 같이 비어 있는 1번 위치가 null로 채워진다.

            JPQL 벌크 delete, 네이티브 SQL, DB에서 직접 실행한 delete도 부모 List의 변경 감지를
            거치지 않기 때문에 같은 문제가 발생할 수 있다.
                벌크 쿼리: 영속성 컨텍스트에 있는 엔티티를 하나씩 수정하거나 삭제하지 않고,
                    조건에 맞는 데이터를 UPDATE 또는 DELETE 쿼리로 DB에서 한 번에 직접 변경하는 방식이다.
                    벌크 쿼리가 실행되기 전에 영속성 컨텍스트의 기존 변경 사항은 flush되어 DB에 먼저 반영될 수 있다.
                    그러나 벌크 쿼리로 변경된 DB의 결과는 영속성 컨텍스트에 있는 엔티티에 자동으로 반영되지 않는다.
                    따라서 벌크 쿼리 실행 후에는 DB와 영속성 컨텍스트의 상태가 서로 달라질 수 있으므로 일반적으로 em.clear()를 호출한다.
                    ex) em.createQuery("delete from Member m where m.age < 20").executeUpdate();

            따라서 @OrderColumn을 사용하는 List의 자식을 삭제할 때는 자식 엔티티만 따로 삭제하지 말고,
            부모의 List에서도 함께 제거하여 연관관계와 순서를 동기화해야 한다.

            이러한 순서 관리의 복잡성과 인덱스 누락 가능성 때문에 @OrderColumn과 INDEX()는
            실무에서 반드시 필요한 경우에만 신중하게 사용하는 편이다.

    사용자 정의 함수 호출
    - 하이버네이트는 사용전 방언에 추가해야 한다.
        - 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.
            select function('group_concat', i.name_ from Item i
*/
public class FunctionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setAge(10);
            member1.setType(MemberType.ADMIN);
            member1.changeTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setAge(10);
            member2.setType(MemberType.ADMIN);
            member2.changeTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();


//            String query = "select concat('a', 'b') from Member m";
//            String query = "select 'a' || 'b' from Member m"; // ab가 출력됨
//            String query = "select substring(m.username, 2, 3) from Member m";
//            String query = "select locate('de','abcdefg') from Member m"; // 숫자를 반환해줌 그래서 Integer형으로 받아야함
//            String query = "select size(t.members) from Team t";
//            String query = "select index(t.members) from Team t";

//            String query = "select function('group_concat', m.username) from Member m";
            String query = "select group_concat(m.username) from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
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
