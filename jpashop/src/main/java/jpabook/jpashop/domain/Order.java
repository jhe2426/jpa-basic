package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

//@Entity
//@Table(name = "ORDERS")
public class Order {

    /*
        해당 엔티티는 객체 설계를 테이블 설계에 맞춘 방식임
        테이블의 외래키를 객체에 그대로 가져옴
            @Column(name = "MEMBER_ID")
            private Long member
        객체 그래프 탐색이 불가능
            객체 그래프 탐색: 객체가 참조하고 있는 다른 객체를 점(.)으로 계속 따라가면서 조회하는 것
                객체가 가진 참조를 따라 다른 객체로 이동하면서 데이터를 조회하는 것
    */

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
