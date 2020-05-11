package main.entity;

import javax.persistence.*;

@Entity
@Table(name = "People")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String _firstName;
    @Column(name = "last_name", nullable = false)
    private String _lastName;
    @Column(name = "father_name", nullable = false)
    private String _fatherName;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group _group;

    @Column(name = "type")
    private Character _type;

    public Person() { }
}
