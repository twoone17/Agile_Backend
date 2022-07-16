package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item { //구현체를 가지고 할것이기떄문에 추상클래스

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    //상속관계 매핑을 해야함
    //name
    private String name;
    //price
    private int price;
    //stockquantity
    private int stockQuantity;
    //category
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //dtype

    //artist

    //etc

    //autor

    //isbn

    //director

    //actor


}
