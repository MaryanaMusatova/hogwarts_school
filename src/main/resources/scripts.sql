-- scripts.sql
-- SQL-скрипты для работы с базой данных Hogwarts

-- Найти всех студентов в возрасте от 10 до 20 лет
SELECT * FROM students WHERE age BETWEEN 10 AND 20;

-- Получить только имена всех студентов
SELECT name FROM students;

-- Найти студентов, в имени которых есть буква 'E' (регистронезависимо, русская и английская)
SELECT * FROM students WHERE name ~* 'Е|E';

-- Найти студентов, у которых возраст меньше чем их ID
SELECT * FROM students WHERE age < id;

-- Отсортировать студентов по возрасту (по возрастанию)
SELECT * FROM students ORDER BY age;

