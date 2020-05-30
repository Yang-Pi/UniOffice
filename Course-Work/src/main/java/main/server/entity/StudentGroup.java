package main.server.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "StudentGroups")
public class StudentGroup {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany
    private List<Person> people;

    public StudentGroup() { }

    public StudentGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public void addStudent(Person newPerson) {
        people.add(newPerson);
    }
}
