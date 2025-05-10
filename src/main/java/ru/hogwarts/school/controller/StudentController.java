package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/age/{age}")
    public List<Student> getStudentsByAge(@PathVariable int age) {
        return studentService.getStudentsByAge(age);
    }

    @GetMapping("/age-between")
    public List<Student> getStudentsByAgeBetween(
            @RequestParam int min,
            @RequestParam int max) {
        return studentService.findStudentsByAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        return studentService.getStudentFaculty(id);
    }

    @GetMapping("/count")
    public Integer getStudentsCount() {
        return studentService.getTotalStudentsCount();
    }

    @GetMapping("/avg-age")
    public Double getAverageAge() {
        return studentService.getAverageStudentsAge();
    }

    @GetMapping("/last-students")
    public List<Student> getLastStudents() {
        return studentService.getLastStudents();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentsNamesStartingWithA() {
        return studentService.getStudentsNamesStartingWithA();
    }

    @GetMapping("/parallel-sum")
    public long getParallelSum() {
        return IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .asLongStream()
                .sum();
    }
}

