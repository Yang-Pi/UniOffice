package main.entity;

import javax.persistence.*;

@Entity
@Table(name = "Subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer _id;
    @Column(name = "name")
    private String _name;

    public Subject() {}
}
