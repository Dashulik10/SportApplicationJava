package org.example.sportapp.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args); // метод для запуска JavaFX - приложения
    }

    // это абстрактный метод для логики отображения
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузка, разбор и создание интерфейса
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Спортивное приложение");
        primaryStage.show();
    }
}
