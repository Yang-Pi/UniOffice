package main.server.web;

import main.server.entity.Mark;
import main.server.entity.Person;
import main.server.entity.StudentGroup;
import main.server.entity.Subject;
import main.server.service.MarkService;
import main.server.service.PersonService;
import main.server.service.StudentGroupService;
import main.server.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UniversityController {
    @Autowired
    private StudentGroupService studentGroupService;
    @Autowired
    private PersonService personService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private MarkService markService;

    @PostMapping(value = "/addGroup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentGroup addGroup(@RequestBody StudentGroup newGroup) {
        return studentGroupService.addGroup(newGroup);
    }

    @PostMapping(value = "/addSubject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Subject addSubject(@RequestBody Subject newSubject) {
        Subject subject = subjectService.findByName(newSubject.getName());
        if (subject == null) {
            return subjectService.addSubject(newSubject);
        }
        return null;
    }

    @PostMapping(value = "/addPerson", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person addPerson(@RequestBody Person newPerson) {
        if (newPerson.getType().equals('S')) {
            StudentGroup studentGroup = studentGroupService.findById(newPerson.getStudentGroup().getId());
            if (studentGroup != null) {
                newPerson.setStudentGroup(studentGroup);
            }
        }
        else if (newPerson.getType().equals('P')) {
            newPerson.setStudentGroup(studentGroupService.findByName("professors"));
        }
        newPerson.getStudentGroup().addStudent(newPerson);

        return personService.addPerson(newPerson);
    }

    @PostMapping(value = "/addMark", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mark addMark(@RequestBody Mark newMark) {
        Subject subject = subjectService.findById(newMark.getSubject().getId());
        Person student = personService.findById(newMark.getStudent().getId());
        Person professor = personService.findById(newMark.getProfessor().getId());

        if (subject != null && student != null && professor != null) {
            newMark.setSubject(subject);
            newMark.setStudent(student);
            newMark.setProfessor(professor);
            student.addMark(newMark);

            return markService.addMark(newMark);
        }

        return null;
    }

    @GetMapping("/groups")
    public ResponseEntity<List<StudentGroup>> getAllGroups() {
        List<StudentGroup> groupList = studentGroupService.listGroups();
        for (StudentGroup studentGroup : groupList) {
            studentGroup.setPeople(null);
        }
        return new ResponseEntity<>(groupList, HttpStatus.OK);
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<List<Person>> getStudentsFromGroupById(@PathVariable("id") Integer id) {
        List<Person> students = new ArrayList<>();

        StudentGroup group = studentGroupService.findById(id);
        if (group != null) {
            for (Person student : group.getPeople()) {
                student.setStudentGroup(null);
                student.setMarks(null);
                students.add(student);
            }
            return new ResponseEntity<>(students, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/checkGroupName/{groupname}")
    public ResponseEntity<StudentGroup> checkGroupName(@PathVariable("groupname") String groupname) {
        StudentGroup studentGroup = studentGroupService.findByName(groupname);
        if (studentGroup != null) {
            studentGroup.setPeople(null);
            return new ResponseEntity<>(studentGroup, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getStudentById(@PathVariable("id") Integer id) {
        Person person = personService.findById(id);

        if (person != null) {
            person.setStudentGroup(null);
            person.setMarks(null);
            return new ResponseEntity<>(person, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/student/{id}/marks")
    public ResponseEntity<Person> getStudentMarksById(@PathVariable("id") Integer id) {
        Person student = personService.findById(id);

        if (student != null) {
            student.setStudentGroup(null);

            for (int i = 0; i < student.getMarks().size(); ++i) {
                student.getMarks().get(i).setStudent(null);
                student.getMarks().get(i).getProfessor().setStudentGroup(null);
            }
            return new ResponseEntity<>(student, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects() {
        return new ResponseEntity(subjectService.listSubjects(), HttpStatus.OK);
    }

    @GetMapping("/checkSubjectName/{subjectname}")
    public ResponseEntity<Subject> checkSubjectName(@PathVariable("subjectname") String subjectname) {
        Subject subject = subjectService.findByName(subjectname);
        if (subject != null) {
            return new ResponseEntity<>(subject, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
