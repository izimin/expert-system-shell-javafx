package psu.ru.esshell.model.pojos;

import lombok.Getter;
import lombok.Setter;
import org.jooq.util.maven.example.tables.pojos.Rule;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RulePojo extends Rule {
    List<FactPojo> conditions = new LinkedList<>();
    List<FactPojo> conclusions = new LinkedList<>();
    String content = "";

    public RulePojo(Long esId) {
        super(null, null, null, esId);
    }

    public RulePojo() {

    }

    public String getContent() {
        return "IF " + conditions.stream().map(FactPojo::getContent).collect(Collectors.joining(" AND ")) +
                " THEN " + conclusions.stream().map(FactPojo::getContent).collect(Collectors.joining("; "));
    }

    @Override
    public String toString() {
        return getContent();
    }
}
