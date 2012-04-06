package nl.astraeus.template;

import java.util.*;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:56 PM
 */
public class ForEachPart extends TemplatePart {

    private List<TemplatePart> parts;
    private List<TemplatePart> altParts;
    private List<TemplatePart> lastParts;

    private boolean hasAlt;
    private boolean hasLast;

    private String [] modelParts;
    private String parameterName;

    public ForEachPart(String modelObject, String parameterName) {
        this.modelParts = modelObject.split("\\.");
        this.parameterName = parameterName;

        this.parts = new ArrayList<TemplatePart>();
        this.altParts = new ArrayList<TemplatePart>();
        this.lastParts = new ArrayList<TemplatePart>();

        this.hasAlt = false;
        this.hasLast = false;
    }

    public void setParts(List<TemplatePart> parts) {
        this.parts = parts;
    }

    public void setAltParts(List<TemplatePart> altParts) {
        this.altParts = altParts;
    }

    public void setLastParts(List<TemplatePart> lastParts) {
        this.lastParts = lastParts;
    }

    public boolean isHasAlt() {
        return hasAlt;
    }

    public void setHasAlt(boolean hasElse) {
        this.hasAlt = hasElse;
    }

    public boolean isHasLast() {
        return hasLast;
    }

    public void setHasLast(boolean hasLast) {
        this.hasLast = hasLast;
    }

    @Override
    public String render(Map<String, Object> model) {
        boolean alt = true;
        StringBuilder result = new StringBuilder();
        Map<String, Object> tmpModel = new HashMap<String, Object>(model);

        tmpModel.remove(modelParts[0]);
        Object value = getValueFromModel(model, modelParts);

        if (value instanceof Iterable) {
            Iterator it = ((Iterable)value).iterator();

            while(it.hasNext()) {
                Object object = it.next();
                tmpModel.put(parameterName, object);

                if (hasLast && !it.hasNext()) {
                    result.append(renderParts(lastParts, tmpModel));
                } else {
                    if (hasAlt && alt) {
                        result.append(renderParts(altParts, tmpModel));
                    } else {
                        result.append(renderParts(parts, tmpModel));
                    }
                }

                alt = !alt;
            }
        }

        return result.toString();
    }
}
