package main.server.service;

import main.server.entity.Person;
import main.server.exception.NotFoundException;
import main.server.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService{
    @Autowired
    private PersonRepository repository;

    @Override
    public List<Person> listPeople() {
        return (List<Person>) repository.findAll();
    }

    @Override
    public Person findById(Integer id) {
        Optional<Person> personOptional = repository.findById(id);
        if (personOptional.isPresent()) {
            return (Person) personOptional.get();
        }
        else {
            new NotFoundException("Person is not found!");
        }

        return null;
    }

    @Override
    public Person addPerson(Person newPerson) {
        return repository.save(newPerson);
    }
}
