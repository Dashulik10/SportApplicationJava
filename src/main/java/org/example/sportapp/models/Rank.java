package org.example.sportapp.models;

/*
* Enum - (перечисление) специальный тип данных, который
    помогает нам определять список ограниченного
    и фиксированного набора констант (Например, дни недели)
* Каждая константа класса ENUM является объектом,
    экземпляром класса ENUM
* Методы, определённые в стандарте : values(), ordinal(), name(), valueOf()
*   values() - озвращает массив всех перечислений
*   ordinal() - нумерация в массивчике начинается с нуля, так вот он возвращает порядковый номер
*   name() - возвращает имя элемента, которое задано в String (то есть у меня эта переменная имеет имя)
*   valueOf(String name) - возвращает элемент по названию
* */

public enum Rank {
    // Это и есть наши константы
    BEGINNER("1-й Юношеский"),
    INTERMEDIATE("2-й Разряд"),
    ADVANCED("КМС"),
    PROFESSIONAL("Мастер Спорта");

    private final String displayName; // Поле для хранения имени

    // Приватный конструктор (только внутри Enum)
    private Rank(String displayName) {
        this.displayName = displayName;
    }

    // Метод для получения имени
    public String getDisplayName() {
        return displayName;
    }

    // Когда мы будем использовать наши enum(не значения, а имена) в UI
    @Override
    public String toString() {
        return displayName;
    }
}
