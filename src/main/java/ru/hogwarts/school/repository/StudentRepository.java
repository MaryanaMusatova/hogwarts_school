package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int minAge, int maxAge);

    @Query("SELECT COUNT(*) FROM Student")
    Integer getTotalCount();

    @Query("SELECT AVG(age) FROM Student")
    Double getAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getLastStudents();
}
