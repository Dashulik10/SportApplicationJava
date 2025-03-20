package org.example.sportapp.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.sportapp.dataBase.AthleteDAO;
import org.example.sportapp.models.Athlete;
import org.example.sportapp.models.Rank;
import org.example.sportapp.models.TeamCast;

import java.sql.SQLException;
import java.util.List;

//убрать колонки ранг, спорт и титул, cast

public class AddAthleteController {

    @FXML
    private TextField secondNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField patronymicField;
    @FXML
    private ComboBox<TeamCast> teamCastComboBox;
    @FXML
    private TextField positionField;
    @FXML
    private ComboBox<Integer> titleComboBox;

    @FXML
    private ComboBox<String> sportComboBox;
    @FXML
    private ComboBox<Rank> rankComboBox;

    private final AthleteDAO athleteDAO = new AthleteDAO();

    private AthletesTableController tableController; // для обновления виджета после добавления спортсмена

    public void setTableController(AthletesTableController tableController) {
        this.tableController = tableController;
    }

    @FXML
    public void initialize() {
        // Инициализация значений для выпадающих списков
        initializeRankComboBox();
        initializeSportComboBox();
        initializeTitleComboBox();
        initializeTeamCastComboBox();
    }

    private void initializeRankComboBox() {
        // Загрузка доступных значений из Enum Rank
        // Метод из класса `FXCollections`,
        // который используется для создания списка объектов,
        // подконтрольного механизмам JavaFX
        // (например, автоматической привязке интерфейса, обновлению при изменении и т. д.).
        rankComboBox.setItems(FXCollections.observableArrayList(Rank.values()));
    }

    private void initializeTeamCastComboBox(){
        teamCastComboBox.setItems(FXCollections.observableArrayList(TeamCast.values()));
    }

    private void initializeSportComboBox() {
        // Попытка загрузить виды спорта из базы данных
        try {
            List<String> sports = athleteDAO.getAllSports();
            sportComboBox.setItems(FXCollections.observableArrayList(sports));
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке видов спорта: " + e.getMessage());
        }
    }

    private void initializeTitleComboBox() {
        titleComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }


    // ОСНОВА
    @FXML
    private void handleAddAthlete() {
        try {
            Long sportId = athleteDAO.getSportIdByName(sportComboBox.getValue());
            Athlete athlete = new Athlete(
                    0,
                    secondNameField.getText(),
                    firstNameField.getText(),
                    patronymicField.getText(),
                    teamCastComboBox.getValue().getDisplayName(),
                    positionField.getText(),
                    titleComboBox.getValue(),
                    sportId,
                    rankComboBox.getValue().getDisplayName()
            );
            athleteDAO.addAthlete(athlete);

            // Обновляем главную таблицу (через основной контроллер)
            if (tableController != null) {
                tableController.loadAthletesFromDatabase();
            }

            close();
        } catch (Exception e) {
            System.err.println("Ошибка добавления спортсмена: " + e.getMessage());
        }
    }


    @FXML
    private void close() {
        Stage stage = (Stage) secondNameField.getScene().getWindow();
        stage.close();
    }
}