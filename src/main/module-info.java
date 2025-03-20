module org.example.sportapp {
    requires javafx.controls;
    requires javafx.fxml;

    // Экспортируем пакеты, которые должны быть доступны другим модулям
    exports org.example.sportapp;
    exports org.example.sportapp.main;

    // Открываем все контроллеры для javafx.fxml
    opens org.example.sportapp.controllers to javafx.fxml;
}