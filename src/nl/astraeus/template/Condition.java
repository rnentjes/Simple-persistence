package nl.astraeus.template;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 4:19 PM
 */
public abstract class Condition {

    public abstract boolean evaluate(Map<String, Object> model);
}
