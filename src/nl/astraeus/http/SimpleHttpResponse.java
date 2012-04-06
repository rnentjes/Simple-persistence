package nl.astraeus.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.StringContent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 8:52 AM
 */
public class SimpleHttpResponse implements HttpServletResponse {
    private SimpleServletOutputStream outputStream;
    private PrintWriter printWriter;
    private int responseCode;
    private String errorMessage;
    private Map<String, String> headers;
    private Map<Integer, String> responseMessages;
    private SimpleWebServer server;

    private String contentType;

    public SimpleHttpResponse(SimpleWebServer server) {
        this.server = server;
        this.outputStream = new SimpleServletOutputStream();
        this.printWriter = null;
        this.responseCode = 200;
        this.errorMessage = "";

        this.contentType = "text/html";

        this.headers = new HashMap<String, String>();
        this.responseMessages = new HashMap<Integer, String>();

        responseMessages.put(200, "OK");
        responseMessages.put(400, "Bad Request");
        responseMessages.put(404, "Not Found");
    }

    void writeToOutputStream(SimpleHttpRequest request, DataOutputStream output) throws IOException {
        if (request != null && request.getHttp11()) {
            output.writeBytes("HTTP/1.1 ");
        } else {
            output.writeBytes("HTTP/1.0 ");
        }
        output.writeBytes(Integer.toString(responseCode));
        output.writeBytes(" ");
        output.writeBytes(getResponseCode());
        output.writeBytes("\r\n");

        if (responseCode != 200) {
            setContentType("text/plain");
            resetBuffer();
            getWriter().print(errorMessage);
        }

        output.writeBytes("Content-Type: ");
        output.writeBytes(getContentType());
        output.writeBytes("\r\n");

        output.writeBytes("Content-Length: ");
        output.writeBytes(Integer.toString(outputStream.length()));
        output.writeBytes("\r\n");

        //Set-Cookie: name2=value2; Expires=Wed, 09 Jun 2021 10:18:14 GMT
        if (request != null && request.getSessionId() != null) {
            output.writeBytes("Set-Cookie: ");
            output.writeBytes(SimpleHttpRequest.SESSION_COOKIE);
            output.writeBytes("=");
            output.writeBytes(request.getSessionId());
            output.writeBytes("\r\n");
        }

        output.writeBytes("Connection: keep-alive\r\n");
        output.writeBytes("\r\n");
    }

    private String getResponseCode() {
        String result = responseMessages.get(responseCode);

        if (result == null) {
            result = "500 Internal Server Error";
        }

        return result;
    }


    public void addCookie(Cookie cookie) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean containsHeader(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String encodeURL(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String encodeRedirectURL(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String encodeUrl(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String encodeRedirectUrl(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendError(int i, String s) throws IOException {
        responseCode = i;
        errorMessage = s;
    }

    public void sendError(int i) throws IOException {
        responseCode = i;
    }

    public void sendRedirect(String s) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDateHeader(String s, long l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addDateHeader(String s, long l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setHeader(String s, String s1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addHeader(String s, String s1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setIntHeader(String s, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addIntHeader(String s, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setStatus(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setStatus(int i, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getCharacterEncoding() {
        return "UTF-8";
    }

    public String getContentType() {
        return contentType;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        if (printWriter == null) {
            printWriter = new PrintWriter(new SimpleUTF8Writer(outputStream));
        }

        return printWriter;
    }

    public void setCharacterEncoding(String s) {
        throw new IllegalStateException("Changing encoding is not allowed, encoding is hardcoded to UTF-8.");
    }

    public void setContentLength(int i) {
        throw new IllegalStateException("Setting content length not supported (it's calculated)");
    }

    public void setContentType(String s) {
        this.contentType = s;
    }

    public void setBufferSize(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getBufferSize() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void flushBuffer() throws IOException {
    }

    public void resetBuffer() {
        printWriter = null;
        outputStream = new SimpleServletOutputStream();
    }

    public boolean isCommitted() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLocale(Locale locale) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Locale getLocale() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
