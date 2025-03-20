package org.example.sportapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.example.sportapp.models.Athlete;

import java.util.List;
import java.util.Map;

public class TreeController {

    @FXML
    private TreeView<String> athletesTreeView;
    // отображает данные в виде вложенной иерархической структуры


    public void setTreeData(Map<String, Map<String, List<Athlete>>> treeData) {
        TreeItem<String> root = new TreeItem<>("Спортсмены");
        root.setExpanded(true); // корень, по умолчанию развернула

        treeData.forEach((sport, teams) -> {
            TreeItem<String> sportItem = new TreeItem<>(sport);
            sportItem.setExpanded(true);

            teams.forEach((team, athletes) -> {
                TreeItem<String> teamItem = new TreeItem<>(team);

                for (Athlete athlete : athletes) {
                    String athleteName = athlete.getSecond_name() + " " + athlete.getFirst_name();
                    TreeItem<String> athleteItem = new TreeItem<>(athleteName);
                    teamItem.getChildren().add(athleteItem);
                }

                sportItem.getChildren().add(teamItem);
            });

            root.getChildren().add(sportItem);
        });

        athletesTreeView.setRoot(root); // усталновили корень
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) athletesTreeView.getScene().getWindow();
        stage.close();
    }
}