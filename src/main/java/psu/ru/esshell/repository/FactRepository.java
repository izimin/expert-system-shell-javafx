package psu.ru.esshell.repository;

import org.jooq.DSLContext;
import org.jooq.util.maven.example.Sequences;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psu.ru.esshell.model.pojos.FactPojo;

import static org.jooq.util.maven.example.tables.Fact.FACT;

@Repository
@Transactional
public class FactRepository {

    private final DSLContext dslContext;

    public FactRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Long insert(FactPojo factPojo) {
        factPojo.setId(dslContext.nextval(Sequences.FACT_ID_SEQ));
        dslContext.insertInto(FACT)
                .columns(FACT.ID, FACT.VARIABLE_ID, FACT.DOMAIN_VALUE_ID)
                .values(factPojo.getId(), factPojo.getVariableId(), factPojo.getDomainValueId())
                .execute();
        return factPojo.getId();
    }

    public Long getId(FactPojo factPojo) {
        return dslContext.select(FACT.ID)
                .from(FACT)
                .where(FACT.VARIABLE_ID.eq(factPojo.getVariableId()))
                .and(FACT.DOMAIN_VALUE_ID.eq(factPojo.getDomainValueId()))
                .fetchOneInto(Long.class);
    }

    public FactPojo getById(Long id) {
        return dslContext.selectFrom(FACT)
                .where(FACT.ID.eq(id))
                .fetchOneInto(FactPojo.class);
    }

    public boolean checkExists(FactPojo factPojo) {
        return dslContext.fetchExists(
                dslContext.selectFrom(FACT)
                .where(FACT.VARIABLE_ID.eq(factPojo.getVariableId()))
                        .and(FACT.DOMAIN_VALUE_ID.eq(factPojo.getDomainValueId()))
        );
    }
}
