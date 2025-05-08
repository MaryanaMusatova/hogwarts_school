package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
        testStudent = new Student(null, "Test Student", 11);
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        ResponseEntity<Student> response = createTestStudent();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(student -> {
                    assertThat(student.getId()).isNotNull();
                    assertThat(student.getName()).isEqualTo("Test Student");
                });
    }

    @Test
    void getStudentById_shouldReturnStudentWhenExists() {
        Student created = createTestStudent().getBody();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/{id}", Student.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(Student::getId)
                .isEqualTo(created.getId());
    }

    @Test
    void getStudentById_shouldReturnNotFoundWhenNotExists() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/{id}", Student.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        Student created = createTestStudent().getBody();
        created.setName("Updated Name");

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl,
                PUT,
                new HttpEntity<>(created),
                Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(Student::getName)
                .isEqualTo("Updated Name");
    }

    @Test
    void deleteStudent_shouldReturnOk() {
        Student created = createTestStudent().getBody();

        restTemplate.delete(baseUrl + "/{id}", created.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/{id}", Student.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredList() {
        createTestStudent();

        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/age/{age}", List.class, 11);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getStudentFacultyTest_shouldReturnNotNull() {
        createTestStudent();
        long id = 1;

        String response = restTemplate.getForObject(
                baseUrl + "/get-student-faculty?id={id}", String.class, id);

        assertThat(response).isNotNull();
    }

    private ResponseEntity<Student> createTestStudent() {
        return restTemplate.postForEntity(baseUrl, testStudent, Student.class);
    }
}