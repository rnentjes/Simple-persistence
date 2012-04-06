package nl.astraeus.template;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import java.util.List;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:54 PM
 */
public abstract class TemplatePart {

    public abstract String render(Map<String, Object> model);

    protected String renderParts(List<TemplatePart> parts, Map<String, Object> model) {
        StringBuilder result = new StringBuilder();

        for (TemplatePart part : parts) {
            result.append(part.render(model));
        }

        return result.toString();
    }

    protected Object getValueFromModel(Map<String, Object> model, String valueName) {
        String [] parts = valueName.split("\\.");

        return getValueFromModel(model, parts);
    }

    protected Object getValueFromModel(Map<String, Object> model, String [] parts) {
        int index = 0;
        Object value = null;

        if (parts.length > index) {
            value = model.get(parts[index]);

            while(value != null && parts.length > ++index) {
                value = ReflectHelper.get().getMethodValue(value, parts[index]);
            }
        }

        return value;

    }
}
