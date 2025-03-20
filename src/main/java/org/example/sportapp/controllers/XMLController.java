package org.example.sportapp.controllers;

import javafx.stage.FileChooser;
import org.example.sportapp.models.Athlete;
import org.example.sportapp.models.AthleteXMLLoader;
import org.example.sportapp.models.AthleteXMLSaver;

import java.io.File;
import java.util.List;

public class XMLController {

    public List<Athlete> loadFromXML() throws Exception {
        FileChooser fileChooser = new FileChooser(); // окно для вызова файлов
        fileChooser.setTitle("Выберите XML файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML файлы", "*.xml"));

        File file = fileChooser.showOpenDialog(null); // открываем окно для выбора файла
        if (file == null) {
            return null;
        }

        AthleteXMLLoader loader = new AthleteXMLLoader();
        return loader.loadFromXML(file);
    }

    public void saveToXML(List<Athlete> athletes) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить XML файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML файлы", "*.xml"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            AthleteXMLSaver saver = new AthleteXMLSaver();
            saver.saveToXML(file, athletes);
        }
    }
}