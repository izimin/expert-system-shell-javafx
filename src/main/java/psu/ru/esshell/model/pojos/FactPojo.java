package psu.ru.esshell.model.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.util.maven.example.tables.pojos.Fact;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FactPojo extends Fact {
    String content = "";
    String variableName = "";
    String variableValue = "";

    public FactPojo(Long id, Long variableId, Long domainValueId, String content, String variableName, String variableValue) {
        super(id, variableId, domainValueId);
        this.content = content;
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    public FactPojo(Long variableId, Long domainValueId) {
        super(null, variableId, domainValueId);
    }
}
