package jpabook.jpashop.valuetype.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.DeliveryStatus;
import jpabook.jpashop.domain.Order2;
import jpabook.jpashop.valuetype.Address;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class Delivery extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @Enumerated
    private Address address;

    private DeliveryStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
}
