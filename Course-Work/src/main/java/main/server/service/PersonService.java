package main.server.service;

import main.server.entity.Person;

import java.util.List;

public interface PersonService {
    List<Person> listPeople();
    Person findById(Integer id);

    Person addPerson(Person newPerson);
}
