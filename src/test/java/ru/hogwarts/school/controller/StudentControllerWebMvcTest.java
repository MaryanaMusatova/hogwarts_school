package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StudentControllerMockTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private final Student testStudent = new Student(1L, "Harry Potter", 12);
    private final Faculty testFaculty = new Faculty(1L, "Gryffindor", "Red");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void createStudent_shouldReturnCreatedStudentAndStatusOk() throws Exception {
        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Harry Potter\",\"age\":12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"));
    }

    @Test
    void getStudentById_shouldReturnStudentWhenExists() throws Exception {
        when(studentService.getStudentById(anyLong())).thenReturn(Optional.of(testStudent));

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry Potter"));
    }

    @Test
    void getStudentById_shouldReturnNotFoundWhenNotExists() throws Exception {
        when(studentService.getStudentById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        Student updated = new Student(1L, "Harry Updated", 13);
        when(studentService.updateStudent(any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Harry Updated\",\"age\":13}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry Updated"));
    }

    @Test
    void deleteStudent_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentsByAge_shouldReturnStudentsList() throws Exception {
        when(studentService.getStudentsByAge(anyInt())).thenReturn(Collections.singletonList(testStudent));

        mockMvc.perform(get("/student/age/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        when(studentService.getStudentFaculty(anyLong())).thenReturn(testFaculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }
}