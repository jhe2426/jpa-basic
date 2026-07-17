package hellojpa.inheritance;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A") // Item 테이블의 DTYPE에 A값으로 해당 엔티티가 저장될 때 저장이 됨
public class Album extends Item {

    private String artist;

}
