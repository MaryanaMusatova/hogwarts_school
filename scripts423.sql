-- Получение информации о студентах с названиями факультетов
SELECT s.name, s.age, f.name AS faculty_name
FROM Students s
LEFT JOIN Faculty f ON s.faculty_id = f.id;

-- Получение студентов, у которых есть аватарки
SELECT s.name
FROM Students s
INNER JOIN Avatars a ON s.id = a.student_id;