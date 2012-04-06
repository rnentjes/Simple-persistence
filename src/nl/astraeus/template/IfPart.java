package nl.astraeus.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:56 PM
 */
public class IfPart extends TemplatePart {

    protected BooleanCondition ifCondition;
    private List<TemplatePart> ifParts;
    private List<TemplatePart> elseParts;
    private boolean hasElse;

    public IfPart(String ifCondition) {
        this.ifCondition = new BooleanCondition(ifCondition);
        this.ifParts = new ArrayList<TemplatePart>();
        this.elseParts = new ArrayList<TemplatePart>();
    }

    public void setIfParts(List<TemplatePart> ifParts) {
        this.ifParts = ifParts;
    }

    public List<TemplatePart> getIfParts() {
        return ifParts;
    }

    public void setElseParts(List<TemplatePart> elseParts) {
        this.elseParts = elseParts;
    }

    public boolean isHasElse() {
        return hasElse;
    }

    public void setHasElse(boolean hasElse) {
        this.hasElse = hasElse;
    }

    @Override
    public String render(Map<String, Object> model) {
        String result = "";

        if (ifCondition.evaluate(model)) {
            result = renderParts(ifParts, model);
        } else {
            result = renderParts(elseParts, model);
        }

        return result;
    }
}
