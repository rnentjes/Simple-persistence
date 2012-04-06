package nl.astraeus.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * User: rnentjes
 * Date: 4/3/12
 * Time: 9:23 PM
 */
public class SimpleHttpSession extends AttributeParameterHolder implements HttpSession {

    private long creationTime = System.currentTimeMillis();
    private String sessionId = null;
    private SimpleWebServer server;
    private long lastAccessedTime = 0;

    public SimpleHttpSession(SimpleWebServer server, String sessionId) {
        this.server = server;
        this.sessionId = sessionId;

        lastAccessedTime = System.currentTimeMillis();
    }

    public String getId() {
        return sessionId;
    }

    void setLastAccessedTime() {
        lastAccessedTime = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public ServletContext getServletContext() {
        return server.getServletContext();
    }


    public void setMaxInactiveInterval(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public int getMaxInactiveInterval() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public HttpSessionContext getSessionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object getValue(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String[] getValueNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void putValue(String s, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void removeValue(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void invalidate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isNew() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
