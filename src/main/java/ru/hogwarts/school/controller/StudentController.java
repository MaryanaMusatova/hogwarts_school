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


    // 1: Параллельный вывод
    @GetMapping("/print-parallel")
    public void printNamesParallel() {

        List<Student> students = studentService.getFirstSixStudents();

        System.out.println("Основной поток:");
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        // Параллельные потоки
        new Thread(() -> {
            System.out.println("Поток 1:");
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println("Поток 2:");
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        }).start();
    }

    //  2: Синхронизированный вывод
    @GetMapping("/print-synchronized")
    public void printNamesSynchronized() {

        List<Student> students = studentService.getFirstSixStudents();

        System.out.println("Основной поток:");
        printSynchronized(students.get(0).getName());
        printSynchronized(students.get(1).getName());

        // Параллельные потоки
        new Thread(() -> {
            System.out.println("Поток 1:");
            printSynchronized(students.get(2).getName());
            printSynchronized(students.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println("Поток 2:");
            printSynchronized(students.get(4).getName());
            printSynchronized(students.get(5).getName());
        }).start();
    }

    private synchronized void printSynchronized(String name) {
        System.out.println(name);
    }
}
