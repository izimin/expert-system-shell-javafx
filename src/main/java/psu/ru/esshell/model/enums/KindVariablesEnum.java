package psu.ru.esshell.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.util.maven.example.enums.KindVariable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zimin
 */

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KindVariablesEnum {

    OUTPUT(1L, "Выводимая"),
    REQUESTED(2L, "Запрашиваемая"),
    OUTPUT_REQUESTED(3L, "Выводимо-запрашиваемая"),
    ;

    private final Long id;
    private final String name;

    static private Map<Long, KindVariablesEnum> kindVariablesEvents;

    static {
        kindVariablesEvents = new HashMap<>();
        Arrays.stream(KindVariablesEnum.values()).forEach(event -> kindVariablesEvents.put(event.id, event));
    }

    public static KindVariablesEnum getById(Long id) {
        return kindVariablesEvents.get(id);
    }

    public static String getNameByLiteral(KindVariable kindVariable) {
        if (kindVariable.getLiteral().equals("output")) {
            return getById(1L).getName();
        } else if (kindVariable.getLiteral().equals("requested")) {
            return getById(2L).getName();
        } else {
            return getById(3L).getName();
        }
    }
}