--liquibase formatted sql

--changeset musatovam:create-student-name-index
CREATE INDEX IF NOT EXISTS idx_student_name ON students(name);

--changeset musatovam:create-faculty-name-color-index
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty(name, color);