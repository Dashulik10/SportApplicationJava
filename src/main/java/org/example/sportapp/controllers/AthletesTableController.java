package org.example.sportapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sportapp.dataBase.AthleteDAO;
import org.example.sportapp.models.Athlete;
import org.example.sportapp.models.AthleteXMLLoader;
import org.example.sportapp.models.AthleteXMLSaver;

import java.io.File;
import java.util.List;
import java.util.Map;

public class AthletesTableController {

    private int ITEMS_PER_PAGE = 10; // Количество записей на страницу по умолчанию
    private int currentPage = 0; // Текущая страница, начинается с 0

    private ObservableList<Athlete> paginatedData = FXCollections.observableArrayList(); // Данные текущей страницы

    private final AthleteDAO athleteDAO = new AthleteDAO();

    private final ObservableList<Athlete> athletesData = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox<Integer> itemsPerPageChoiceBox;

    @FXML
    private Label totalRecordsLabel; // общее кол-во записей

    @FXML
    private Label currentPageLabel; // номер текущей страницы

    @FXML
    private TableView<Athlete> athletesTable;

    @FXML
    private TableColumn<Athlete, Integer> idColumn;

    @FXML
    private TableColumn<Athlete, String> secondNameColumn;

    @FXML
    private TableColumn<Athlete, String> firstNameColumn;

    @FXML
    private TableColumn<Athlete, String> patronymicColumn;

    @FXML
    private TableColumn<Athlete, String> teamCastColumn;

    @FXML
    private TableColumn<Athlete, String> positionColumn;

    @FXML
    private TableColumn<Athlete, Integer> titleColumn;

    @FXML
    private TableColumn<Athlete, String> sportColumn;

    @FXML
    private TableColumn<Athlete, String> rankColumn;

    //Инициализация таблицы + загрузка
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        secondNameColumn.setCellValueFactory(new PropertyValueFactory<>("second_name"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        teamCastColumn.setCellValueFactory(new PropertyValueFactory<>("team_cast"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        sportColumn.setCellValueFactory(new PropertyValueFactory<>("sport"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        athletesTable.setItems(paginatedData);

        // Настройка выбора записей на странице
        itemsPerPageChoiceBox.getItems().addAll(5, 10, 20, 50);
        itemsPerPageChoiceBox.setValue(ITEMS_PER_PAGE); // Устанавливаем текущее число записей
        itemsPerPageChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ITEMS_PER_PAGE = newValue; // Обновляем число записей на странице
            currentPage = 0; // Сбрасываем на первую страницу
            updateTableWithCurrentPage(); // Обновляем отображение
        });

        loadAthletesFromDatabase();
        updateTableWithCurrentPage(); // Обновляем таблицу данными текущей страницы
    }

    private void updateTableWithCurrentPage() {
        int fromIndex = currentPage * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, athletesData.size()); // Лимит списка

        if (fromIndex <= toIndex) {
            paginatedData.setAll(athletesData.subList(fromIndex, toIndex)); // Устанавливаем данные этой страницы
        }

        // Обновляем информацию о страницах и записях
        int totalRecords = athletesData.size(); // количество записей
        int totalPages = (int) Math.ceil((double) totalRecords / ITEMS_PER_PAGE); //количесвто страниц

        currentPageLabel.setText((currentPage + 1) + " / " + totalPages); // Обновляем номер текущей страницы
        totalRecordsLabel.setText(String.valueOf(totalRecords)); // Обновляем общее число записей
    }

    @FXML
    private void handleFirstPage() {
        if (currentPage > 0) {
            currentPage = 0;
            updateTableWithCurrentPage();
        }
    }

    @FXML
    private void handleLastPage() {
        int totalRecords = athletesData.size();
        currentPage = (totalRecords - 1) / ITEMS_PER_PAGE; // Вычисляем последнюю страницу
        updateTableWithCurrentPage();
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateTableWithCurrentPage();
        }
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < athletesData.size()) {
            currentPage++;
            updateTableWithCurrentPage();
        }
    }

    //Загрука таблицы
    //Через DAO делаю запрос в бд, возвращаю массив, через геттеры передаю массив таблице
    public void loadAthletesFromDatabase() {
        try {
            List<Athlete> athletes = athleteDAO.searchAthletes(null, null, null, null, null);
            athletesData.setAll(athletes); // Обновление полного списка атлетов
            totalRecordsLabel.setText(String.valueOf(athletes.size())); // Выводим общее количество записей
            currentPage = 0; // Сбрасываем страницу
            updateTableWithCurrentPage(); // Обновляем таблицу новой страницей
        } catch (Exception e) {
            System.err.println("Ошибка загрузки атлетов из базы данных: " + e.getMessage());
        }
    }

    public void openAddAthleteScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogAddView.fxml"));
            Scene scene = new Scene(loader.load());

            AddAthleteController controller = loader.getController();
            controller.setTableController(this);

            Stage stage = new Stage();
            stage.setTitle("Добавить атлета");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL); // Установим модальное поведение (все остальные окна блокируются
            stage.initOwner(athletesTable.getScene().getWindow()); // Установим родителя
            stage.showAndWait(); // запускаем новое окно (пока пользователь его не закроет, ждем)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeleteAthleteScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogDeleteView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Удаление спортсмена");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(athletesTable.getScene().getWindow());

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            DeleteAthleteController controller = loader.getController();

            // Обновляем данные перед передачей
            ObservableList<Athlete> currentData = FXCollections.observableArrayList(athleteDAO.searchAthletes(null, null, null, null, null));
            controller.setAthletesData(currentData);

            stage.showAndWait();

            // Обновляем главную таблицу!!!!!
            loadAthletesFromDatabase();
        } catch (Exception e) {
            System.err.println("Ошибка открытия окна удаления: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void openSearchAthleteScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogSearchView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Поиск спортсменов");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(athletesTable.getScene().getWindow()); // Родительское окно

            SearchAthleteController controller = loader.getController();
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Ошибка открытия окна поиска: " + e.getMessage());
        }
    }


    @FXML
    private void handleLoadXML() {
        XMLController xmlController = new XMLController();

        try {
            List<Athlete> athletes = xmlController.loadFromXML();
            if (athletes != null) {
                athleteDAO.addMultipleAthletes(athletes);
                loadAthletesFromDatabase();
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки XML: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveXML() {
        XMLController xmlController = new XMLController();

        try {
            xmlController.saveToXML(athletesTable.getItems());
        } catch (Exception e) {
            System.err.println("Ошибка сохранения XML: " + e.getMessage());
        }
    }

    @FXML
    private void openTreeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TreeViewDialog.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Дерево спортсменов");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(athletesTable.getScene().getWindow());

            Scene scene = new Scene(loader.load());
            TreeController controller = loader.getController();

            // Загружаем дерево из DAO
            Map<String, Map<String, List<Athlete>>> treeData = athleteDAO.getAthleteTree();
            controller.setTreeData(treeData); // Передаём данные

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Ошибка открытия окна дерева: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
