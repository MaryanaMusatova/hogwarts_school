-- scripts.sql
-- SQL-скрипты для работы с базой данных Hogwarts

-- Найти всех студентов в возрасте от 10 до 15 лет
SELECT * FROM student WHERE age BETWEEN 10 AND 15;

-- Получить только имена всех студентов
SELECT name FROM student;

-- Найти студентов, в имени которых есть буква 'P' (регистронезависимо)
SELECT * FROM student WHERE name ILIKE '%P%';

-- Найти студентов, у которых возраст меньше чем их ID
SELECT * FROM student WHERE age < id;

-- Отсортировать студентов по возрасту (по возрастанию)
SELECT * FROM student ORDER BY age;

-- Количество студентов на факультетах
SELECT f.name AS faculty_name, COUNT(s.id) AS student_count
FROM faculty f
LEFT JOIN student s ON f.id = s.faculty_id
GROUP BY f.name;

-- Средний возраст студентов
SELECT AVG(age) AS average_age FROM student;

-- Обновление возраста студента по ID
-- UPDATE student SET age = 18 WHERE id = 1;

-- Удаление студента по ID
-- DELETE FROM student WHERE id = 10;