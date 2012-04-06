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
public class SimpleServletContext extends AttributeParameterHolder implements ServletContext {

    private SimpleWebServer server;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, String> initParameters = new HashMap<String, String>();
    private Map<String, HttpServlet> servlets = new HashMap<String, HttpServlet>();

    public SimpleServletContext(SimpleWebServer server) {
        this.server = server;
    }

    public String getContextPath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ServletContext getContext(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public String getMimeType(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set getResourcePaths(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL getResource(String s) throws MalformedURLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getResourceAsStream(String s) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(s);
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Servlet getServlet(String s) throws ServletException {
        return servlets.get(s);
    }

    public Enumeration<HttpServlet> getServlets() {
        final Iterator<HttpServlet> it = servlets.values().iterator();

        return new Enumeration<HttpServlet>() {
            
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            
            public HttpServlet nextElement() {
                return it.next();
            }
        };
    }

    public Enumeration<String> getServletNames() {
        final Iterator<String> it = servlets.keySet().iterator();

        return new Enumeration<String>() {
            
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            
            public String nextElement() {
                return it.next();
            }
        };
    }

    public void log(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void log(Exception e, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void log(String s, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRealPath(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getServerInfo() {
        return "Simple-Web-Server v1.0";
    }

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

    public String getServletContextName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
