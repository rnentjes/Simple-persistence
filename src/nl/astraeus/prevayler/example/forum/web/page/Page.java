package nl.astraeus.prevayler.example.forum.web.page;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:20 PM
 */
public abstract class Page {
    
    private ST template;

    public abstract Page processRequest(HttpServletRequest request);
    public abstract Map<String, Object> defineModel(HttpServletRequest request);

    protected Page() {
        template = getTemplate(this.getClass(), this.getClass().getSimpleName() + ".html");
    }
    
    public String render(HttpServletRequest request) {
        setAttributesOnTemplate(template, defineModel(request));

        return template.render();
    }
    
    public static ST getTemplate(Class cls, String file) {
        InputStream in = null;

        try {
            in = cls.getResourceAsStream(file);

            String template = IOUtils.toString(in);

            ST st = new ST(template, '$', '$');

            return st;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void setAttributesOnTemplate(ST template, Map<String, Object> model) {
        if (template != null && template.getAttributes() != null) {
            for (Map.Entry<String, Object> entry : template.getAttributes().entrySet()) {
                template.remove(entry.getKey());
            }
        }

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            template.add(entry.getKey(), entry.getValue());
        }
    }
}
