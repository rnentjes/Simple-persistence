package nl.astraeus.template;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:55 PM
 */
public class ValuePart extends TemplatePart {

    private String [] parts;

    public ValuePart(String text) {
        parts = text.split("\\.");
    }

    @Override
    public String render(Map<String, Object> model) {
        String result = String.valueOf(getValueFromModel(model, parts));

        // get current output target and escape our result
        // result = escapedResult...

        return result;
    }

}
