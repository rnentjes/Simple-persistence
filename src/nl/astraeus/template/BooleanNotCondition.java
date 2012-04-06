package nl.astraeus.template;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 4:20 PM
 */
public class BooleanNotCondition extends BooleanCondition {

    public BooleanNotCondition(String text) {
        super(text);
    }

    @Override
    public boolean evaluate(Map<String, Object> model) {
        return !super.evaluate(model);
    }


}
