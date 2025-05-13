package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.debug("StudentService initialized with repository: {}", studentRepository);
    }

    public Student createStudent(Student student) {

        logger.info("Creating new student: {}", student);
        Student createdStudent = studentRepository.save(student);
        logger.debug("Created student with ID: {}", createdStudent.getId());
        return createdStudent;
    }

    public Optional<Student> getStudentById(long id) {
        logger.debug("Fetching student by ID: {}", id);
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            logger.warn("Student with ID {} not found", id);
        } else {
            logger.debug("Found student: {}", student.get());
        }
        return student;
    }

    public Student updateStudent(Student student) {
        logger.info("Updating student with ID: {}", student.getId());
        Student updatedStudent = studentRepository.save(student);
        logger.debug("Updated student: {}", updatedStudent);
        return updatedStudent;
    }

    public void deleteStudent(long id) {
        logger.info("Deleting student with ID: {}", id);
        if (!studentRepository.existsById(id)) {
            logger.error("Attempt to delete non-existent student with ID: {}", id);
            throw new RuntimeException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
        logger.debug("Student with ID {} deleted successfully", id);
    }

    public List<Student> getStudentsByAge(int age) {
        logger.debug("Fetching students by age: {}", age);
        List<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public List<Student> findStudentsByAgeBetween(int minAge, int maxAge) {
        logger.debug("Fetching students with age between {} and {}", minAge, maxAge);
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range", students.size());
        return students;
    }

    public Faculty getStudentFaculty(Long studentId) {
        logger.debug("Fetching faculty for student ID: {}", studentId);
        return studentRepository.findById(studentId)
                .map(student -> {
                    Faculty faculty = student.getFaculty();
                    logger.debug("Found faculty {} for student {}", faculty, student);
                    return faculty;
                })
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    return new RuntimeException("Student not found");
                });
    }

    public Integer getTotalStudentsCount() {
        logger.debug("Fetching total students count");
        Integer count = studentRepository.getTotalCount();
        logger.info("Total students count: {}", count);
        return count;
    }

    public Double getAverageStudentsAge() {
        logger.debug("Calculating average students age");
        Double averageAge = studentRepository.getAverageAge();
        logger.info("Average students age: {}", averageAge);
        return averageAge;
    }

    public List<Student> getLastStudents() {
        logger.debug("Fetching last students");
        List<Student> students = studentRepository.getLastStudents();
        logger.debug("Found {} last students", students.size());
        return students;
    }

    public List<String> getStudentsNamesStartingWithA() {
        logger.debug("Fetching students whose names start with 'A'");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    // Самый быстрый - Формула Гаусса: n(n+1)/2
    //return 1_000_000L * (1_000_000L + 1L) / 2L;

    public long calculateParallelSum() {
        return IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .asLongStream()
                .sum();
    }

    public List<Student> getFirstSixStudents() {
        return studentRepository.findFirst6ByOrderByIdAsc();
    }
}
