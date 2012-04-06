package nl.astraeus.template;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:55 PM
 */
public class StringPart extends TemplatePart {

    private String part;

    public StringPart(String part) {
        this.part = part;
    }

    @Override
    public String render(Map<String, Object> model) {
        return part;
    }
}
