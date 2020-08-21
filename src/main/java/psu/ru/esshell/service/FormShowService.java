package psu.ru.esshell.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import psu.ru.esshell.controller.javaFX.ErrorWindowController;

import java.io.IOException;

public class FormShowService {

    public static void showError(String msg, AnchorPane... anchorPanes) {
        if (anchorPanes.length != 0) {
            anchorPanes[0].setDisable(true);
        }
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(FormShowService.class.getResource("/ErrorWindow.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage errorFrm = new Stage();
        errorFrm.initModality(Modality.APPLICATION_MODAL);
        errorFrm.setAlwaysOnTop(true);
        errorFrm.setResizable(false);
        errorFrm.setTitle("Ошибка");
        errorFrm.setScene(new Scene(root, 407, 166));
        loader. <ErrorWindowController>getController().initialize(msg);

        errorFrm.setOnHidden(event -> {
            anchorPanes[0].setDisable(false);
        });

        errorFrm.setOnCloseRequest(event -> {
            anchorPanes[0].setDisable(false);
        });

        errorFrm.showAndWait();
    }
}
