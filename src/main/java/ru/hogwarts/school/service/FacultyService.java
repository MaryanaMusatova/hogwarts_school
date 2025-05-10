package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service

public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {

        this.facultyRepository = facultyRepository;
        logger.info("FacultyService initialized");
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.debug("Creating new faculty: {}", faculty);
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty created successfully with ID: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Optional<Faculty> getFacultyById(long id) {
        logger.debug("Getting faculty by ID: {}", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isEmpty()) {
            logger.warn("Faculty not found with ID: {}", id);
        } else {
            logger.trace("Found faculty: {}", faculty.get());
        }
        return faculty;
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.debug("Updating faculty with ID: {}", faculty.getId());
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty updated successfully: {}", updatedFaculty.getId());
        return updatedFaculty;
    }

    public void deleteFaculty(long id) {
        logger.info("Deleting faculty with ID: {}", id);
        try {
            facultyRepository.deleteById(id);
            logger.info("Faculty deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete faculty with ID: {}", id, e);
            throw e;
        }
    }

    public List<Faculty> getFacultiesByColor(String color) {
        logger.debug("Getting faculties by color: {}", color);
        List<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public List<Faculty> searchFaculties(String searchTerm) {
        logger.debug("Searching faculties by term: {}", searchTerm);
        List<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(searchTerm, searchTerm);
        logger.debug("Found {} faculties matching '{}'", faculties.size(), searchTerm);
        return faculties;
    }

    public List<Student> getFacultyStudents(Long facultyId) {
        logger.debug("Getting students for faculty ID: {}", facultyId);
        return facultyRepository.findById(facultyId)
                .map(faculty -> {
                    logger.debug("Found {} students for faculty {}",
                            faculty.getStudents().size(), facultyId);
                    return faculty.getStudents();
                })
                .orElseThrow(() -> {
                    logger.error("Faculty not found with ID: {}", facultyId);
                    return new RuntimeException("Faculty not found");
                });
    }

    public String getLongestFacultyName() {
        logger.debug("Finding faculty with the longest name");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }
}