package psu.ru.esshell.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.util.maven.example.Sequences;
import org.jooq.util.maven.example.tables.pojos.Rule;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.RulePojo;

import java.util.List;

import static org.jooq.util.maven.example.tables.Conclusion.CONCLUSION;
import static org.jooq.util.maven.example.tables.Condition.CONDITION;
import static org.jooq.util.maven.example.tables.DomainValue.DOMAIN_VALUE;
import static org.jooq.util.maven.example.tables.Fact.FACT;
import static org.jooq.util.maven.example.tables.Rule.RULE;
import static org.jooq.util.maven.example.tables.Variable.VARIABLE;

@Repository
@Transactional
public class RuleRepository {

    private final DSLContext dslContext;

    private final VariableRepository variableRepository;

    private final DomainRepository domainRepository;

    private final FactRepository factRepository;

    public RuleRepository(DSLContext dslContext, VariableRepository variableRepository, DomainRepository domainRepository, FactRepository factRepository) {
        this.dslContext = dslContext;
        this.variableRepository = variableRepository;
        this.domainRepository = domainRepository;
        this.factRepository = factRepository;
    }

    public RulePojo getById(Long id) {
        return dslContext.selectFrom(RULE)
                .where(RULE.ID.eq(id))
                .orderBy(RULE.ORDER)
                .fetchOneInto(RulePojo.class);
    }

    public void insert(RulePojo rulePojo) {
        rulePojo.setId(dslContext.nextval(Sequences.RULE_ID_SEQ));
        dslContext.insertInto(RULE)
                .columns(RULE.ID, RULE.ES_ID, RULE.NAME, RULE.ORDER)
                .values(rulePojo.getId(), rulePojo.getEsId(), rulePojo.getName(), rulePojo.getOrder() != null ? rulePojo.getOrder() : getCount(rulePojo.getEsId())+1)
        .execute();

        addConditionsAndConclusions(rulePojo);

        updateOrders(rulePojo.getEsId());
    }

    public void updateOrders(Long esId) {
        List<Rule> rules = dslContext.selectFrom(RULE)
                .where(RULE.ES_ID.eq(esId))
                .orderBy(RULE.ORDER, RULE.ID)
                .fetchInto(Rule.class);

        Integer order = 1;
        for (Rule rule : rules) {
            dslContext.update(RULE)
                    .set(RULE.ORDER, order)
                    .where(RULE.ID.eq(rule.getId()))
                    .execute();
            order++;
        }
    }

    public void update(RulePojo rulePojo) {
        dslContext.update(RULE)
                .set(RULE.NAME, rulePojo.getName())
                .set(RULE.ES_ID, rulePojo.getEsId())
                .set(RULE.ORDER, rulePojo.getOrder())
        .where(RULE.ID.eq(rulePojo.getId()))
        .execute();

        dslContext.deleteFrom(CONDITION)
                .where(CONDITION.RULE_ID.eq(rulePojo.getId()))
                .execute();

        dslContext.deleteFrom(CONCLUSION)
                .where(CONCLUSION.RULE_ID.eq(rulePojo.getId()))
                .execute();

        addConditionsAndConclusions(rulePojo);
    }

    private void addConditionsAndConclusions(RulePojo rulePojo) {
        int order = 1;
        for (FactPojo factPojo : rulePojo.getConditions()) {
            Long factId = factRepository.getId(factPojo);

            if (factId == null) {
                factId = factRepository.insert(factPojo);
            }

            dslContext.insertInto(CONDITION)
                    .columns(CONDITION.FACT_ID, CONDITION.RULE_ID, CONDITION.ORDER)
                    .values(factId, rulePojo.getId(), order++)
            .execute();
        }

        order = 1;
        for (FactPojo factPojo : rulePojo.getConclusions()) {
            Long factId = factRepository.getId(factPojo);

            if (factId == null) {
                factId = factRepository.insert(factPojo);
            }

            dslContext.insertInto(CONCLUSION)
                    .columns(CONCLUSION.FACT_ID, CONCLUSION.RULE_ID, CONCLUSION.ORDER)
                    .values(factId,  rulePojo.getId(), order++)
            .execute();
        }
    }

    public void delete(Long id) {
        RulePojo rulePojo = getById(id);
        dslContext.deleteFrom(CONDITION)
                .where(CONDITION.RULE_ID.eq(id))
                .execute();

        dslContext.deleteFrom(CONCLUSION)
                .where(CONCLUSION.RULE_ID.eq(id))
                .execute();

        dslContext.deleteFrom(RULE)
                .where(RULE.ID.eq(id))
        .execute();

        updateOrders(rulePojo.getEsId());
    }

    public Integer getCount(Long esId) {
        return dslContext.selectCount()
                .from(RULE)
                .where(RULE.ES_ID.eq(esId))
                .fetchOneInto(Integer.class);
    }

    public List<RulePojo> getList(Long esId) {
        return dslContext.selectFrom(RULE)
                .where(RULE.ES_ID.eq(esId))
                .orderBy(RULE.ORDER)
                .fetchInto(RulePojo.class);
    }

    public List<FactPojo> getConditionsByRuleId(Long ruleId) {
        return dslContext
                .select(FACT.ID.as("id"),
                        FACT.DOMAIN_VALUE_ID.as("domainValueId"),
                        FACT.VARIABLE_ID.as("variableId"),
                        VARIABLE.NAME.as("variableName"),
                        DOMAIN_VALUE.VALUE.as("variableValue"),
                        DSL.concat(VARIABLE.NAME, DSL.val(" == "), DOMAIN_VALUE.VALUE).as("content"))
                .from(CONDITION)
                .join(FACT).on(FACT.ID.eq(CONDITION.FACT_ID))
                .join(VARIABLE).on(FACT.VARIABLE_ID.eq(VARIABLE.ID))
                .join(DOMAIN_VALUE).on(FACT.DOMAIN_VALUE_ID.eq(DOMAIN_VALUE.ID))
                .where(CONDITION.RULE_ID.eq(ruleId))
                .fetchInto(FactPojo.class);
    }

    public List<FactPojo> getConclusionsByRuleId(Long ruleId) {
        return dslContext
                .select(FACT.ID.as("id"),
                        FACT.DOMAIN_VALUE_ID.as("domainValueId"),
                        FACT.VARIABLE_ID.as("variableId"),
                        VARIABLE.NAME.as("variableName"),
                        DOMAIN_VALUE.VALUE.as("variableValue"),
                        DSL.concat(VARIABLE.NAME, DSL.val(" := "), DOMAIN_VALUE.VALUE).as("content"))
                .from(CONCLUSION)
                .join(FACT).on(FACT.ID.eq(CONCLUSION.FACT_ID))
                .join(VARIABLE).on(FACT.VARIABLE_ID.eq(VARIABLE.ID))
                .join(DOMAIN_VALUE).on(FACT.DOMAIN_VALUE_ID.eq(DOMAIN_VALUE.ID))
                .where(CONCLUSION.RULE_ID.eq(ruleId))
                .fetchInto(FactPojo.class);
    }

    public boolean checkName(String name, Long esId) {
        return dslContext.fetchExists(
                dslContext.selectFrom(RULE)
                        .where(RULE.NAME.eq(name)
                                .and(RULE.ES_ID.eq(esId)))
        );
    }

    public void updateOrdersAfterDragAndDrop(List<RulePojo> rules) {
        for (int i = 0; i < rules.size(); i++) {
            dslContext.update(RULE)
                    .set(RULE.ORDER, i+1)
                    .where(RULE.ID.eq(rules.get(i).getId()))
                    .execute();
        }
    }
}