package main.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer _id;
    @Column(name = "name")
    private String _name;

    public Group() { }

    public Group(String name) {
        this._name = name;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }
}
