package ait.cohort70.student.dao;

import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.model.Student;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository {

    Student save(Student student);

    Optional<Student> findById(Long id);

    void deleteById(Long id);

    List<Student> findAll();

}
