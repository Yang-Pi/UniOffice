package main.server.service;

import main.server.entity.Mark;

import java.util.List;
import java.util.Optional;

public interface MarkService {
    List<Mark> listMarks();
    Optional<Mark> findById(Integer id);

    Mark addMark(Mark newMark);
}
