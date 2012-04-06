package nl.astraeus.http;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * User: rnentjes
 * Date: 4/3/12
 * Time: 9:24 PM
 */
abstract class AttributeParameterHolder {

    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, String> initParameters = new HashMap<String, String>();
    private Map<String, String []> parameters = new HashMap<String, String []>();

    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    public Enumeration getInitParameterNames() {
        final Iterator<String> it = initParameters.keySet().iterator();

        return new Enumeration<String>() {
            
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            
            public String nextElement() {
                return it.next();
            }
        };
    }

    void addParameter(String name, String value) {
        String [] values = parameters.get(name);

        if (values == null) {
            values = new String[1];
            values[0] = value;
            parameters.put(name, values);
        } else {
            String [] newValues = Arrays.copyOf(values, values.length+1);
            newValues[newValues.length] = value;
            parameters.put(name, newValues);
        }
    }

    public String getParameter(String s) {
        String result = null;
        String [] values = parameters.get(s);

        if (values != null && values.length > 0) {
            result = values[0];
        }

        return result;
    }

    public Enumeration getParameterNames() {
        final Iterator<String> it = parameters.keySet().iterator();

        return new Enumeration<String>() {

            public boolean hasMoreElements() {
                return it.hasNext();
            }


            public String nextElement() {
                return it.next();
            }
        };
    }

    public String[] getParameterValues(String s) {
        return parameters.get(s);
    }

    public Map getParameterMap() {
        return parameters;
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public Enumeration<String> getAttributeNames() {
        final Iterator<String> it = attributes.keySet().iterator();

        return new Enumeration<String>() {
            
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            
            public String nextElement() {
                return it.next();
            }
        };
    }

    public void setAttribute(String s, Object o) {
        attributes.put(s,o);
    }


    public void removeAttribute(String s) {
        attributes.remove(s);
    }
}
