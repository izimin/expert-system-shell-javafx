package psu.ru.esshell.model.pojos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.util.maven.example.enums.KindVariable;
import org.jooq.util.maven.example.tables.pojos.Variable;

/**
 * @author zimin
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariablePojo extends Variable {
    DomainPojo domain;
    String kindName;
    String domainName;

    public VariablePojo(Long id, String name, Long domainId, Long esId, Integer order, KindVariable kind, String question) {
        super(id, name, domainId, esId, order, kind, question);
        this.domain = null;
        this.kindName = null;
        this.domainName = null;
    }

    public VariablePojo(Long esId) {
        super(null, null, null, esId, null, null, null);
        this.domain = null;
        this.kindName = null;
        this.domainName = null;
    }
}
