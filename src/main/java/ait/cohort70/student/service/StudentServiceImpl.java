package ait.cohort70.student.service;

import ait.cohort70.student.dao.StudentRepository;
import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.model.Student;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;


    @Override
    public void addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (studentRepository.findById(studentCredentialsDto.getId()).isEmpty()) {
            Student student = new Student(studentCredentialsDto.getId(), studentCredentialsDto.getName(),
                    studentCredentialsDto.getPassword());
            studentRepository.save(student);
        } else {
            throw new EntityExistsException();
        }

    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!studentUpdateDto.getName().isEmpty()) student.setName(studentUpdateDto.getName());
        if (!studentUpdateDto.getPassword().isEmpty()) student.setPassword(studentUpdateDto.getPassword());
        //  Student newStudent = new Student(id, studentUpdateDto.getName(),
        //       studentUpdateDto.getPassword());
        // studentRepository.save(newStudent);
        // return new StudentCredentialsDto(newStudent.getId(), newStudent.getName(), newStudent.getPassword());
        return new StudentCredentialsDto(student.getId(), student.getName(), student.getPassword());
    }

    @Override
    public void addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);
    }

    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return studentRepository.findAll().stream()
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .map(x -> new StudentDto(x.getId(), x.getName(), x.getScores())).toList();
    }


    @Override
    public Long countStudentsByNames(Set<String> names) {
        return studentRepository.findAll().stream()
                .filter(x -> names.contains(x.getName()))
                .count();
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return studentRepository.findAll().stream()
                .filter(x -> {
                    Map<String, Integer> scores = x.getScores();

                    if (scores.containsKey(examName)) {
                        Integer actualScore = scores.get(examName);
                        return actualScore != null && actualScore >= minScore;
                    }
                    return false;
                })
                .map(x -> new StudentDto(x.getId(), x.getName(), x.getScores())).toList();
    }
}
