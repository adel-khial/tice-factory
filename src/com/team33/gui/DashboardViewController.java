package com.team33.gui;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardViewController implements Controller {

    private MainApp mainApp;

    @FXML
    private Label noFile;

    @FXML
    private JFXListView<String> fileLists;

    @FXML
    public void initialize() {
        if(!RecentFileHandler.readFile().isEmpty()) {
            noFile.setVisible(false);
            for(String s : RecentFileHandler.readFile()) {
                fileLists.getItems().add(s);
            }
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
