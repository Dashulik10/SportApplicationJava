package org.example.sportapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.sportapp.dataBase.AthleteDAO;
import org.example.sportapp.models.Athlete;

import java.util.List;
import java.util.Optional;

public class DeleteAthleteController {

    @FXML
    private TableView<Athlete> athletesTable;

    @FXML
    private TableColumn<Athlete, Integer> idColumn;

    @FXML
    private TableColumn<Athlete, String> secondNameColumn;

    @FXML
    private TableColumn<Athlete, String> firstNameColumn;

    @FXML
    private TableColumn<Athlete, String> rankColumn;

    private final AthleteDAO athleteDAO = new AthleteDAO();

    private final ObservableList<Athlete> athletesData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        secondNameColumn.setCellValueFactory(new PropertyValueFactory<>("second_name"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        athletesTable.setItems(athletesData);
    }

    // Чтобы передать список всех спортиков (использую в главном контроллере)
    public void setAthletesData(List<Athlete> athletes) {
        athletesData.setAll(athletes);
    }

    @FXML
    private void handleDelete() {
        // тут забирает строчку, на которую я жмякну и преобразует её в атлета-котлета
        Athlete selectedAthlete = athletesTable.getSelectionModel().getSelectedItem();
        if (selectedAthlete == null) {
            // Предупреждающее окошко
            Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, выберите спортсмена для удаления!", ButtonType.OK);
            alert.show();
            return;
        }

        // Подтверждение удаления (с типом "Подтверждение")
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Вы точно хотите удалить выбранного спортсмена?");
        Optional<ButtonType> result = confirmation.showAndWait();

        // Проверяем, чтобы кто-то что-то жмякнул и подтвердил)
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = athleteDAO.deleteAthlete(
                        selectedAthlete.getSecond_name(),
                        selectedAthlete.getFirst_name(),
                        selectedAthlete.getTeam_cast(),
                        selectedAthlete.getSport(),
                        selectedAthlete.getRank()
                );
                if (success) {
                    athletesData.remove(selectedAthlete); // Если всё ок, удаляем из таблицы
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Удаление прошло успешно!", ButtonType.OK);
                    successAlert.show();
                } else {
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR, "Ошибка: не удалось удалить спортсмена.", ButtonType.OK);
                    failureAlert.show();
                }
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Ошибка при удалении: " + e.getMessage(), ButtonType.OK);
                errorAlert.show();
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) athletesTable.getScene().getWindow();
        stage.close();
    }
}