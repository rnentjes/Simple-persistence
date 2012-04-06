package nl.astraeus.forum.web.page;

import nl.astraeus.template.SimpleTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 8:41 PM
 */
public abstract class SimpleTemplatePage extends Page {

    private SimpleTemplate template;

    protected SimpleTemplatePage() {
        template = getSimpleTemplate(this.getClass());
    }

    public String render(HttpServletRequest request) {
        return template.render(defineModel(request));
    }

    private static Map<Class, SimpleTemplate> templateCache = new HashMap<Class, SimpleTemplate>();

    public synchronized static SimpleTemplate getSimpleTemplate(Class cls) {
        SimpleTemplate result = templateCache.get(cls);

        if (result == null) {
            InputStream in = null;

            try {
                in = cls.getResourceAsStream(cls.getSimpleName() + ".html");

                result = new SimpleTemplate('@', in);

                templateCache.put(cls, result);
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

        return result;
    }
}
