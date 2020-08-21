package psu.ru.esshell.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.util.maven.example.tables.Fact;
import org.jooq.util.maven.example.tables.pojos.ReasonTree;
import org.jooq.util.maven.example.tables.pojos.WorkingMemory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.FactPojo;
import psu.ru.esshell.model.pojos.WorkingMemoryPojo;

import java.util.List;

import static org.jooq.util.maven.example.tables.Conclusion.CONCLUSION;
import static org.jooq.util.maven.example.tables.Variable.VARIABLE;
import static org.jooq.util.maven.example.tables.WorkingMemory.WORKING_MEMORY;
import static org.jooq.util.maven.example.tables.ReasonTree.REASON_TREE;
import static org.jooq.util.maven.example.tables.Fact.FACT;
import static org.jooq.util.maven.example.tables.DomainValue.DOMAIN_VALUE;


@Repository
@Transactional
public class ConsultationRepository {

    private final DSLContext dslContext;

    public ConsultationRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void clear() {
        dslContext.deleteFrom(REASON_TREE).execute();
        dslContext.deleteFrom(WORKING_MEMORY).execute();
    }


    public boolean variableExistsInMemory(Long id) {
        return dslContext.fetchExists(
                dslContext.selectFrom(WORKING_MEMORY
                        .join(FACT).on(WORKING_MEMORY.FACT_ID.eq(FACT.ID)))
                        .where(FACT.VARIABLE_ID.eq(id))
        );
    }

    public void insert(WorkingMemory item) {
        dslContext.insertInto(WORKING_MEMORY)
                .columns(WORKING_MEMORY.FACT_ID, WORKING_MEMORY.RULE_ID)
                .values(item.getFactId(), item.getRuleId())
                .execute();
    }

    public Long getDomainValue(Long id) {
        return dslContext.select(DOMAIN_VALUE.ID).from(WORKING_MEMORY)
                        .join(FACT).on(WORKING_MEMORY.FACT_ID.eq(FACT.ID))
                        .join(DOMAIN_VALUE).on(FACT.DOMAIN_VALUE_ID.eq(DOMAIN_VALUE.ID))
                        .where(FACT.VARIABLE_ID.eq(id))
                        .fetchOneInto(Long.class);
    }

    public void insertIntoReasonTree(Long ruleId, Long parentRuleId) {
        dslContext.insertInto(REASON_TREE)
                .columns(REASON_TREE.RULE_ID, REASON_TREE.PARENT_RULE_ID)
                .values(ruleId, parentRuleId)
                .execute();
    }

    public List<WorkingMemoryPojo> getFacts() {
        return dslContext
                .select(WORKING_MEMORY.FACT_ID.as("factId"),
                        WORKING_MEMORY.RULE_ID.as("ruleId"),
                        DSL.concat(VARIABLE.NAME, DSL.val(" := "), DOMAIN_VALUE.VALUE).as("content"))
                .from(WORKING_MEMORY)
                .join(FACT).on(FACT.ID.eq(WORKING_MEMORY.FACT_ID))
                .join(VARIABLE).on(FACT.VARIABLE_ID.eq(VARIABLE.ID))
                .join(DOMAIN_VALUE).on(FACT.DOMAIN_VALUE_ID.eq(DOMAIN_VALUE.ID))
                .fetchInto(WorkingMemoryPojo.class);
    }

    public List<ReasonTree> getTree() {
        return dslContext.selectFrom(REASON_TREE)
                .fetchInto(ReasonTree.class);
    }


    public void removeWithRuleId(Long ruleId) {
        dslContext.deleteFrom(WORKING_MEMORY)
                .where(WORKING_MEMORY.RULE_ID.eq(ruleId))
                .execute();
    }

    public void updateWithRuleId(Long id, Long ruleId) {
        dslContext.update(REASON_TREE)
                .set(REASON_TREE.PARENT_RULE_ID, ruleId)
                .where(REASON_TREE.RULE_ID.eq(id))
                .execute();
    }

    public void updateOnlyWorkingMemory(Long id) {
        dslContext.update(WORKING_MEMORY)
                .set(WORKING_MEMORY.RULE_ID, id)
                .where(WORKING_MEMORY.RULE_ID.eq(0L))
                .execute();
    }
}
