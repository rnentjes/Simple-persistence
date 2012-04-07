package nl.astraeus.http;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/3/12
 * Time: 9:21 PM
 */
public class SimpleHttpRequest extends AttributeParameterHolder implements HttpServletRequest {
    final static String SESSION_COOKIE = "SWSSessionID";

    private String queryString;
    private HttpMethod httpMethod = null;
    private String uri;
    private boolean http11;
    private boolean headersRead;
    private boolean keepAlive;
    private SimpleHttpSession session;
    private SimpleWebServer server;
    private String contentType;
    private int contentLength = -1;

    public SimpleHttpRequest(SimpleWebServer server, HttpMethod httpMethod, String requestString, boolean http11) {
        this.server = server;
        this.session = null;
        this.httpMethod = httpMethod;
        this.http11 = http11;
        this.headersRead = false;
        this.keepAlive = http11;
        this.contentType = "";

        int qmloc = requestString.indexOf('?');

        if (qmloc > -1) {
            uri = requestString.substring(0, qmloc);
            queryString = requestString.substring(qmloc+1);
        } else {
            uri = requestString.trim();
            queryString = "";
        }
    }

    boolean headersRead() {
        return headersRead;
    }

    void readHeaders(Map<HttpHeader, String> headers) throws IOException {
        headersRead = true;

        keepAlive = headers.get(HttpHeader.CONNECTION) != null;
        if (headers.get(HttpHeader.CONTENT_LENGTH) != null) {
            contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH));
        } else {
            contentLength = 0;
        }
        addCookie(headers.get(HttpHeader.COOKIE));
        contentType = headers.get(HttpHeader.CONTENT_TYPE);
    }

    void parseRequestParameters(String formdata) throws UnsupportedEncodingException {

        if (formdata != null && !formdata.isEmpty()) {
            if (formdata.endsWith("&")) {
                formdata = formdata + queryString;
            } else {
                formdata = formdata + "&" + queryString;
            }

            String [] parts = formdata.split("\\&");

            for (String part : parts) {
                String [] sp = part.split("=");

                if (sp.length == 1) {
                    String name = URLDecoder.decode(sp[0],"UTF-8");
                    String value = "";

                    addParameter(name, value);
                } else if ((sp.length == 2)) {
                    String name = URLDecoder.decode(sp[0],"UTF-8");
                    String value = URLDecoder.decode(sp[1],"UTF-8");

                    addParameter(name, value);
                }
            }
        }
    }

    private void addCookie(String in) {
        String [] parts = in.split("\\;");

        for (String part : parts) {
            String [] sp = part.split("\\=");

            if (sp.length == 2) {
                if (sp[0].trim().equals(SESSION_COOKIE)) {
                    session = server.getSession(sp[1]);
                }
            }
        }

    }

    HttpMethod getHttpMethod() {
        return httpMethod;
    }

    void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getAuthType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Cookie[] getCookies() {
        return new Cookie[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getDateHeader(String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getHeader(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getHeaders(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getHeaderNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIntHeader(String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getMethod() {
        return httpMethod.toString();
    }

    public String getPathInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPathTranslated() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getContextPath() {
        return "";
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isUserInRole(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Principal getUserPrincipal() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRequestedSessionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRequestURI() {
        return uri;
    }

    public StringBuffer getRequestURL() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getServletPath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HttpSession getSession(boolean b) {
        HttpSession result = session;

        if (b && session == null) {
            result = getSession();
        }

        return result;
    }

    public HttpSession getSession() {
        if (session == null) {
            session = server.getSession(null);
        }

        session.setLastAccessedTime();

        return session;
    }

    public boolean isRequestedSessionIdValid() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getCharacterEncoding() {
        return "UTF-8";
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getProtocol() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getScheme() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getServerName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getServerPort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BufferedReader getReader() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRemoteAddr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRemoteHost() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Locale getLocale() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getLocales() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isSecure() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRealPath(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getRemotePort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalAddr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getLocalPort() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getHttp11() {
        return http11;
    }

    public boolean getKeepAlive() {
        return keepAlive;
    }

    //@CheckForNull
    public String getSessionId() {
        String result = null;

        if (getSession(false) != null) {
            result = getSession().getId();
        }

        return result;
    }
}
