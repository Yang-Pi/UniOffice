package main.server.service;

import main.server.entity.Mark;
import main.server.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarkServiceImpl implements MarkService {
    @Autowired
    private MarkRepository markRepository;

    @Override
    public List<Mark> listMarks() {
        return (List<Mark>) markRepository.findAll();
    }

    @Override
    public Optional<Mark> findById(Integer id) {
        return markRepository.findById(id);
    }

    @Override
    public Mark addMark(Mark newMark) {
        return markRepository.save(newMark);
    }


}
