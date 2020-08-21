package psu.ru.esshell.controller.javaFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

@Controller
public class ErrorWindowController {


    @FXML
    private Text message;

    @FXML
    private Button btOk;

    @FXML
    public void initialize(String msg) {
        message.setText(msg);

        btOk.setOnAction(event -> close());

        btOk.getScene().setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.ESCAPE)) {
                close();
            }
        });
    }

    private void close() {
        // close the form
        btOk.getScene().getWindow().hide();
    }
}