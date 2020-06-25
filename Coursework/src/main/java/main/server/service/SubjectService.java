package main.server.service;

import main.server.entity.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> listSubjects();
    Subject findById(Integer id);
    Subject findByName(String name);

    Subject addSubject(Subject subject);
}
