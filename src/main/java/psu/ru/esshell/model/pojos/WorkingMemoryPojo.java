package psu.ru.esshell.model.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.util.maven.example.tables.pojos.WorkingMemory;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkingMemoryPojo extends WorkingMemory {
    private String content;
}
