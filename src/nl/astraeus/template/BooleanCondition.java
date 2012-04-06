package nl.astraeus.template;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 4:20 PM
 */
public class BooleanCondition extends Condition {

    private String[] parts;

    public BooleanCondition(String text) {
        parts = text.split("\\.");
    }

    @Override
    public boolean evaluate(Map<String, Object> model) {
        boolean result = false;
        int index = 0;
        Object value = null;

        try {
            if (parts.length > index) {
                value = model.get(parts[index]);

                while (value != null && parts.length > ++index) {
                    value = ReflectHelper.get().getMethodValue(value, parts[index]);
                }
            }

            if (value != null) {
                if (value instanceof Boolean) {
                    result = (Boolean) value;
                } else {
                    result = true;
                }
            }
        } catch (IllegalArgumentException e) {
            if (e.getCause().getClass().equals(NoSuchMethodException.class)) {
                result = false;
            } else {
                throw e;
            }
        }

        return result;
    }


}
