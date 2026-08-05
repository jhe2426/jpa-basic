package dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect extends H2Dialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        // 부모 Dialect가 기본으로 제공하는 SQL 함수들을 먼저 등록한다.
        // 이 호출을 생략하면 기본 함수 등록에 영향을 줄 수 있으므로 일반적으로 호출한다.
        super.initializeFunctionRegistry(functionContributions);

        // Hibernate가 관리하는 SQL 함수 저장소(Function Registry)에 group_concat 함수를 등록한다.
        functionContributions.getFunctionRegistry().registerNamed(

                // JPQL에서 사용할 함수 이름, 실제 DB에 전달되는 SQL 함수 이름도 group_concat으로 동일
                "group_concat",

                // group_concat 함수의 반환 타입을 String으로 지정
                functionContributions
                        //Hibernate가 사용하는 타입 설정 정보를 가져옴
                        .getTypeConfiguration()
                        // String, Integer 등의 기본 타입이 등록된 저장소를 가져옴
                        .getBasicTypeRegistry()
                        // group_concat의 조회 결과를 String으로 처리하도록
                        // Hibernate에 등록된 기본 타입 저장소에서 문자열(String) 타입 정보를 가져옴
                        .resolve(StandardBasicTypes.STRING)
        );
    }
}
