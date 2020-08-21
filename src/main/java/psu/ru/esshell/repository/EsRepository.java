package psu.ru.esshell.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.jooq.util.maven.example.tables.Es.ES;

@Repository
@Transactional
public class EsRepository {

    private final DSLContext dslContext;

    public EsRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Long getEsNameById(Long esId) {
        return dslContext.select(ES.NAME)
                .from(ES)
                .where(ES.ID.eq(esId))
                .fetchOneInto(Long.class);
    }

    public Long updateName(Long esId) {
        return dslContext.select(ES.NAME)
                .from(ES)
                .where(ES.ID.eq(esId))
                .fetchOneInto(Long.class);
    }

}
