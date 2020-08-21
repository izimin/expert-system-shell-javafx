package psu.ru.esshell.controller.javaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Controller;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.service.DomainService;

import java.io.IOException;
import java.util.Objects;

import static psu.ru.esshell.service.FormShowService.showError;

@Controller
public class UpsertDomainFormController {

    @FXML
    private AnchorPane anch;

    @FXML
    private TextField tfdName;

    @FXML
    private TableView<DomainValue> tvDomainValues;

    @FXML
    private TableColumn<DomainValue, Integer> clmnNumber;

    @FXML
    private TableColumn<DomainValue, String> clmnValue;

    @FXML
    private TextField tfdValue;

    @FXML
    private Button btAdd;

    @FXML
    private Button btEdit;

    @FXML
    private Button btDelete;

    @FXML
    private Button btOk;

    @FXML
    private Button btCancel;

    private DomainPojo domainPojo = new DomainPojo();

    private DomainService domainService;

    private boolean isEdit;

    private String oldName;

    private String oldDomainValue;

    private Integer order;

    @FXML
    void initialize(DomainPojo domainPojo, DomainService domainService) {
        calibrationColumns();

        isEdit = domainPojo.getId() != null;
        oldName = domainPojo.getName();

        this.domainService = domainService;

        this.domainPojo.setId(domainPojo.getId());
        this.domainPojo.setEsId(domainPojo.getEsId());
        this.domainPojo.setName(domainPojo.getName());
        this.domainPojo.setValues(domainPojo.getValues());
        this.domainPojo.setOrder(domainPojo.getOrder());

        if (!isEdit) {
            tfdName.setText(String.format("Domain%d",  this.domainService.getCountDomains(domainPojo.getEsId()) + 1));
        } else {
            tfdName.setText(this.domainPojo.getName());
            fillValuesDomain();
        }

        btCancel.setOnAction(event -> close());

        btOk.setOnAction(event -> {
            addOrUpdate();
        });

        btAdd.setOnAction(event -> {
            addValue();
        });

        btEdit.setOnAction(event -> {
            editValue();
        });

        btDelete.setOnAction(event -> {
            deleteValue();
        });

        tvDomainValues.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setTfdValue();
        });

        tfdValue.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addValue();
            }
        });

        btOk.getScene().setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                addOrUpdate();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                close();
            }
        });

        tvDomainValues.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteValue();
            }
        });

        btOk.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                addOrUpdate();
            }
        });

        btCancel.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                close();
            }
        });

        btAdd.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addValue();
            }
        });

        btEdit.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editValue();
            }
        });

        btDelete.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteValue();
            }
        });
    }

    private void addOrUpdate() {
        if (validateData()) {
            this.domainPojo.setName(tfdName.getText());
            if (!isEdit) {
                this.domainService.addDomain(this.domainPojo);
            } else {
                this.domainService.saveDomain(this.domainPojo);
            }
            close();
        }
    }

    private void deleteValue() {
        DomainValue domainValue = tvDomainValues.getSelectionModel().getSelectedItem();
        if (domainValue == null) {
            showError("Выберите знаение домена для удаления!", anch);
            return;
        }

        this.domainPojo.getValues().remove(domainValue);
        tvDomainValues.getItems().remove(domainValue);

        for (int i = 0; i < tvDomainValues.getItems().size(); i++) {
            domainPojo.getValues().get(i).setOrder(i+1);
            tvDomainValues.getItems().get(i).setOrder(i+1);
        }

        tfdValue.setText("");
    }

    private void editValue() {
        DomainValue domainValue = tvDomainValues.getSelectionModel().getSelectedItem();
        if (domainValue == null) {
            showError("Выберите знаение домена для изменения!", anch);
            return;
        }

        if (validateValueDomain(true)) {
            int index = this.domainPojo.getValues().indexOf(domainValue);
            this.domainPojo.getValues().get(index).setValue(tfdValue.getText());
            tvDomainValues.getItems().set(index, domainPojo.getValues().get(index));
            tfdValue.setText("");
        }
    }

    private void addValue() {
        if (validateValueDomain(false)) {
            DomainValue domainValue = new DomainValue(null, tfdValue.getText(), null, this.domainPojo.getValues().size() + 1);
            this.domainPojo.getValues().add(domainValue);
            tvDomainValues.getItems().add(domainValue);
            tfdValue.setText("");
        }
    }

    private void calibrationColumns() {
        int widthClmnNumber = 40;

        clmnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        clmnNumber.setCellValueFactory(new PropertyValueFactory<>("order"));

        clmnNumber.setPrefWidth(widthClmnNumber);
        clmnValue.prefWidthProperty().bind(tvDomainValues.widthProperty().subtract(widthClmnNumber));
    }

    private void setTfdValue() {
        DomainValue domainValue = tvDomainValues.getSelectionModel().getSelectedItem();
        if (domainValue != null) {
            tfdValue.setText(domainValue.getValue());
            oldDomainValue = domainValue.getValue();
        }
    }

    private void fillValuesDomain() {
        tvDomainValues.getItems().clear();

        for (DomainValue domainValue : domainPojo.getValues()) {
            tvDomainValues.getItems().add(domainValue);
        }
    }

    private boolean validateValueDomain(boolean isEditValue) {
        String msg = "";
        if (tfdValue.getText() == null || tfdValue.getText().trim().equals("")) {
            msg = "Введите корректное значение домена!\n";
        } else if (this.domainPojo.getValues().stream().anyMatch(value -> value.getValue().equals(tfdValue.getText()))
            && !(isEditValue && tfdValue.getText().equals(oldDomainValue))) {
            msg = "Такое значение домена уже существует!\n";
        }

        if (msg.isEmpty()) {
            return true;
        } else {
            showError(msg, anch);
            return false;
        }
    }

    private boolean validateData() {
        String msg = "";
        if (tfdName.getText() == null || tfdName.getText().trim().equals("")) {
            msg += "Введите корректное наименование домена!\n";
        } else if ((!isEdit || !Objects.equals(oldName, tfdName.getText())) && this.domainService.checkName(tfdName.getText(), this.domainPojo.getEsId())) {
            msg += "Домен с таким наименованием уже существует!\n";
        }
        if (domainPojo.getValues().size() < 2) {
            msg += "Укажите минимум 2 значения домена!\n";
        }

        if (msg.isEmpty()) {
            return true;
        } else {
            showError(msg, anch);
            return false;
        }
    }

    private void close() {
        // close the form
        btOk.getScene().getWindow().hide();
    }
}
