package main.repository;

import main.entity.Mark;
import org.springframework.data.repository.CrudRepository;

public interface MarkRepository extends CrudRepository<Mark, Long> {
}
