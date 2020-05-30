package main.server.repository;

import main.server.entity.StudentGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentGroupRepository extends CrudRepository<StudentGroup, Integer> {
    Optional<StudentGroup> findStudentGroupByName(String name);
}


