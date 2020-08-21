package psu.ru.esshell.controller.javaFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

@Controller
public class ConfirmationWindowController {


    @FXML
    private Text message;

    @FXML
    private Button btOk;

    @FXML
    private Button btCancel;

    @FXML
    void initialize(String msg) {
        message.setText(msg);

        btOk.setOnAction(event -> close());
    }

    private void close() {
        // close the form
        btOk.getScene().getWindow().hide();
    }
}