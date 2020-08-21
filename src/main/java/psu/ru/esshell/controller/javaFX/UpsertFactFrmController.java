package psu.ru.esshell.controller.javaFX;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;
import psu.ru.esshell.model.pojos.VariablePojo;
import psu.ru.esshell.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpsertFactFrmController {

    public class StringConverterVariable extends StringConverter<VariablePojo> {

        @Override
        public String toString(VariablePojo variablePojo) {
            return variablePojo == null ? null : variablePojo.getName();
        }

        @Override
        public VariablePojo fromString(String string) {
            return null;
        }

    }

    public class StringConverterDomainValue extends StringConverter<DomainValue> {

        @Override
        public String toString(DomainValue domainValue) {
            return domainValue == null ? null : domainValue.getValue();
        }

        @Override
        public DomainValue fromString(String string) {
            return null;
        }

    }

    @FXML
    private AnchorPane anch;

    @FXML
    private ComboBox<VariablePojo> cbVariables;

    @FXML
    private Label lblOp;

    @FXML
    private ComboBox<DomainValue> cbValues;

    @FXML
    private Button btOk;

    @FXML
    private Button btCancel;

    @FXML
    private Button btAddVariable;

    private FactPojo factPojo = new FactPojo();

    private DomainService domainService;

    private VariableService variableService;

    private RuleService ruleService;

    private FactService factService;

    private boolean isEdit;

    private boolean isCondition;

    private Long esId;

    private List<FactPojo> factPojoList;

    private Integer oldCountVariables;

    void initialize(List<FactPojo> factPojoList, FactPojo factPojo, Long esId, boolean isEdit, boolean isCondition, DomainService domainService, VariableService variableService, RuleService ruleService, FactService factService) {
        if (!isCondition) {
            Platform.runLater(() -> lblOp.setText(":="));
        }

        this.isEdit = isEdit;

        this.factPojo.setId(factPojo.getId());
        this.factPojo.setVariableId(factPojo.getVariableId());
        this.factPojo.setDomainValueId(factPojo.getDomainValueId());
        this.factPojo.setVariableName(factPojo.getVariableName());
        this.factPojo.setVariableValue(factPojo.getVariableValue());
        this.factPojo.setContent(factPojo.getContent());

        this.isCondition = isCondition;
        this.esId = esId;

        this.factPojoList = factPojoList;

        this.domainService = domainService;
        this.variableService = variableService;
        this.ruleService = ruleService;
        this.factService = factService;

        btOk.setOnAction(event -> {
            addOrUpdate(factPojo, isCondition);
        });

        btCancel.setOnAction(event -> {
            close();
        });

        cbVariables.setConverter(new StringConverterVariable());
        cbVariables.setCellFactory(p -> new ListCell<VariablePojo>() {
            @Override
            protected void updateItem(VariablePojo item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        cbValues.setConverter(new StringConverterDomainValue());
        cbValues.setCellFactory(p -> new ListCell<DomainValue>() {
            @Override
            protected void updateItem(DomainValue item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getValue());
                } else {
                    setText(null);
                }
            }
        });

        cbVariables.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillCbValues(newValue);
        });

        if (isEdit) {
            fillCbVariables();
            cbVariables.getSelectionModel().select(variableService.getVariableById(this.factPojo.getVariableId()));
        } else {
            fillCbVariables();
        }

        btOk.getScene().setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                addOrUpdate(factPojo, isCondition);
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                close();
            }
        });

        btOk.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                addOrUpdate(factPojo, isCondition);
            }
        });

        btCancel.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                close();
            }
        });

        btAddVariable.setOnAction(event -> {
            openDialogUpsertVariable("Добавление переменной", new VariablePojo(esId));
        });
    }

    private void addOrUpdate(FactPojo factPojo, boolean isCondition) {
        if (validateData()) {
            VariablePojo variablePojo = cbVariables.getSelectionModel().getSelectedItem();
            DomainValue domainValue = cbValues.getSelectionModel().getSelectedItem();

            factPojo.setVariableId(variablePojo.getId());
            factPojo.setVariableName(variablePojo.getName());
            factPojo.setDomainValueId(domainValue.getId());
            factPojo.setVariableValue(domainValue.getValue());
            factPojo.setContent(String.format("%s %s %s", variablePojo.getName(), (isCondition ? "==" : ":="), domainValue.getValue()));
            close();
        }
    }

    private boolean validateData() {
        String msg = "";
        VariablePojo variable = cbVariables.getSelectionModel().getSelectedItem();
        DomainValue domainValue = cbValues.getSelectionModel().getSelectedItem();
        if (variable == null) {
            msg += "Выберите переменную!\n";
        }

        if (domainValue == null) {
            msg += "Выберите значение!\n";
        }

        if (variable != null && domainValue != null) {
            List<FactPojo> list = factPojoList.stream().filter(x ->
                    Objects.equals(x.getVariableId(), variable.getId())
                            && Objects.equals(x.getDomainValueId(), domainValue.getId()))
                    .collect(Collectors.toList());

            if (list.size() != 0) {
                if (!isEdit || !factPojo.getId().equals(list.get(0).getId())) {
                    msg += "Такое " + (isCondition ? "условие" : "заключение") + " уже существует!";
                }
            }
        }

        if (!msg.isEmpty()) {
            FormShowService.showError(msg, anch);
            return false;
        }

        return true;
    }

    private void fillCbVariables() {
        cbVariables.getItems().clear();

        List<VariablePojo> variablePojoList =  isCondition ? variableService.getList(esId) : variableService.getOnlyOutputVariables(esId);

        for (VariablePojo variablePojo : variablePojoList) {
            cbVariables.getItems().add(variablePojo);
        }
    }

    private void fillCbValues(VariablePojo variablePojo) {
        cbValues.getItems().clear();

        if (variablePojo == null) {
            return;
        }

        List<DomainValue> domainValues = domainService.getDomainValues(variablePojo.getDomainId());

        for (DomainValue domainValue : domainValues) {
            cbValues.getItems().add(domainValue);
        }

        if (isEdit) {
            cbValues.getSelectionModel().select(domainService.getDomainValueById(factPojo.getDomainValueId()));
        }
    }

    private void close() {
        // close the form
        btOk.getScene().getWindow().hide();
    }

    private void openDialogUpsertVariable(String title, VariablePojo variablePojo) {
        anch.setDisable(true);
        oldCountVariables = variableService.getCount(esId);

        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpsertVariableFrm.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage upsertVariableFrm = new Stage();
        upsertVariableFrm.initModality(Modality.APPLICATION_MODAL);
        upsertVariableFrm.setAlwaysOnTop(true);
        upsertVariableFrm.setResizable(false);
        upsertVariableFrm.setTitle(title);
        upsertVariableFrm.setScene(new Scene(root, 401, 568));
        loader. <UpsertVariableFormController>getController().initialize(variablePojo, domainService, variableService);
        upsertVariableFrm.setOnHidden(event -> {
            closed();
        });
        upsertVariableFrm.setOnCloseRequest(event -> {
            closed();
        });
        upsertVariableFrm.showAndWait();
    }

    private void closed() {
        anch.setDisable(false);
        if (!Objects.equals(oldCountVariables, variableService.getCount(esId))) {
            fillCbVariables();
            cbVariables.getSelectionModel().select(cbVariables.getItems().size() - 1);
        }
    }
}
