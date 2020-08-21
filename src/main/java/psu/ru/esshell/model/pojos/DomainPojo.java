package psu.ru.esshell.model.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.util.maven.example.tables.pojos.Domain;
import org.jooq.util.maven.example.tables.pojos.DomainValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zimin
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DomainPojo extends Domain {
    List<DomainValue> values = new ArrayList<>();                    // Значения домена

    public DomainPojo(Long id, String name, Long esId, Integer order) {
        super(id, name, esId, order);
        this.values = new ArrayList<>();
    }

    public DomainPojo(Long esId) {
        super(null, null, esId, null);
        this.values = new ArrayList<>();
    }
}
