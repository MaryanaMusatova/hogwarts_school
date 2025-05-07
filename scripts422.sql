-- Создание таблицы для машин
CREATE TABLE Car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- Создание таблицы для людей
CREATE TABLE Person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    has_license BOOLEAN NOT NULL,
    car_id INTEGER REFERENCES Car(id)
);