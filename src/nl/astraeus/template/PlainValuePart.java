package nl.astraeus.template;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:55 PM
 */
public class PlainValuePart extends TemplatePart {

    private String [] parts;

    public PlainValuePart(String text) {
        parts = text.split("\\.");
    }

    @Override
    public String render(Map<String, Object> model) {
        return String.valueOf(getValueFromModel(model, parts));
    }
}
