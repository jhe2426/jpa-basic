package jpabook.jpashop.valuetype;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Address {

    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /*
        값 타입은 식별자가 없으므로 모든 구성 값을 기준으로 동등성을 비교한다.

        @ElementCollection으로 사용해도 값 타입 요소 자체가 프록시가 되는 것은 아니므로 필드에 직접
        접근해도 정상적으로 비교할 수 있다.

        반면 엔티티는 지연 로딩 시 프록시 객체로 전달될 수 있다.
        엔티티의 equals()에서 비교 대상의 필드에 직접 접근하면 프록시가 보관한 실제 엔티티의 값에 접근하지 못할 수 있으므로,
        프록시 초기화와 실제 값 조회가 가능하도록 getter를 통해 비교하는 것이 안전하다.
        Use getters when available 옵션을 선택한 뒤 재정의하도록 하면 됨
    */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}
