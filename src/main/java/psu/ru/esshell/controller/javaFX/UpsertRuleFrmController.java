package psu.ru.esshell.controller.javaFX;

import java.io.IOException;
import java.util.Objects;

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
import org.jooq.util.maven.example.tables.Rule;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;
import psu.ru.esshell.service.DomainService;
import psu.ru.esshell.service.FactService;
import psu.ru.esshell.service.RuleService;
import psu.ru.esshell.service.VariableService;

import static psu.ru.esshell.service.FormShowService.showError;

public class UpsertRuleFrmController {

    @FXML
    private AnchorPane anch;

    @FXML
    private TextField tfdName;

    @FXML
    private TableView<FactPojo> tvConditions;

    @FXML
    private TableColumn<FactPojo, String> clmnCondition;

    @FXML
    private Button btAddCondition;

    @FXML
    private Button btEditCondition;

    @FXML
    private Button btDeleteCondition;

    @FXML
    private TableView<FactPojo> tvConclusions;

    @FXML
    private TableColumn<FactPojo, String> clmnConclusion;

    @FXML
    private Button btAddConclusion;

    @FXML
    private Button btEditConclusion;

    @FXML
    private Button btDeleteConclusion;

    @FXML
    private Button btOk;

    @FXML
    private Button btCancel;

    private RulePojo rulePojo = new RulePojo();

    private DomainService domainService;

    private VariableService variableService;

    private RuleService ruleService;

    private FactService factService;

    private boolean isEdit;

    private String oldName;

    @FXML
    void initialize(RulePojo rulePojo, DomainService domainService, VariableService variableService, RuleService ruleService, FactService factService) {

        isEdit = rulePojo.getId() != null;

        this.rulePojo.setEsId(rulePojo.getEsId());
        this.rulePojo.setId(rulePojo.getId());
        this.rulePojo.setName(rulePojo.getName());
        this.rulePojo.setOrder(rulePojo.getOrder());
        this.rulePojo.setConclusions(rulePojo.getConclusions());
        this.rulePojo.setConditions(rulePojo.getConditions());

        this.domainService = domainService;
        this.variableService = variableService;
        this.ruleService = ruleService;
        this.factService = factService;

        this.oldName = rulePojo.getName();

        if (!isEdit) {
            tfdName.setText(String.format("Rule%d",  ruleService.getCount(rulePojo.getEsId()) + 1));
        } else {
            tfdName.setText(this.rulePojo.getName());
            fillConditions();
            fillConclusions();
        }

        clmnCondition.setCellValueFactory(new PropertyValueFactory<>("content"));
        clmnConclusion.setCellValueFactory(new PropertyValueFactory<>("content"));

        btCancel.setOnAction(event -> {
            close();
        });

        btOk.setOnAction(event -> {
            addOrUpdate();
        });

        btAddCondition.setOnAction(event -> {
            addCondition();
        });

        btEditCondition.setOnAction(event -> {
            editCondition();
        });

        btDeleteCondition.setOnAction(event -> {
            deleteCondition();
        });

        btAddConclusion.setOnAction(event -> {
            addConclusion();
        });

        btEditConclusion.setOnAction(event -> {
            editConclusion();
        });

        btDeleteConclusion.setOnAction(event -> {
            deleteConclusion();
        });

        btOk.getScene().setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                addOrUpdate();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                close();
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

        btAddCondition.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addCondition();
            }
        });

        btAddConclusion.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addConclusion();
            }
        });

        btEditCondition.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editCondition();
            }
        });

        btEditConclusion.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editConclusion();
            }
        });

        btDeleteCondition.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteCondition();
            }
        });

        btDeleteConclusion.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteConclusion();
            }
        });

        tvConditions.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteCondition();
            }
        });

        tvConclusions.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteConclusion();
            }
        });
    }

    private void deleteConclusion() {
        FactPojo factPojo = tvConclusions.getSelectionModel().getSelectedItem();
        if (factPojo == null) {
            showError("Выберите заключение для удаления!", anch);
        } else {
            this.rulePojo.getConclusions().remove(factPojo);
            fillConclusions();
        }
    }

    private void editConclusion() {
        FactPojo factPojo = tvConclusions.getSelectionModel().getSelectedItem();
        if (factPojo == null) {
            showError("Выберите заключение для изменения!", anch);
            return;
        }
        int index = this.rulePojo.getConclusions().indexOf(factPojo);
        openUpsertFactFrm("Изменение заключения", factPojo, true, false);
        rulePojo.getConclusions().set(index, factPojo);
        fillConclusions();
    }

    private void addConclusion() {
        FactPojo factPojo = new FactPojo(null, null, null, null, null, null);

        openUpsertFactFrm("Добавление заключения", factPojo, false, false);
        if (factPojo.getVariableId() != null) {
            this.rulePojo.getConclusions().add(factPojo);
            fillConclusions();
        }
    }

    private void deleteCondition() {
        FactPojo factPojo = tvConditions.getSelectionModel().getSelectedItem();
        if (factPojo == null) {
            showError("Выберите условие для удаления!", anch);
        } else {
            this.rulePojo.getConditions().remove(factPojo);
            fillConditions();
        }
    }

    private void editCondition() {
        FactPojo factPojo = tvConditions.getSelectionModel().getSelectedItem();
        if (factPojo == null) {
            showError("Выберите условие для изменения!", anch);
            return;
        }
        int index = this.rulePojo.getConditions().indexOf(factPojo);
        openUpsertFactFrm("Изменение условия", factPojo, true, true);
        this.rulePojo.getConditions().set(index, factPojo);
        fillConditions();
    }

    private void addCondition() {
        FactPojo factPojo = new FactPojo(null, null, null, null, null, null);
        openUpsertFactFrm("Добавление условия", factPojo, false, true);
        if (factPojo.getVariableId() != null) {
            this.rulePojo.getConditions().add(factPojo);
            fillConditions();
        }
    }

    private void addOrUpdate() {
        if (validateData()) {
            this.rulePojo.setName(tfdName.getText());
            if (isEdit) {
                ruleService.save(this.rulePojo);
            } else {
                ruleService.add(this.rulePojo);
            }
            close();
        }
    }

    private boolean validateData() {
        String msg = "";

        if (rulePojo.getConditions().size() < 1) {
            msg += "Добавьте хотя бы одно условие!\n";
        }

        if (rulePojo.getConclusions().size() < 1) {
            msg += "Добавьте хотя бы одно заключение!\n";
        }

        if (tfdName.getText().isEmpty()) {
            msg += "Введите название правила!\n";
        } else if (ruleService.checkName(tfdName.getText(), rulePojo.getEsId())) {
            if (!isEdit || !Objects.equals(oldName, tfdName.getText()))
            msg += "Правило с таким наименованием уже существует!\n";
        }

        if (!msg.isEmpty()) {
            showError(msg, anch);
            return false;
        }

        return true;
    }

    private void fillConditions() {
        tvConditions.getItems().clear();

        for (FactPojo condition : rulePojo.getConditions()) {
            tvConditions.getItems().add(condition);
        }
    }

    private void fillConclusions() {
        tvConclusions.getItems().clear();

        for (FactPojo conclusion : rulePojo.getConclusions()) {
            tvConclusions.getItems().add(conclusion);
        }
    }

    private void openUpsertFactFrm(String title, FactPojo factPojo, boolean isEditFact, boolean isCondition) {
        anch.setDisable(true);
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpsertFactFrm.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage upsertFactFrm = new Stage();
        upsertFactFrm.initModality(Modality.APPLICATION_MODAL);
        upsertFactFrm.setAlwaysOnTop(true);
        upsertFactFrm.setResizable(false);
        upsertFactFrm.setTitle(title);
        upsertFactFrm.setScene(new Scene(root, 401, 243));
        loader. <UpsertFactFrmController>getController().initialize(isCondition ? rulePojo.getConditions() : rulePojo.getConclusions(),
                factPojo,  rulePojo.getEsId(), isEditFact, isCondition, domainService, variableService, ruleService, factService);
        upsertFactFrm.setOnCloseRequest(event -> {
            anch.setDisable(false);
        });
        upsertFactFrm.setOnHidden(event -> {
            anch.setDisable(false);
        });
        upsertFactFrm.showAndWait();
    }

    private void close() {
        // close the form
        btOk.getScene().getWindow().hide();
    }
}
