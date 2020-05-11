package main.entity;

import javax.persistence.*;

@Entity
@Table(name = "Marks")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer _id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Person _student;

    @ManyToOne
    @JoinColumn(name = "subject")
    private Subject _subject;

    @ManyToOne
    @JoinColumn(name = "teacher")
    private Person _teacher;

    @Column(name = "value")
    private Integer _value;

    public Mark() {    }
}
