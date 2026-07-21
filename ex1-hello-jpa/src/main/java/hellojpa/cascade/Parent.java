package hellojpa.cascade;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    /*
        영속성 전이는 부모 엔티티가 자식 엔티티의 생명주기를 완전히 관리하는 관계에서 사용하는 것이 적절하다.
        자식 엔티티가 부모 없이 독립적으로 존재하거나, 여러 엔티티에서 공유되는 경우에는 부모 삭제나 저장 작업이
        의도하지 않게 자식에게 전파될 수 있으므로 사용에 주의해야 한다.
    */
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
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

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
