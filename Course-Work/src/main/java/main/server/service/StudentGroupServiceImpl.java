package main.server.service;

import main.server.entity.StudentGroup;
import main.server.exception.NotFoundException;
import main.server.repository.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentGroupServiceImpl implements StudentGroupService {
    @Autowired
    private StudentGroupRepository repository;

    @Override
    public List<StudentGroup> listGroups() {
        return (List<StudentGroup>) repository.findAll();
    }

    @Override
    public StudentGroup findById(Integer id) {
        Optional<StudentGroup> optionalGroup = repository.findById(id);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        }
        else {
            new NotFoundException("Group is not found");
        }

        return null;
    }

    @Override
    public StudentGroup findByName(String name) {
        Optional<StudentGroup> studentGroup = repository.findStudentGroupByName(name);
        if (studentGroup.isPresent()) {
            return studentGroup.get();
        }
        else {
            new NotFoundException("Group is not found");
        }

        return null;
    }

    @Override
    public StudentGroup addGroup(StudentGroup newGroup) {
        return repository.save(newGroup);
    }
}
