package psu.ru.esshell.repository;

import javafx.collections.ObservableList;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.util.maven.example.Sequences;
import org.jooq.util.maven.example.tables.pojos.Domain;
import org.jooq.util.maven.example.tables.pojos.DomainValue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.DomainPojo;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.util.maven.example.tables.Domain.DOMAIN;
import static org.jooq.util.maven.example.tables.DomainValue.DOMAIN_VALUE;
import static org.jooq.util.maven.example.tables.Variable.VARIABLE;

@Repository
@Transactional
public class DomainRepository {

    private final DSLContext dslContext;

    public DomainRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public DomainPojo getDomainById(Long id) {
        return dslContext.selectFrom(DOMAIN)
                .where(DOMAIN.ID.eq(id))
                .orderBy(DOMAIN.ORDER)
                .fetchOneInto(DomainPojo.class);
    }

    public void insert(DomainPojo domainPojo) {
        domainPojo.setId(dslContext.nextval(Sequences.DOMAIN_ID_SEQ));
        dslContext.insertInto(DOMAIN)
                .columns(DOMAIN.ID, DOMAIN.NAME, DOMAIN.ES_ID, DOMAIN.ORDER)
                .values(domainPojo.getId(), domainPojo.getName(), domainPojo.getEsId(), domainPojo.getOrder() != null ? domainPojo.getOrder() : getCount(domainPojo.getEsId()) + 1)
        .execute();

        insertDomainValues(domainPojo);
        updateOrders(domainPojo.getEsId());
    }

    private void insertDomainValues(DomainPojo domainPojo) {
        for (DomainValue domainValue : domainPojo.getValues()) {
            dslContext.insertInto(DOMAIN_VALUE)
                    .columns(DOMAIN_VALUE.ID, DOMAIN_VALUE.DOMAIN_ID, DOMAIN_VALUE.VALUE, DOMAIN_VALUE.ORDER)
                    .values(dslContext.nextval(Sequences.DOMAIN_VALUES_ID_SEQ), domainPojo.getId(), domainValue.getValue(), getCountDomainValues(domainPojo.getId()) + 1)
            .execute();
        }
    }

    public void update(DomainPojo domainPojo) {
        dslContext.update(DOMAIN)
                .set(DOMAIN.NAME, domainPojo.getName())
                .set(DOMAIN.ES_ID, domainPojo.getEsId())
                .set(DOMAIN.ORDER, domainPojo.getOrder())
        .where(DOMAIN.ID.eq(domainPojo.getId()))
        .execute();


        for (DomainValue domainValue : domainPojo.getValues()) {
            if (domainValue.getId() == null) {
                dslContext.insertInto(DOMAIN_VALUE)
                        .columns(DOMAIN_VALUE.ID, DOMAIN_VALUE.DOMAIN_ID, DOMAIN_VALUE.VALUE, DOMAIN_VALUE.ORDER)
                        .values(dslContext.nextval(Sequences.DOMAIN_VALUES_ID_SEQ), domainPojo.getId(), domainValue.getValue(), getCountDomainValues(domainPojo.getId()) + 1)
                        .execute();
            } else {
                dslContext.update(DOMAIN_VALUE)
                        .set(DOMAIN_VALUE.VALUE, domainValue.getValue())
                        .where(DOMAIN_VALUE.ID.eq(domainValue.getId()))
                        .execute();
            }
        }

        dslContext.deleteFrom(DOMAIN_VALUE)
                .where(DOMAIN_VALUE.DOMAIN_ID.eq(domainPojo.getId())
                        .and(DOMAIN_VALUE.ID.notIn(domainPojo.getValues().stream()
                                .map(DomainValue::getId).collect(Collectors.toList()))))
                .execute();

    }

    public void delete(Long id) {
        dslContext.deleteFrom(DOMAIN_VALUE)
                .where(DOMAIN_VALUE.DOMAIN_ID.eq(id))
        .execute();
        dslContext.deleteFrom(DOMAIN)
                .where(DOMAIN.ID.eq(id))
        .execute();
    }

    public void updateOrders(Long esId) {
        List<Domain> domains = dslContext.selectFrom(DOMAIN)
                .where(DOMAIN.ES_ID.eq(esId))
                .orderBy(DOMAIN.ORDER, DOMAIN.ID)
                .fetchInto(Domain.class);

        Integer order = 1;
        for (Domain domain : domains) {
            dslContext.update(DOMAIN)
                    .set(DOMAIN.ORDER, order)
                    .where(DOMAIN.ID.eq(domain.getId()))
                    .execute();
            order++;
        }
    }

    public Integer getCount(Long esId) {
        return dslContext.selectCount()
                .from(DOMAIN)
                .where(DOMAIN.ES_ID.eq(esId))
                .fetchOneInto(Integer.class);
    }

    public Integer getCountDomainValues(Long domainId) {
        return dslContext.selectCount()
                .from(DOMAIN_VALUE)
                .where(DOMAIN_VALUE.DOMAIN_ID.eq(domainId))
                .fetchOneInto(Integer.class);
    }

    public List<DomainPojo> getList(Long esId) {
        return dslContext.selectFrom(DOMAIN)
                .where(DOMAIN.ES_ID.eq(esId))
                .orderBy(DOMAIN.ORDER)
                .fetchInto(DomainPojo.class);
    }

    public List<DomainValue> getValues(Long id) {
        return dslContext.selectFrom(DOMAIN_VALUE)
                .where(DOMAIN_VALUE.DOMAIN_ID.eq(id))
                .orderBy(DOMAIN_VALUE.ORDER)
                .fetchInto(DomainValue.class);
    }

    public boolean checkName(String name, Long esId) {
        return dslContext.fetchExists(
                dslContext.selectFrom(DOMAIN)
                        .where(DOMAIN.NAME.eq(name)
                                .and(DOMAIN.ES_ID.eq(esId)))
        );
    }

    public boolean checkValue(String value, Long domainId) {
        return dslContext.fetchExists(
                dslContext.selectFrom(DOMAIN_VALUE)
                        .where(DOMAIN_VALUE.VALUE.eq(value)
                        .and(DOMAIN_VALUE.DOMAIN_ID.eq(domainId)))
        );
    }

    public String getNameVariableByDomainId(Long domainId) {
        return dslContext.select(VARIABLE.NAME)
                .from(VARIABLE)
                .where(VARIABLE.DOMAIN_ID.eq(domainId))
                .fetchOneInto(String.class);
    }

    public DomainValue getDomainValueById(Long domainValueId) {
        return dslContext.selectFrom(DOMAIN_VALUE)
                .where(DOMAIN_VALUE.ID.eq(domainValueId))
                .fetchOneInto(DomainValue.class);
    }

    public void updateOrdersAfterDragAndDrop(List<DomainPojo> domains) {
        for (int i = 0; i < domains.size(); i++) {
            dslContext.update(DOMAIN)
                    .set(DOMAIN.ORDER, i+1)
                    .where(DOMAIN.ID.eq(domains.get(i).getId()))
                    .execute();
        }
    }
}
