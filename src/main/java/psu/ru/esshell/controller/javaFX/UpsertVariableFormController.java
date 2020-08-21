package psu.ru.esshell.controller.javaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jooq.util.maven.example.enums.KindVariable;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Controller;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.VariablePojo;
import psu.ru.esshell.service.DomainService;
import psu.ru.esshell.service.VariableService;

import java.io.IOException;
import java.util.Objects;

import static psu.ru.esshell.service.FormShowService.showError;

@Controller
public class UpsertVariableFormController {

    @FXML
    private AnchorPane anch;

    @FXML
    private TextField tfdName;

    @FXML
    private ComboBox<DomainPojo> cbDomains;

    @FXML
    private TableView<DomainValue> tvDomainValues;

    @FXML
    private TableColumn<DomainValue, Integer> clmnNumber;

    @FXML
    private TableColumn<DomainValue, String> clmnValue;

    @FXML
    private Button btEditDomain;

    @FXML
    private Button btAddDomain;

    @FXML
    private RadioButton rbtOut;

    @FXML
    private RadioButton rbtInp;

    @FXML
    private RadioButton rbtOutInp;

    @FXML
    private TextField tfdQuestion;

    @FXML
    private Button btOk;

    @FXML
    private Button btCancel;

    private VariablePojo variablePojo = new VariablePojo();
    private DomainService domainService;
    private VariableService variableService;
    private boolean isEdit;
    private String oldName;
    private boolean isGoodQuestion = false;

    public class StringConverterDomain extends StringConverter<DomainPojo> {

        @Override
        public String toString(DomainPojo domainPojo) {
            return domainPojo == null ? null : domainPojo.getName();
        }

        @Override
        public DomainPojo fromString(String string) {
            return null;
        }

    }

    @FXML
    void initialize(VariablePojo variablePojo, DomainService domainService, VariableService variableService) {
        calibrationColumns();

        this.domainService = domainService;
        this.variableService = variableService;

        isEdit = variablePojo.getId() != null;
        oldName = variablePojo.getName();

        this.variablePojo.setId(variablePojo.getId());
        this.variablePojo.setEsId(variablePojo.getEsId());
        this.variablePojo.setName(variablePojo.getName());
        this.variablePojo.setOrder(variablePojo.getOrder());
        this.variablePojo.setDomainName(variablePojo.getDomainName());
        this.variablePojo.setDomain(variablePojo.getDomain());
        this.variablePojo.setKindName(variablePojo.getKindName());
        this.variablePojo.setKind(variablePojo.getKind());
        this.variablePojo.setDomainId(variablePojo.getDomainId());
        this.variablePojo.setQuestion(variablePojo.getQuestion());

        // Радиобаттоны
        rbtOut.setOnAction(event -> {
            selectRbt(KindVariable.output, true);
        });

        rbtInp.setOnAction(event -> {
            selectRbtInp(KindVariable.requested);
        });

        rbtOutInp.setOnAction(event -> {
            selectRbtInp(KindVariable.output_requested);
        });

        tfdName.textProperty().addListener((observable, oldValue, newValue) -> {
            if ((oldValue + "?").equals(tfdQuestion.getText().trim())) {
                tfdQuestion.setText(tfdName.getText() + "?");
            }
        });

        if (!isEdit) {
            tfdName.setText(String.format("Variable%d", variableService.getCount(this.variablePojo.getEsId()) + 1));
            fillCbDomains();
            rbtOut.getToggleGroup().selectToggle(rbtOut);
            this.variablePojo.setKind(KindVariable.output);
        } else {
            tfdName.setText(this.variablePojo.getName());
            fillData();
        }

        cbDomains.setConverter(new StringConverterDomain());
        cbDomains.setCellFactory(p -> new ListCell <DomainPojo> () {
            @Override
            protected void updateItem(DomainPojo item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        cbDomains.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.variablePojo.setDomainId(newValue.getId());
            }
            this.variablePojo.setDomain(newValue);
            fillTvDomains();
        });

        btEditDomain.setOnAction(event -> {
            editDomain();
        });

        btAddDomain.setOnAction(event -> {
            addDomain();
        });

        btCancel.setOnAction(event -> close());

        btOk.setOnAction(event -> {
            addOrUpdate();
        });

        btOk.getScene().setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                addOrUpdate();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                close();
            }
        });

        btAddDomain.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                addDomain();
            }
        });

        btEditDomain.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                editDomain();
            }
        });

        rbtOut.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                selectRbt(KindVariable.output, true);
            }
        });

        rbtInp.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                selectRbtInp(KindVariable.requested);
            }
        });

        rbtOutInp.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                selectRbtInp(KindVariable.output_requested);
            }
        });
    }

    private void selectRbt(KindVariable output, boolean b) {
        this.variablePojo.setKind(output);
        tfdQuestion.setDisable(b);
    }

    private void selectRbtInp(KindVariable requested) {
        selectRbt(requested, false);
        if (!tfdQuestion.getText().isEmpty()) {
            return;
        }
        tfdQuestion.setText(this.variablePojo.getQuestion() != null
                && !this.variablePojo.getQuestion().isEmpty() ? this.variablePojo.getQuestion() : tfdName.getText() + "?");
    }

    private void editDomain() {
        DomainPojo domainPojo = cbDomains.getSelectionModel().getSelectedItem();
        if (domainPojo == null) {
            showError("Выберите домен для измения!", anch);
            return;
        }
        int index = cbDomains.getSelectionModel().getSelectedIndex();
        domainPojo.setValues(domainService.getDomainValues(domainPojo.getId()));
        openDialogUpsertDomain("Изменение домена", domainPojo);
        cbDomains.getSelectionModel().select(index);
    }

    private void addDomain() {
        openDialogUpsertDomain("Добавление домена", new DomainPojo(this.variablePojo.getEsId()));
    }

    private void addOrUpdate() {
        if (validateData()) {
            this.variablePojo.setName(tfdName.getText());

            if (!rbtOut.isSelected()) {
                this.variablePojo.setQuestion(tfdQuestion.getText());
            }

            if (this.variablePojo.getId() == null) {
                variableService.add(this.variablePojo);
            } else {
                variableService.save(this.variablePojo);
            }
            close();
        }
    }

    private void calibrationColumns() {
        int widthClmnNumber = 40;

        clmnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        clmnNumber.setCellValueFactory(new PropertyValueFactory<>("order"));

        clmnNumber.setPrefWidth(widthClmnNumber);
        clmnValue.prefWidthProperty().bind(tvDomainValues.widthProperty().subtract(widthClmnNumber));
    }

    private void fillData() {
        tfdName.setText(variablePojo.getName() != null ? variablePojo.getName() : "");
        tfdQuestion.setText(variablePojo.getQuestion() != null ? variablePojo.getQuestion() : "");

        switch (variablePojo.getKind()) {
            case output: {
                rbtOut.getToggleGroup().selectToggle(rbtOut);
                tfdQuestion.setDisable(true);
                break;
            }
            case requested: {
                rbtInp.getToggleGroup().selectToggle(rbtInp);
                tfdQuestion.setDisable(false);
                break;
            }
            case output_requested:{
                rbtOutInp.getToggleGroup().selectToggle(rbtOutInp);
                tfdQuestion.setDisable(false);
                break;
            }
        }

        fillCbDomains();

        cbDomains.getSelectionModel().select(variablePojo.getDomain());

        fillTvDomains();
    }

    private void fillTvDomains() {
        tvDomainValues.getItems().clear();

        for (DomainValue domainValue : domainService.getDomainValues(variablePojo.getDomainId())) {
            tvDomainValues.getItems().add(domainValue);
        }
    }

    private void fillCbDomains() {
        cbDomains.getItems().clear();

        for (DomainPojo domainPojo : variableService.getListDomains(this.variablePojo.getEsId())){
            cbDomains.getItems().add(domainPojo);
        }
    }

    private void fillDomains() {
        fillCbDomains();

        cbDomains.getSelectionModel().select(cbDomains.getItems().size()-1);
        fillTvDomains();
    }

    private void openDialogUpsertDomain(String title, DomainPojo domainPojo) {
        anch.setDisable(true);
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpsertDomainFrm.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage upsertDomainFrm = new Stage();
        upsertDomainFrm.initModality(Modality.APPLICATION_MODAL);
        upsertDomainFrm.setAlwaysOnTop(true);
        upsertDomainFrm.setResizable(false);
        upsertDomainFrm.setTitle(title);
        upsertDomainFrm.setScene(new Scene(root, 401, 568));
        loader. <UpsertDomainFormController>getController().initialize(domainPojo, domainService);
        upsertDomainFrm.setOnHidden(event -> {
            fillDomains();
            anch.setDisable(false);
        });
        upsertDomainFrm.setOnCloseRequest(event -> {
            fillDomains();
            anch.setDisable(false);
        });
        upsertDomainFrm.showAndWait();
    }

    private boolean validateData() {
        String msg = "";
        if (tfdName.getText() == null || tfdName.getText().trim().equals("")) {
            msg += "Введите корректное наименование переменной!\n";
        } else if ((!isEdit || !Objects.equals(oldName, tfdName.getText())) && variableService.checkName(tfdName.getText(), this.variablePojo.getEsId())){
            msg += "Переменная с таким наименованием уже существует!\n";
        }

        if (cbDomains.getSelectionModel().getSelectedItem() == null) {
            msg += "Выберите домен значений!\n";
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