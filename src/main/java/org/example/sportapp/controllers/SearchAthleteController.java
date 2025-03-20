package org.example.sportapp.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sportapp.dataBase.AthleteDAO;
import org.example.sportapp.models.Athlete;
import org.example.sportapp.models.Rank;
import org.example.sportapp.models.TeamCast;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class SearchAthleteController {

    @FXML
    private TextField secondNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private ComboBox<TeamCast> teamCastComboBox;

    @FXML
    private ComboBox<String> sportComboBox;

    @FXML
    private ComboBox<Rank> rankComboBox;

    @FXML
    private TableView<Athlete> resultsTable;

    @FXML
    private TableColumn<Athlete, Long> idColumn;

    @FXML
    private TableColumn<Athlete, String> secondNameColumn;

    @FXML
    private TableColumn<Athlete, String> firstNameColumn;

    @FXML
    private TableColumn<Athlete, String> teamCastColumn;

    @FXML
    private TableColumn<Athlete, String> sportColumn;

    @FXML
    private TableColumn<Athlete, String> rankColumn;

    private final AthleteDAO athleteDAO = new AthleteDAO();
    // Для результатов поиска
    private final ObservableList<Athlete> searchResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeComboBoxes();
        initializeResultsTable();
    }

    private void initializeComboBoxes() {
        // Забираем значения, устанавливаем
        teamCastComboBox.setItems(FXCollections.observableArrayList(TeamCast.values()));
        rankComboBox.setItems(FXCollections.observableArrayList(Rank.values()));
        try {
            List<String> sports = athleteDAO.getAllSports();
            sportComboBox.setItems(FXCollections.observableArrayList(sports));
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки видов спорта: " + e.getMessage());
        }
    }

    private void initializeResultsTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        secondNameColumn.setCellValueFactory(new PropertyValueFactory<>("second_name"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        teamCastColumn.setCellValueFactory(new PropertyValueFactory<>("team_cast"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        // Устанавливаем кастомную фабрику для отображения названия вида спорта
        sportColumn.setCellValueFactory(cellData -> {
            Long sportId = cellData.getValue().getSport();

            if (sportId != null) {
                try {
                    String sportName = athleteDAO.getSportNameById(sportId);
                    return new SimpleStringProperty(sportName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return new SimpleStringProperty("Не указан");
        });

        resultsTable.setItems(searchResults);
    }

    @FXML
    public void handleSearch() {
        try {
            // Считываем значения из полей
            String lastName = secondNameField.getText().isEmpty() ? null : secondNameField.getText();
            String firstName = firstNameField.getText().isEmpty() ? null : firstNameField.getText();
            TeamCast teamCast = teamCastComboBox.getValue();
            String teamCastValue = teamCast == null ? null : teamCast.getDisplayName();
            String sport = sportComboBox.getValue();
            Rank rank = rankComboBox.getValue();
            String rankValue = rank == null ? null : rank.getDisplayName();

            // Преобразуем вид спорта из String в Long (sportId)
            Long sportId = null;
            if (sport != null) {
                sportId = athleteDAO.getSportIdByName(sport);
            }

            List<Athlete> results = athleteDAO.searchAthletes(
                    lastName,
                    firstName,
                    teamCastValue,
                    sportId,
                    rankValue
            );

            // Обновляем
            searchResults.setAll(results);

            if (results.isEmpty()) {
                showAlert("Результаты поиска", "По вашему запросу спортсмены не найдены.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Ошибка поиска", "Произошла ошибка при выполнении поиска: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleClose() {
        ((Stage) secondNameField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ObservableList<Athlete> getSearchResults() {
        return searchResults;
    }
}