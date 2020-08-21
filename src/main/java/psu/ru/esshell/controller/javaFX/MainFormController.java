package psu.ru.esshell.controller.javaFX;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import org.jooq.util.maven.example.enums.KindVariable;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.jooq.util.maven.example.tables.pojos.Es;
import org.jooq.util.maven.example.tables.pojos.Fact;
import org.jooq.util.maven.example.tables.pojos.WorkingMemory;
import org.springframework.stereotype.Controller;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;
import psu.ru.esshell.model.pojos.VariablePojo;
import psu.ru.esshell.service.*;

import static psu.ru.esshell.service.FormShowService.showError;

@Controller
public class MainFormController {

    public class StringConverterGoal extends StringConverter<VariablePojo> {

        @Override
        public String toString(VariablePojo variablePojo) {
            return variablePojo == null ? null : variablePojo.getName();
        }

        @Override
        public VariablePojo fromString(String string) {
            return null;
        }

    }

    public class StringConverterAnswer extends StringConverter<DomainValue> {

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
    private Button btRules;

    @FXML
    private Button btDomains;

    @FXML
    private Button btVariables;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabRules;

    @FXML
    private Label lblNameEs;

    @FXML
    private TableView<RulePojo> tvRules;

    @FXML
    private TableColumn<RulePojo, String> clmnNumberRule;

    @FXML
    private TableColumn<RulePojo, String> clmnNameRule;

    @FXML
    private TableColumn<RulePojo, String> clmnContentRule;

    @FXML
    private Button btAddRule;

    @FXML
    private Button btEditRule;

    @FXML
    private Button btDeleteRule;

    @FXML
    private TableView<FactPojo> tvConditions;

    @FXML
    private TableColumn<FactPojo, String> clmnConditionRule;

    @FXML
    private TableView<FactPojo> tvConclusions;

    @FXML
    private TableColumn<FactPojo, String> clmnConclusionRule;

    @FXML
    private Tab tabDomains;

    @FXML
    private TableView<DomainPojo> tvDomains;

    @FXML
    private TableColumn<DomainPojo, Integer> clmnNumberDomain;

    @FXML
    private TableColumn<DomainPojo, String> clmnNameDomain;

    @FXML
    private Button btAddDomain;

    @FXML
    private Button btEditDomain;

    @FXML
    private Button btDeleteDomain;

    @FXML
    private TableView<DomainValue> tvDomainValues;

    @FXML
    private TableColumn<DomainValue, String> clmnDomainValues;

    @FXML
    private Tab tabVariables;

    @FXML
    private TableView<VariablePojo> tvValiables;

    @FXML
    private TableColumn<VariablePojo, Integer> clmnNumberVariable;

    @FXML
    private TableColumn<VariablePojo, String> clmnNameVariable;

    @FXML
    private TableColumn<VariablePojo, String> clmnKindVariable;

    @FXML
    private TableColumn<VariablePojo, String> clmnDomainVariable;

    @FXML
    private Button btAddVariable;

    @FXML
    private Button btEditVariable;

    @FXML
    private Button btDeleteVariable;

    @FXML
    private TextField tfdQuestion;

    @FXML
    private TableView<DomainValue> tvDomainValuesVariable;

    @FXML
    private TableColumn<DomainValue, String> clmnDomainValuesVariable;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button btConsultation;

    @FXML
    private Button btStartConsultation;

    @FXML
    private Button btShowReason;

    @FXML
    private ScrollPane scrollPaneConsult;

    @FXML
    private VBox vboxQuestion;

    @FXML
    private VBox vboxAnswer;

    @FXML
    private Button btSend;

    @FXML
    private ComboBox<VariablePojo> cbGoal;

    @FXML
    private ComboBox<DomainValue> cbAnswer;

    @FXML
    private MenuItem btCreateEs;

    @FXML
    private MenuItem btOpenEs;

    @FXML
    private MenuItem btSaveEs;

    @FXML
    private MenuItem btExit;


    @FXML
    private HBox hBox;

    private Es es = new Es(1L, "Проггер", null);

    private final DomainService domainService;
    private final VariableService variableService;
    private final RuleService ruleService;
    private final FactService factService;
    private final ConsultationService consultationService;

    List<RulePojo> rulePojos;
    VariablePojo goal;

    boolean isFirst = true;

    public MainFormController(DomainService domainService, VariableService variableService, RuleService ruleService, FactService factService, ConsultationService consultationService) {
        this.domainService = domainService;
        this.variableService = variableService;
        this.ruleService = ruleService;
        this.factService = factService;
        this.consultationService = consultationService;
    }

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            lblNameEs.setText("ЭС: " + es.getName().toUpperCase());
        });

        domainService.getCountDomains(es.getId());
        columnWidthCalibration();
        fillRuleList();

        /* Таблицы */
        tvDomains.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillValuesDomain(newValue);
        });

        tvValiables.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillValuesDomainVariable(newValue);
        });

        tvRules.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillConditionsAndConclusions(newValue);
        });

        /* Кнопки */
        btRules.setStyle("-fx-background-color: black;");

        btRules.setOnAction(event -> {
            tabPane.getSelectionModel().select(0);

            btDomains.setStyle("-fx-background-color: #131022;");
            btVariables.setStyle("-fx-background-color: #131022;");
            btRules.setStyle("-fx-background-color: black;");
            btConsultation.setStyle("-fx-background-color: #131022;");

            fillRuleList();
        });

        btDomains.setOnAction(event -> {
            tabPane.getSelectionModel().select(1);
            btDomains.setStyle("-fx-background-color: black;");
            btVariables.setStyle("-fx-background-color: #131022;");
            btRules.setStyle("-fx-background-color: #131022;");
            btConsultation.setStyle("-fx-background-color: #131022;");

            fillDomainList();
        });

        btVariables.setOnAction(event -> {
            tabPane.getSelectionModel().select(2);
            btDomains.setStyle("-fx-background-color: #131022;");
            btVariables.setStyle("-fx-background-color: black;");
            btRules.setStyle("-fx-background-color: #131022;");
            btConsultation.setStyle("-fx-background-color: #131022;");

            fillVariableList();
        });

        btConsultation.setOnAction(event -> {
            tabPane.getSelectionModel().select(3);
            btDomains.setStyle("-fx-background-color: #131022;");
            btVariables.setStyle("-fx-background-color: #131022;");
            btRules.setStyle("-fx-background-color: #131022;");
            btConsultation.setStyle("-fx-background-color: black;");
            if (isFirst) {
                Platform.runLater(() -> {
                    ChatItem chatItem = new ChatItem("Здравствуйте! Выберите цель и нажмите \"Начать консультацию\".", false);
                    vboxAnswer.getChildren().add(chatItem.getLblTransparent());
                    vboxQuestion.getChildren().add(chatItem.getLbl());
                });
                isFirst = false;
            }
            vboxCalibration();
            fillCbGoal();
        });

        btAddRule.setOnAction(event -> {
            addRule();
        });

        btEditRule.setOnAction(event -> {
            editRule();
        });

        btDeleteRule.setOnAction(event -> {
            deleteRule();
        });

        btAddDomain.setOnAction(event -> {
            addDomain();
        });

        btEditDomain.setOnAction(event -> {
            editDomain();
        });

        btDeleteDomain.setOnAction(event -> {
            deleteDomain();
        });

        btAddVariable.setOnAction(event -> {
            addVariable();
        });

        btEditVariable.setOnAction(event -> {
            editVariable();
        });

        btDeleteVariable.setOnAction(event -> {
            deleteVariable();
        });

        settingDragAndDrop();

        tvRules.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteRule();
            }
        });

        tvDomains.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteDomain();
            }
        });

        tvValiables.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteVariable();
            }
        });

        btAddRule.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addRule();
            }
        });

        btAddVariable.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addVariable();
            }
        });

        btAddDomain.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                addDomain();
            }
        });

        btEditRule.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editRule();
            }
        });

        btEditVariable.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editVariable();
            }
        });

        btEditDomain.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                editDomain();
            }
        });

        btDeleteRule.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteRule();
            }
        });

        btDeleteVariable.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteVariable();
            }
        });

        btDeleteDomain.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !event.isControlDown()) {
                deleteDomain();
            }
        });

        btStartConsultation.setOnAction(event -> {
            startConsultation();
        });

        btShowReason.setOnAction(event -> {
            openDialogReasonFrm();
        });

        btSend.setOnAction(event -> {
            sendAnswer();
        });

        cbAnswer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btSend.setDisable(newValue == null);
        });

        hBox.heightProperty().addListener(observable -> scrollPaneConsult.setVvalue(1D));
    }

    private void sendAnswer() {
        synchronized (lock) {
            Platform.runLater(() -> {
                DomainValue domainValue = cbAnswer.getSelectionModel().getSelectedItem();

                if (domainValue == null) {
                    showError("Выберите ответ из предложенного списка!", anch);
                    return;
                }

                ChatItem chatItem = new ChatItem(domainValue.getValue(), true);
                vboxAnswer.getChildren().add(chatItem.getLbl());
                vboxQuestion.getChildren().add(chatItem.getLblTransparent());
            });
            lock.notifyAll();
        }
    }

    boolean f = true;

    @Getter
    class ChatItem {
        Label lbl;
        Label lblTransparent;

        ChatItem(String text, boolean isAnswer) {
            this.lbl = new Label(text);
            this.lbl.setStyle("-fx-background-color: " + (isAnswer ? "#222222; " : "#36314D; ") +
                    "-fx-background-radius: 10 10 10 10;" +
                    "-fx-padding: 10;");
            this.lbl.setWrapText(true);

            this.lblTransparent = new Label(this.lbl.getText());
            this.lblTransparent.setStyle(lbl.getStyle() + "-fx-opacity: 0;");
            this.lblTransparent.setWrapText(true);
        }
    }

    private void askQuestion(VariablePojo variablePojo) {
        Platform.runLater(() -> {
            ChatItem chatItem = new ChatItem(variablePojo.getQuestion(), false);
            vboxAnswer.getChildren().add(chatItem.getLblTransparent());
            vboxQuestion.getChildren().add(chatItem.getLbl());

            cbAnswer.getItems().clear();
            cbAnswer.getItems().addAll(domainService.getDomainValues(variablePojo.getDomainId()));
        });
    }

    Long prevId = 0L;
    Map<Long, Long> map = new HashMap<>();
    private DomainValue mlv(VariablePojo variable, Long parentId) {
        synchronized (lock) {
            if (consultationService.variableExistsInMemory(variable.getId())) {

                return domainService.getDomainValueById(consultationService.getDomainValueId(variable.getId()));
            }
            for (RulePojo rulePojo : rulePojos) {
                if (rulePojo.getConclusions().stream().map(Fact::getVariableId)
                        .anyMatch(y -> y.equals(variable.getId()))) {
                    boolean done = true;
                    for (FactPojo factPojo : rulePojo.getConditions()) {
                        VariablePojo variablePojo = variableService.getVariableById(factPojo.getVariableId());
                        if (!consultationService.variableExistsInMemory(variablePojo.getId()) && variablePojo.getKind().equals(KindVariable.requested)) {
                            askQuestion(variablePojo);
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            FactPojo newFact = new FactPojo(variablePojo.getId(), cbAnswer.getSelectionModel().getSelectedItem().getId());
                            Long id = null;
                            if (!factService.checkExists(newFact)) {
                                done = false;
                                break;
                            } else {
                                id = factService.getId(newFact);
                            }

                            consultationService.insert(new WorkingMemory(0L, id));

                            Platform.runLater(() -> {
                                cbAnswer.getItems().clear();
                            });

                            if (!Objects.equals(newFact.getDomainValueId(), factPojo.getDomainValueId())) {
                                done = false;
                                break;
                            }
                        } else {
                            DomainValue domainValue = mlv(variablePojo, rulePojo.getId());

                            if (!Objects.equals(domainValue.getId(), factPojo.getDomainValueId())) {
                                done = false;
                                break;
                            } else {
                                consultationService.updateWithRuleId(map.get(domainValue.getId()), rulePojo.getId());
                            }
                        }
                    }
                    if (done) {
                        consultationService.updateOnlyWorkingMemory(rulePojo.getId());
                        consultationService.insertIntoReasonTree(rulePojo.getId(), parentId);
                        Long domainId = rulePojo.getConclusions().stream()
                                .filter(x -> Objects.equals(x.getVariableId(), variable.getId()))
                                .findAny().get().getDomainValueId();
                        consultationService.insert(new WorkingMemory(rulePojo.getId(), factService.getId(new FactPojo(variable.getId(), domainId))));
                        map.put(domainId, rulePojo.getId());
                        return domainService.getDomainValueById(domainId);
                    }
                }
            }

            return null;
        }
    }
    private static final class Lock { }
    private final Object lock = new Lock();

    private void startConsultation() {

        consultationService.clear();
        vboxQuestion.getChildren().clear();
        vboxAnswer.getChildren().clear();
        btShowReason.setDisable(true);

        VariablePojo variablePojo = cbGoal.getSelectionModel().getSelectedItem();

        if (variablePojo == null) {
            showError("Выберите цель консультации!", anch);
            return;
        }

        rulePojos = ruleService.getList(es.getId());
        goal = cbGoal.getSelectionModel().getSelectedItem();

        new Thread(() -> {
            DomainValue res = mlv(goal, 0L);

            Platform.runLater(() -> {
                ChatItem chatItem = new ChatItem(res == null ? "Не удалось определить результат :("
                        : goal.getName() + " = " + res.getValue(), false);
                vboxAnswer.getChildren().add(chatItem.getLblTransparent());
                vboxQuestion.getChildren().add(chatItem.getLbl());
                chatItem = new ChatItem("Консультация завершена", false);
                vboxAnswer.getChildren().add(chatItem.getLblTransparent());
                vboxQuestion.getChildren().add(chatItem.getLbl());
                btShowReason.setDisable(false);
            });
        }).start();
    }

    private void fillCbGoal() {
        cbGoal.getItems().clear();
        cbGoal.getItems().addAll(variableService.getOnlyOutputVariables(es.getId()));

        if (goal != null) {
            cbGoal.getSelectionModel().select(goal);
        }
    }

    private void vboxCalibration() {

        vboxQuestion.prefWidthProperty().bind(scrollPaneConsult.widthProperty().multiply(0.5).subtract(25));
        vboxAnswer.prefWidthProperty().bind(scrollPaneConsult.widthProperty().multiply(0.5).subtract(25));

        cbGoal.setConverter(new StringConverterGoal());
        cbGoal.setCellFactory(p -> new ListCell<VariablePojo>() {
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

        cbAnswer.setConverter(new StringConverterAnswer());
        cbAnswer.setCellFactory(p -> new ListCell<DomainValue>() {
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
    }

    private void settingDragAndDrop() {
        tvRules.setRowFactory(tv -> {
            TableRow<RulePojo> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    RulePojo draggedRule = tvRules.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = tvRules.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tvRules.getItems().add(dropIndex, draggedRule);

                    ruleService.updateOrdersAfterDragAndDrop(tvRules.getItems());
                    int i = 1;
                    for (RulePojo rule : tvRules.getItems()) {
                        rule.setOrder(i++);
                    }
                    event.setDropCompleted(true);
                    tvRules.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });

            return row ;
        });

        tvDomains.setRowFactory(tv -> {
            TableRow<DomainPojo> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    DomainPojo draggedRule = tvDomains.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = tvDomains.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tvDomains.getItems().add(dropIndex, draggedRule);

                    domainService.updateOrdersAfterDragAndDrop(tvDomains.getItems());
                    int i = 1;
                    for (DomainPojo rule : tvDomains.getItems()) {
                        rule.setOrder(i++);
                    }
                    event.setDropCompleted(true);
                    tvDomains.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });

            return row ;
        });

        tvValiables.setRowFactory(tv -> {
            TableRow<VariablePojo> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    VariablePojo draggedRule = tvValiables.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = tvValiables.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tvValiables.getItems().add(dropIndex, draggedRule);

                    variableService.updateOrdersAfterDragAndDrop(tvValiables.getItems());
                    int i = 1;
                    for (VariablePojo rule : tvValiables.getItems()) {
                        rule.setOrder(i++);
                    }
                    event.setDropCompleted(true);
                    tvDomains.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });

            return row ;
        });
    }

    private void deleteVariable() {
        VariablePojo variablePojo = tvValiables.getSelectionModel().getSelectedItem();
        if (variablePojo == null) {
            showError("Выберите переменную для удаления!", anch);
            return;
        }

        variableService.remove(variablePojo.getId());
        variableService.updateOrders(es.getId());

        tvValiables.getItems().remove(variablePojo);
        for (int i = 0; i < tvValiables.getItems().size(); i++) {
            tvValiables.getItems().get(i).setOrder(i+1);
        }
    }

    private void editVariable() {
        VariablePojo variablePojo = tvValiables.getSelectionModel().getSelectedItem();
        if (variablePojo == null) {
            showError("Выберите переменную для изменения!", anch);
            return;
        }
        openDialogUpsertVariable("Изменение переменной", variablePojo);
        fillDomainList();
    }

    private void addVariable() {
        openDialogUpsertVariable("Добавление переменной", new VariablePojo(es.getId()));
    }

    private void deleteDomain() {
        DomainPojo domainPojo = tvDomains.getSelectionModel().getSelectedItem();
        if (domainPojo == null) {
            showError("Выберите домен для удаления!", anch);
            return;
        }

        if (validateDeleteDomain(domainPojo)) {
            domainService.removeDomain(domainPojo.getId());
            domainService.updateOrders(es.getId());
            tvDomains.getItems().remove(domainPojo);
            for (int i = 0; i < tvDomains.getItems().size(); i++) {
                tvDomains.getItems().get(i).setOrder(i+1);
            }
        }
    }

    private void editDomain() {
        DomainPojo domainPojo = tvDomains.getSelectionModel().getSelectedItem();
        if (domainPojo == null) {
            showError("Выберите домен для изменения!", anch);
            return;
        }
        openDialogUpsertDomain("Изменение домена", domainPojo);
        fillDomainList();
    }

    private void addDomain() {
        openDialogUpsertDomain("Добавление домена", new DomainPojo(es.getId()));
    }

    private void deleteRule() {
        RulePojo rulePojo = tvRules.getSelectionModel().getSelectedItem();
        if (rulePojo == null) {
            showError("Выберите правило для удаления!", anch);
            return;
        }

        ruleService.remove(rulePojo.getId());

        tvRules.getItems().remove(rulePojo);
        for (int i = 0; i < tvRules.getItems().size(); i++) {
            tvRules.getItems().get(i).setOrder(i+1);
        }
    }

    private void editRule() {
        RulePojo rulePojo = tvRules.getSelectionModel().getSelectedItem();
        if (rulePojo == null) {
            showError("Выберите правило для изменения!", anch);
            return;
        }
        openDialogUpsertRule("Изменение правила", rulePojo);
        fillDomainList();
    }

    private void addRule() {
        openDialogUpsertRule("Добавление правила", new RulePojo(es.getId()));
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

        int index = tvDomains.getSelectionModel().getSelectedIndex();
        if (domainPojo.getId() == null) {
            domainPojo.setOrder(index + 1);
        }

        loader. <UpsertDomainFormController>getController().initialize(domainPojo, domainService);
        upsertDomainFrm.setOnHidden(event -> {
            //fillDomainList();
            fillDomainList();
            if (!tvDomains.getItems().isEmpty()) {
                tvDomains.getSelectionModel().select(domainPojo.getId() != null ? index : index+1);
            }
            anch.setDisable(false);
        });
        upsertDomainFrm.setOnCloseRequest(event -> {
            fillDomainList();
            if (!tvDomains.getItems().isEmpty()) {
                tvDomains.getSelectionModel().select(domainPojo.getId() != null ? index : index+1);
            }
            anch.setDisable(false);
        });
        upsertDomainFrm.showAndWait();
    }

    private void openDialogUpsertVariable(String title, VariablePojo variablePojo) {
        anch.setDisable(true);

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

        int index = tvValiables.getSelectionModel().getSelectedIndex();
        if (variablePojo.getId() == null) {
            variablePojo.setOrder(index + 1);
        }

        loader. <UpsertVariableFormController>getController().initialize(variablePojo, domainService, variableService);

        upsertVariableFrm.setOnHidden(event -> {
            fillVariableList();
            if (!tvValiables.getItems().isEmpty()) {
                tvValiables.getSelectionModel().select(variablePojo.getId() != null ? index : index+1);
            }
            anch.setDisable(false);
        });
        upsertVariableFrm.setOnCloseRequest(event -> {
            fillVariableList();
            if (!tvValiables.getItems().isEmpty()) {
                tvValiables.getSelectionModel().select(variablePojo.getId() != null ? index : index+1);
            }
            anch.setDisable(false);
        });
        upsertVariableFrm.showAndWait();
    }

    private void openDialogUpsertRule(String title, RulePojo rulePojo) {
        anch.setDisable(true);

        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpsertRuleFrm.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage upsertRuleFrm = new Stage();
        upsertRuleFrm.initModality(Modality.APPLICATION_MODAL);
        upsertRuleFrm.setAlwaysOnTop(true);
        upsertRuleFrm.setResizable(false);
        upsertRuleFrm.setTitle(title);
        upsertRuleFrm.setScene(new Scene(root, 401, 733));

        int index = tvRules.getSelectionModel().getSelectedIndex();
        if (rulePojo.getId() == null) {
            rulePojo.setOrder(index + 1);
        }

        loader. <UpsertRuleFrmController>getController().initialize(rulePojo, domainService, variableService, ruleService, factService);
        upsertRuleFrm.setOnHidden(event -> {
            fillRuleList();
            anch.setDisable(false);
            if (!tvRules.getItems().isEmpty()) {
                tvRules.getSelectionModel().select(rulePojo.getId() != null ? index : index+1);
            }
        });
        upsertRuleFrm.setOnCloseRequest(event -> {
            fillRuleList();
            if (!tvRules.getItems().isEmpty()) {
                tvRules.getSelectionModel().select(rulePojo.getId() != null ? index : index+1);
            }
            anch.setDisable(false);
        });
        upsertRuleFrm.showAndWait();
    }

    private void openDialogReasonFrm() {
        anch.setDisable(true);

        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReasonFrm.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage upsertDomainFrm = new Stage();
        upsertDomainFrm.initModality(Modality.APPLICATION_MODAL);
        upsertDomainFrm.setAlwaysOnTop(true);
        upsertDomainFrm.setResizable(false);
        upsertDomainFrm.setTitle("Объяснение");
        upsertDomainFrm.setScene(new Scene(root, 1222, 591));
        loader. <ReasonFrmController>getController().initialize(ruleService, factService, variableService, domainService, consultationService);
        upsertDomainFrm.setOnHidden(event -> {
            fillDomainList();
            anch.setDisable(false);
        });
        upsertDomainFrm.setOnCloseRequest(event -> {
            fillDomainList();
            anch.setDisable(false);
        });
        upsertDomainFrm.showAndWait();
    }

    private void columnWidthCalibration() {
        int widthClmnNumber = 40;
        int widthClmnName = 160;
        int widthClmnKindVariable = 200;
        int sumWidthClmnNameAndNumber = widthClmnNumber + widthClmnName;

        // Домены
        clmnNumberDomain.setPrefWidth(widthClmnNumber);
        clmnNameDomain.prefWidthProperty().bind(tvDomains.widthProperty().subtract(widthClmnNumber));

        clmnDomainValues.setCellValueFactory(new PropertyValueFactory<>("value"));
        clmnNumberDomain.setCellValueFactory(new PropertyValueFactory<>("order"));
        clmnNameDomain.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Переменные
        clmnNumberVariable.setPrefWidth(widthClmnNumber);
        clmnNameVariable.setPrefWidth(widthClmnName);
        clmnKindVariable.setPrefWidth(widthClmnKindVariable);
        clmnDomainVariable.prefWidthProperty().bind(tvValiables.widthProperty().subtract(sumWidthClmnNameAndNumber + widthClmnKindVariable));

        clmnNumberVariable.setCellValueFactory(new PropertyValueFactory<>("order"));
        clmnNameVariable.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnKindVariable.setCellValueFactory(new PropertyValueFactory<>("kindName"));
        clmnDomainVariable.setCellValueFactory(new PropertyValueFactory<>("domainName"));
        clmnDomainValuesVariable.setCellValueFactory(new PropertyValueFactory<>("value"));

//        clmnDomainVariable.setCellFactory(param -> {
//            TableCell<VariablePojo, String> cell = new TableCell<>();
//            Text text = new Text();
//            cell.setGraphic(text);
//            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//            cell.setWrapText(true);
//            text.setFill(Color.WHITE);
//            text.textProperty().bind(cell.itemProperty());
//            text.wrappingWidthProperty().bind(clmnDomainVariable.widthProperty());
//            return cell;
//        });
        clmnNumberRule.setPrefWidth(widthClmnNumber);
        clmnNameRule.setPrefWidth(widthClmnName);
        clmnContentRule.prefWidthProperty().bind(tvRules.widthProperty().subtract(sumWidthClmnNameAndNumber));

        clmnNumberRule.setCellValueFactory(new PropertyValueFactory<>("order"));
        clmnNameRule.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmnContentRule.setCellValueFactory(new PropertyValueFactory<>("content"));

        clmnConditionRule.setCellValueFactory(new PropertyValueFactory<>("content"));
        clmnConclusionRule.setCellValueFactory(new PropertyValueFactory<>("content"));
    }

    private void fillRuleList() {
        tvRules.getItems().clear();

        List<RulePojo> rulePojos = ruleService.getList(es.getId());

        for (RulePojo rulePojo : rulePojos) {
            tvRules.getItems().add(rulePojo);
        }
    }

    private void fillConditionsAndConclusions(RulePojo rulePojo) {
        tvConditions.getItems().clear();
        tvConclusions.getItems().clear();

        if (rulePojo == null) {
            return;
        }

        for (FactPojo condition : rulePojo.getConditions()) {
            tvConditions.getItems().add(condition);
        }

        for (FactPojo conclusion : rulePojo.getConclusions()) {
            tvConclusions.getItems().add(conclusion);
        }
    }

    private void fillValuesDomain(DomainPojo domainPojo) {
        tvDomainValues.getItems().clear();

        if (domainPojo == null) {
            return;
        }

        for (DomainValue domainValue : domainService.getDomainValues(domainPojo.getId())) {
            tvDomainValues.getItems().add(domainValue);
        }
    }

    private void fillValuesDomainVariable(VariablePojo variablePojo) {
        tvDomainValuesVariable.getItems().clear();
        tfdQuestion.setText("");

        if (variablePojo == null) {
            return;
        }

        tfdQuestion.setText(variablePojo.getQuestion() != null ? variablePojo.getQuestion() : "");

        for (DomainValue domainValue :  domainService.getDomainValues(variablePojo.getDomainId())) {
            tvDomainValuesVariable.getItems().add(domainValue);
        }
    }

    private void fillDomainList() {
        tvDomains.getItems().clear();
        tvDomainValues.getItems().clear();

        List<DomainPojo> domainPojos = domainService.getList(es.getId());

        for (DomainPojo domainPojo : domainPojos) {
            tvDomains.getItems().add(domainPojo);
        }
    }

    private void fillVariableList() {
        tvValiables.getItems().clear();
        tvDomainValuesVariable.getItems().clear();

        List<VariablePojo> variablePojos = variableService.getList(es.getId());

        for (VariablePojo variablePojo : variablePojos) {
            tvValiables.getItems().add(variablePojo);
        }
    }

    private boolean validateDeleteDomain(DomainPojo domainPojo) {
        String msg = "";
        String nameVariable = this.domainService.getNameVariableByDomainId(domainPojo.getId());
        if (domainPojo.getId() != null && nameVariable != null) {
            msg = String.format("Данный домен используется переменной \"%s\".\n", nameVariable);
        }

        if (msg.isEmpty()) {
            return true;
        } else {
            showError(msg, anch);
            return false;
        }
    }
}