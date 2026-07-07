package hellojpa;

import jakarta.persistence.*;

@Entity
@Table(name = "member")
@SequenceGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ", // 매핑할 데이터베이스 시퀀스 이름
    initialValue = 1, allocationSize = 50
)

/*
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
*/
public class MemberV2 {

    /*
        기본 키 매핑 방법
        - 직접 할당: @Id만 사용
        - 자동 생성(@GeneratedValue)
            - IDENTITY: 데이터베이스에 위임, MYSQL
            - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE, PostgreSQL, H2
                @SequenceGenerator 필요
                    - JPA에게 어떤 DB 시퀀스를 사용해서 사용해서 PK 값을 만들지 알려주는 설정 애노테이션
                    - 속성
                        name: JPA 내부에서 사용할 시퀀스 생성기 이름
                        sequenceName: DB에 실제로 존재하는 시퀀스 이름
                        allocationSize: Hibernate가 DB 시퀀스에서 ID 값을 한 번에 몇 개씩 미리 확보할지 정하는 값 (기본값은 50)
                            DB 시퀀스의 INCREMENT BY 값과 동일하게 맞춰야 한다.
                                값이 다르면 Hibernate가 기대하는 다음 ID 구간과 DB 시퀀스가 실제로 증가하는 값이 맞지 않아
                                ID 할당 범위 계산이 꼬이거나 설정 오류가 발생할 수 있다.
                                같은 ID가 중복 할당될 위험이 있다.
                                따라서 DB 시퀀스가 INCREMENT BY 50이면 allocationSize도 50,
                                DB 시퀀스가 INCREMENT BY 1이면 allocationSize도 1로 설정해야 한다.
                                INCREMENT BY: DB 시퀀스가 호출될 때마다 값이 몇 씩 증가할지 정하는 설정 (DB 시퀀스의 증가 폭 설정)
                            확보한 ID 구간은 Hibernate 내부 Identifier Generator의 JVM 메모리에서 관리된다.
                            여러 트랜잭션에서 엔티티가 저장될 때 해당 ID를 하나씩 사용하며, 모두 사용하면 다시 DB 시퀀스를 조회한다.
                            단, 서버 재시작 등으로 사용하지 못한 ID가 생기면 PK 값이 중간에 비어 보일 수 있다.
                        initialValue: 시퀀스 시작 번호
                            DDL 생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 시작하는 수를 지정한다.
                        schema: 시퀀스가 속한 DB 스키마
                        catalog: 시퀀스가 속한 DB 카탈로그
                데이터베이스 시퀀스 오브젝트 사용
                    INSERT 전에 시퀀스에서 ID를 미리 조회하여 엔티티에 할당한다.
                    ID를 미리 알 수 있으므로 배치 INSERT 등의 성능 최적화가 가능하다.
            - TABLE: 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략, 모든 DB에서 사용
                @TableGenerator 필요
            - AUTO: 방언에 따라 자동 지정, 기본값

            IDENTITY와 SEQUENCE 전략 차이점
                - IDENTITY 전략은 INSERT를 실행해야 DB가 ID 값을 생성하므로, INSERT 이후에야 ID 값을 알 수 있다.
                - 반면 SEQUENCE 전략은 INSERT 전에 DB 시퀀스에서 ID 값을 미리 조회하여 엔티티에 할당한다.
                - 이 차이로 인해 SEQUENCE 전략은 ID를 미리 확보할 수 있고, allocationSize 설정을 통해
                    여러 개의 ID를 한 번에 받아와 사용할 수 있다. 따라서 ID 생성 과정에서 발생하는 DB 통신 횟수를
                    줄일 수 있으며, JDBC 배치 INSERT 같은 성능 최적화에 유리하다.
                        JDBC 배치: 하나의 트랜잭션 안에서 생긴 같은 형태의 INSERT/UPDATE의 여러 SQL을 묶어서 DB로 전송하는 방식
    */

    /*
        IDENTITY 전략 - 특징
        - 기본 키 생성을 데이터베이스에 위임
        - 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
            (예: MySQL의 AUTO_INCREMENT)
        - 일반적인 JPA 저장 흐름에서는 트랜잭션 커밋 시점에 flush가 발생하면서 INSERT SQL이 실행된다.
        - 하지만 IDENTITY 전략은 DB에 INSERT SQL을 실행해야만 생성된 ID 값을 알 수 있다.
        - 즉, INSERT 전에는 엔티티의 PK 값을 알 수 없다.
        - 따라서 Hibernate는 em.persist() 호출 시점에 INSERT SQL을 즉시 실행하고,
        - DB가 INSERT 시점에 생성한 식별자 값을 받아와 엔티티의 id 필드에 할당한다.
        - 이렇게 해야 영속성 컨텍스트가 해당 엔티티를 식별자 값 기준으로 관리할 수 있다.
    */
/*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK 타입은 Integer보다 Long을 사용하는 것을 권장한다.
    // Integer는 약 21억까지 표현할 수 있지만, 데이터가 증가해 범위를 초과하면
    // 이후 Long 타입으로 변경하는 비용이 크다.
    // Long은 Integer보다 메모리를 더 사용하지만, 요즘 서버 환경에서는 큰 부담이 적고
    // 장기적인 확장성을 고려하면 처음부터 Long을 사용하는 것이 안전하다.
    private Long id;
    */
/*
        권장하는 식별자 전략
        - 기본 키 제약 조건: null이 아님, 유일, 변하면 안된다.
        - 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키)를 사용하자.
            자연키: 비즈니스적으로 의미있는 키를 의미 (ex: 주민등록번호, 전화번호)
            대리키(대체키): 비즈니스와 전혀 상관없는 값, 랜덤 값을 의미
        - 예를 들어 주민등록번호도 기본 키로 적절하지 않다.
        - 권장: Long형 + 대체키 + 키 생성전략 사용
    *//*


*/

/*
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
*/

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name = "name", nullable = false)
    private String username;

    public MemberV2() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
