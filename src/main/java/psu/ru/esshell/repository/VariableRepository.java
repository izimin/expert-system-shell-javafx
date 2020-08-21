package psu.ru.esshell.repository;

import javafx.collections.ObservableList;
import org.jooq.DSLContext;
import org.jooq.util.maven.example.Sequences;
import org.jooq.util.maven.example.enums.KindVariable;
import org.jooq.util.maven.example.tables.pojos.Variable;
import org.jooq.util.maven.example.tables.pojos.Domain;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.DomainPojo;
import psu.ru.esshell.model.pojos.VariablePojo;

import java.util.List;

import static org.jooq.util.maven.example.tables.Domain.DOMAIN;
import static org.jooq.util.maven.example.tables.Variable.VARIABLE;
import static org.jooq.util.maven.example.tables.Fact.FACT;
import static org.jooq.util.maven.example.tables.DomainValue.DOMAIN_VALUE;

@Repository
@Transactional
public class VariableRepository {

    private final DSLContext dslContext;

    public VariableRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public VariablePojo getById(Long id) {
        return dslContext.selectFrom(VARIABLE)
                .where(VARIABLE.ID.eq(id))
                .orderBy(VARIABLE.ORDER)
                .fetchOneInto(VariablePojo.class);
    }

    public void insert(VariablePojo variablePojo) {
        variablePojo.setId(dslContext.nextval(Sequences.VARIABLE_ID_SEQ));
        dslContext.insertInto(VARIABLE)
                .columns(VARIABLE.ID, VARIABLE.NAME, VARIABLE.ES_ID, VARIABLE.ORDER, VARIABLE.DOMAIN_ID, VARIABLE.KIND, VARIABLE.QUESTION)
                .values(variablePojo.getId(), variablePojo.getName(), variablePojo.getEsId(), variablePojo.getOrder() != null ? variablePojo.getOrder() : getCount(variablePojo.getEsId()) + 1, variablePojo.getDomainId(), variablePojo.getKind(), variablePojo.getQuestion())
        .execute();

        updateOrders(variablePojo.getEsId());
    }

    public void update(VariablePojo variablePojo) {
        dslContext.update(VARIABLE)
                .set(VARIABLE.ID, variablePojo.getId())
                .set(VARIABLE.NAME, variablePojo.getName())
                .set(VARIABLE.ES_ID, variablePojo.getEsId())
                .set(VARIABLE.ORDER, variablePojo.getOrder())
                .set(VARIABLE.DOMAIN_ID, variablePojo.getDomainId())
                .set(VARIABLE.KIND, variablePojo.getKind())
                .set(VARIABLE.QUESTION, variablePojo.getQuestion())
                .where(VARIABLE.ID.eq(variablePojo.getId()))
        .execute();
    }

    public void delete(Long id) {
        dslContext.deleteFrom(FACT)
                .where(FACT.VARIABLE_ID.eq(id))
                .execute();

        dslContext.deleteFrom(VARIABLE)
                .where(VARIABLE.ID.eq(id))
                .execute();
    }

    public Integer getCount(Long esId) {
        return dslContext.selectCount()
                .from(VARIABLE)
                .where(VARIABLE.ES_ID.eq(esId))
                .fetchOneInto(Integer.class);
    }

    public List<VariablePojo> getList(Long esId) {
        return dslContext.selectFrom(VARIABLE)
                .where(VARIABLE.ES_ID.eq(esId))
                .orderBy(VARIABLE.ORDER)
                .fetchInto(VariablePojo.class);
    }


    public List<VariablePojo> getOnlyOutputVariables(Long esId) {
        return dslContext.selectFrom(VARIABLE)
                .where(VARIABLE.ES_ID.eq(esId)
                        .and(VARIABLE.KIND.eq(KindVariable.output)))
                .orderBy(VARIABLE.ORDER)
                .fetchInto(VariablePojo.class);
    }

    public void updateOrders(Long esId) {
        List<Variable> variables = dslContext.selectFrom(VARIABLE)
                .where(VARIABLE.ES_ID.eq(esId))
                .orderBy(VARIABLE.ORDER, VARIABLE.ID)
                .fetchInto(Variable.class);

        Integer order = 1;
        for (Variable variable : variables) {
            dslContext.update(VARIABLE)
                    .set(VARIABLE.ORDER, order)
                    .where(VARIABLE.ID.eq(variable.getId()))
                    .execute();
            order++;
        }
    }

    public boolean checkName(String name, Long esId) {
        return dslContext.fetchExists(
                dslContext.selectFrom(VARIABLE)
                        .where(VARIABLE.NAME.eq(name)
                        .and(VARIABLE.ES_ID.eq(esId)))
        );
    }

    public List<DomainPojo> getListDomains(Long esId) {
        return dslContext.selectFrom(DOMAIN)
                .where(DOMAIN.ES_ID.eq(esId)
                        .and(DOMAIN.ID.notIn(
                                dslContext.select(VARIABLE.DOMAIN_ID).from(VARIABLE).where(VARIABLE.ES_ID.eq(esId))
                        )))
                .orderBy(DOMAIN.ID)
                .fetchInto(DomainPojo.class);
    }

    public String getNameVariableById(Long variableId) {
        return dslContext.select(VARIABLE.NAME)
                .where(VARIABLE.ID.eq(variableId))
                .fetchOneInto(String.class);
    }

    public String getDomainValueById(Long domainValueId, Long variableId) {
        return dslContext.select(DOMAIN_VALUE.VALUE)
                .from(DOMAIN)
                .where(DOMAIN.ID.eq(
                        dslContext.select(VARIABLE.DOMAIN_ID)
                                .from(VARIABLE)
                                .where(VARIABLE.ID.eq(variableId))
                        .fetchOneInto(Long.class))
                ).fetchOneInto(String.class);
    }


    public void updateOrdersAfterDragAndDrop(List<VariablePojo> variables) {
        for (int i = 0; i < variables.size(); i++) {
            dslContext.update(VARIABLE)
                    .set(VARIABLE.ORDER, i+1)
                    .where(VARIABLE.ID.eq(variables.get(i).getId()))
                    .execute();
        }
    }

    public VariablePojo getLastInsertedVariable(Long esId) {
        return dslContext.selectFrom(VARIABLE)
                .where(VARIABLE.ORDER.eq(getCount(esId)))
                .fetchOneInto(VariablePojo.class);
    }
}
