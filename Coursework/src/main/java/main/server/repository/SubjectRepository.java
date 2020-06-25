package main.server.repository;

import main.server.entity.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Integer> {
    Optional<Subject> findByName(String name);
}
