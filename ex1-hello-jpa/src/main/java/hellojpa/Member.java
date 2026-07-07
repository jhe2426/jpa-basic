package hellojpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

//   @Entity
public class Member {

    @Id
    private Long id;

    /*
        @Column 속성
        - name: 필드와 매핑할 테이블의 컬럼 이름
        - insertable, updatable: 등록, 변경 가능 여부 (기본값: TRUE)
        - nullable(DDL): null 값의 허용 여부를 설정, false로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다.
        - unique(DDL): @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다.
            유니크 제약 조건 이름이 랜덤으로 식별하기 어려운 명으로 지정되므로 실무에서는 이 방법으로 유니크 제약을 설정하지 않는다.
            실무에서는 @Table의 uniqueConstraints를 사용하여 유니크 제약 조건 이름을 함께 지정해서 사용햔다.
        - columnDefinition(DDL): 데이터베이스 컬럼 정보를 직접 줄 수 있다.
            ex) columnDefinition = "varchar(100) default 'EMPTY'"
        - length(DDL): 문자 길이 제약조건, String 타입에만 사용한다.
        - precision, scale(DDL): BigDecimal 타입에서 사용한다(BigInteger도 사용할 수 있다.)
            precision은 소수점을 포함한 전체 자릿수를
            scale은 소수의 자릿수이다.
            double, float 타입에는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용한다.
    */
    @Column(name = "name")
    private String username;

    private Integer age;

    /*
        @Enumerated
        - EnumType.ORDINAL: enum 순서를 데이터베이스에 저장 (기본값)
            실무에서 사용하지 않음
            왜냐하면 요구사항에 따라 EnumType의 개수가 줄어들 수도 늘어날 수도 있는데 그럼 일일이 전부 다 고쳐줘야 한다거나 원래의 값의 의미가 변질되어 버리므로
            ORDINAL로 사용하면 안 된다.
        - EnumType.STRING: enum 이름을 데이터베이스에 저장
    */
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    /*
        @Temporal
        - TemporalType.DATE: 날짜, 데이터베이스 date 타입과 매핑 (예: 2024-10-11)
        - TemporalType.TIME: 시간, 데이터베이스 time 타입과 매핑 (예: 11:11:11)
        - TemporalType.TIMESTAMP: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑(예: 2022-02-12 11:11:11)

        참고: LocalDate, LocalDateTime을 사용할 때에는 @Temporal 생략 가능 (최신 하이버네이트 지원)
    */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private LocalDate testLocalDate;
    private LocalDateTime testLocalDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /*
        @Lob
        - 데이터베이스 BLOB, CLOB 타입과 매핑
        - @Lob에는 지정할 수 있는 속성이 없다.
        - 매핑하는 필드 타입이 문자이면 CLOB 매핑, 나머지는 BLOB 매핑
            CLOB: String, char[], java.sql.CLOB
            BLOB: byte[], java.sql.BLOB
    */
    @Lob
    private String description;

    /*
        @Transient
        - 필드 매핑을 하지 않도록 도와주는 애노테이션
        - 데이터베이스에 저장하지 않고 조회되지 않는 멤버 필드에 선언을 함
        - 주로 메모리상에서만 임지로 어떤 값을 보관하고 싶을 때 사용

        @Transient
        private Integer temp;
    */

    public Member() {
    }
}
