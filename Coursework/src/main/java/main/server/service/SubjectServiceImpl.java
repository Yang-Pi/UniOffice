package main.server.service;

import main.server.entity.Subject;
import main.server.exception.NotFoundException;
import main.server.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<Subject> listSubjects() {
        return (List<Subject>) subjectRepository.findAll();
    }

    @Override
    public Subject findById(Integer id) {
        Optional<Subject> optionalGroup = subjectRepository.findById(id);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        }
        else {
            new NotFoundException("Subject is not found");
        }

        return null;
    }

    @Override
    public Subject findByName(String name) {
        Optional<Subject> optionalGroup = subjectRepository.findByName(name);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        }
        else {
            new NotFoundException("Subject is not found");
        }

        return null;
    }

    @Override
    public Subject addSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}
