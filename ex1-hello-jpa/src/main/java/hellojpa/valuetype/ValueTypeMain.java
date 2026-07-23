package hellojpa.valuetype;


/*
    값 타입 분류
    - 기본값 타입
        - 자바 기본 타입 (int, double)
        - 래퍼 클래스 (Integer, Long)
        - String

    - 임베디드 타입 (embedded type, 복합 값 타입)
    - 컬렉션 값 타입 (collection value type)
*/
public class ValueTypeMain {

    public static void main(String[] args) {

        // 자바의 기본 타입은 값을 절대 공유하지 않고 항상 값을 복사함
        int a = 10;
        int b = a;

        a = 20;

        System.out.println("a = " + a);
        System.out.println("b = " + b);

        /*
            Integer같은 래퍼 클래스나 String 같은 특수 클래스는 공유 가능한 객체
            Integer 같은 래퍼 클래스와 String은 불변 객체이다.
            여러 변수가 같은 객체를 참조하더라도 객체 내부의 값을 변경할 수 없으므로
            공유 참조로 인한 사이드 이펙트가 발생하지 않는다.
            값을 변경하는 것처럼 보이는 연산은 기존 객체를 수정하는 것이 아니라
            새로운 객체를 참조하도록 변수를 재할당하는 것이다.
        */
        Integer c = 10;
        Integer d = c;

        System.out.println("c = " + c);
        System.out.println("d = " + d);

    }

}
