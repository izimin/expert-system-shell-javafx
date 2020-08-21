package psu.ru.esshell.service;

import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;
import psu.ru.esshell.repository.DomainRepository;
import psu.ru.esshell.repository.RuleRepository;

import java.util.List;

@Service
@Transactional
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public RulePojo getById(Long id) {
        RulePojo rulePojo = ruleRepository.getById(id);
        rulePojo.setConditions(ruleRepository.getConditionsByRuleId(rulePojo.getId()));
        rulePojo.setConclusions(ruleRepository.getConclusionsByRuleId(rulePojo.getId()));
        rulePojo.setContent(rulePojo.getContent());

        return rulePojo;
    }

    public void add(RulePojo rulePojo) {
        ruleRepository.insert(rulePojo);
    }

    public void save(RulePojo rulePojo) {
        ruleRepository.update(rulePojo);
    }

    public void remove(Long id) {
        ruleRepository.delete(id);
    }

    public Integer getCount(Long esId) {
        return ruleRepository.getCount(esId);
    }

    public List<RulePojo> getList(Long esId) {
        List<RulePojo> rulePojos = ruleRepository.getList(esId);

        for (RulePojo rulePojo : rulePojos) {
            rulePojo.setConditions(ruleRepository.getConditionsByRuleId(rulePojo.getId()));
            rulePojo.setConclusions(ruleRepository.getConclusionsByRuleId(rulePojo.getId()));
            rulePojo.setContent(rulePojo.getContent());
        }

        return rulePojos;
    }

    public boolean checkName(String name, Long esId) {
        return ruleRepository.checkName(name, esId);
    }

    public void updateOrdersAfterDragAndDrop(List<RulePojo> rules) {
        ruleRepository.updateOrdersAfterDragAndDrop(rules);
    }

}
