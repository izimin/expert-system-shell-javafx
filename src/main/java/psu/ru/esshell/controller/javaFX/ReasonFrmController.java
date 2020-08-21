package psu.ru.esshell.controller.javaFX;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jooq.util.maven.example.tables.WorkingMemory;
import org.jooq.util.maven.example.tables.pojos.ReasonTree;
import org.springframework.stereotype.Controller;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;
import psu.ru.esshell.model.pojos.WorkingMemoryPojo;
import psu.ru.esshell.repository.ConsultationRepository;
import psu.ru.esshell.service.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class ReasonFrmController {

    @FXML
    private TreeView<RulePojo> tvRules;

    @FXML
    private TableView<WorkingMemoryPojo> tvFacts;


    @FXML
    private TableColumn<WorkingMemoryPojo, String> clmnFacts;


    @FXML
    private Button btShowTv;

    @FXML
    private Button btClose;

    private ConsultationService consultationService;

    private RuleService ruleService;

    private boolean isExpanded = false;

    void initialize(RuleService ruleService, FactService factService, VariableService variableService, DomainService domainService, ConsultationService consultationService) {
        clmnFacts.setCellValueFactory(new PropertyValueFactory<>("content"));
        tvFacts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.consultationService = consultationService;
        this.ruleService = ruleService;

        tvRules.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectFacts(newValue.getValue().getId());
            }
        });

        btClose.setOnAction(event -> close());

        btShowTv.setOnAction(event -> {
            Platform.runLater(() -> {
                btShowTv.setText(isExpanded ? "Сврернуть дерево" : "Развернуть дерево");
            });
            isExpanded = !isExpanded;
            showTree(tvRules.getRoot(), isExpanded);
        });

        fillTvReason();
        fillFacts();
    }

    private void showTree(TreeItem treeItem, boolean isShow) {
        treeItem.setExpanded(isShow);
        for(Object ti : treeItem.getChildren()) {
            showTree((TreeItem) ti, isShow);
        }
    }

    private void selectFacts(Long id) {
        tvFacts.getSelectionModel().clearSelection();
        for (WorkingMemoryPojo wmp : tvFacts.getItems()) {
            if (Objects.equals(wmp.getRuleId(), id)) {
                tvFacts.getSelectionModel().select(wmp);
            }
        }
    }

    private void tree(TreeItem<RulePojo> item, Long id, List<ReasonTree> reasonTrees) {
        List<Long> ids = reasonTrees.stream().filter(x -> Objects.equals(x.getParentRuleId(), id)).map(ReasonTree::getRuleId).distinct().collect(Collectors.toList());
        for (Long i : ids) {
            TreeItem ti = new TreeItem<>(ruleService.getById(i));
            tree(ti, i, reasonTrees);
            item.getChildren().add(ti);
        }
    }

    private void fillTvReason() {

        List<ReasonTree> reasonTrees = consultationService.getTree();
        RulePojo rulePojo = ruleService.getById(reasonTrees.stream().filter(x -> x.getParentRuleId() == 0).findAny().get().getRuleId());
        TreeItem<RulePojo> treeItem = new TreeItem<>(rulePojo);

        tree(treeItem, rulePojo.getId(), reasonTrees);
        tvRules.setRoot(treeItem);
    }

    private void fillFacts() {
        tvFacts.getItems().clear();

        for (WorkingMemoryPojo fact : consultationService.getFacts()) {
            tvFacts.getItems().add(fact);
        }
    }

    private void close() {
        // close the form
        btClose.getScene().getWindow().hide();
    }
}