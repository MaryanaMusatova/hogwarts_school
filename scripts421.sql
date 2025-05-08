-- Ограничение: возраст студента не может быть меньше 16 лет
ALTER TABLE Students ADD CONSTRAINT age_check CHECK (age >= 16);

-- Ограничение: имена студентов должны быть уникальными и не равны нулю
ALTER TABLE Students ADD CONSTRAINT name_unique UNIQUE (name);
ALTER TABLE Students ALTER COLUMN name SET NOT NULL;

-- Ограничение: пара "название - цвет факультета" должна быть уникальной
ALTER TABLE Faculty ADD CONSTRAINT name_color_unique UNIQUE (name, color);

-- Значение по умолчанию: если возраст не указан, устанавливается 20 лет
ALTER TABLE Students ALTER COLUMN age SET DEFAULT 20;