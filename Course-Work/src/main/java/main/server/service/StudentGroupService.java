package main.server.service;

import main.server.entity.StudentGroup;

import java.util.List;

public interface StudentGroupService {
    List<StudentGroup> listGroups();
    StudentGroup findById(Integer id);
    StudentGroup findByName(String name);

    StudentGroup addGroup(StudentGroup group);
}
