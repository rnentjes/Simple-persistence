package nl.astraeus.http;

import nl.astraeus.util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnentjes
 * Date: 4/3/12
 * Time: 7:38 PM
 */
public class SimpleRequestThread implements Runnable {

    private final static int LINE_BUFFER_SIZE = 1024;

    private static final String HTTP_1_0 = "HTTP/1.0";
    private static final String HTTP_1_1 = "HTTP/1.1";

    private static int OUT_BUFFER = 1 << 16;
    private static int IN_BUFFER  = 1 << 14;

    private SocketChannel sc;
    private SimpleWebServer server;

    private Charset isoCharset = Charset.forName("ISO-8859-1");
    private Charset charset = Charset.forName("UTF-8");
    private CharsetEncoder encoder = charset.newEncoder();
    private CharsetDecoder decoder = charset.newDecoder();

    private boolean running = true;
    private boolean keepalive = true;
    private int requestNumber = 1;
    private Charset utf8 = Charset.forName("UTF-8");

    private char [] charbuffer = new char[LINE_BUFFER_SIZE];
    private byte [] linebuffer = new byte[LINE_BUFFER_SIZE];
    private CharBuffer chars = CharBuffer.allocate(LINE_BUFFER_SIZE);
    private char [] chararray = chars.array();
    ByteBuffer inBuffer = ByteBuffer.allocate(IN_BUFFER);
    private byte [] inarray = inBuffer.array();


    public SimpleRequestThread(SimpleWebServer server, SocketChannel sc) throws UnknownHostException, IOException {
        this.server = server;
        this.sc = sc;
    }

    private List<String> readBlock(ByteBuffer in, SocketChannel sc) throws IOException {
        List<String> result = new ArrayList<String>();
        String line;

        while(!"".equals((line = nextLine2(in, sc)))) {
            result.add(line);
        }

        return result;
    }

    private String [] readBlock2(ByteBuffer in, SocketChannel sc) throws IOException {
        boolean done = false;
        int bytes = -1;
        StringBuilder response = new StringBuilder();
        chars.clear();

        while(!done && (bytes = sc.read(in)) != -1) {
            in.flip();

            decoder.decode(in, chars, false);

            chars.flip();

            String tmp = new String(chararray, chars.position(), chars.remaining());

            if (tmp.endsWith("\r\n\r\n")) {
                done = true;
            }

            in.compact();
            chars.clear();

            response.append(tmp);
        }

        return response.toString().split("\r\n");
    }

    private String [] readHeaders(ByteBuffer in, SocketChannel sc) throws IOException {
        boolean done = false;
        int bytes = -1;
        StringBuilder response = new StringBuilder();
        chars.clear();

        while(!done && (bytes = sc.read(in)) != -1) {
            in.flip();

            decoder.decode(in, chars, false);

            chars.flip();

            String tmp = new String(chararray, chars.position(), chars.remaining());

            if (tmp.endsWith("\r\n\r\n")) {
                done = true;
            }

            in.compact();
            chars.clear();

            response.append(tmp);
        }

        return response.toString().split("\r\n");
    }

    private String readRemaining(ByteBuffer in, SocketChannel sc)throws IOException {
        int bytes = -1;
        StringBuilder response = new StringBuilder();
        boolean found = false;
        boolean first = true;

        while (in.hasRemaining()) {
            decoder.decode(in).toString();
            char ch = (char) in.get();

            response.append(ch);
        }

        return response.toString();
    }

    private String nextLineOld(ByteBuffer in, SocketChannel sc) throws IOException {
        int bytes = -1;
        StringBuilder response = new StringBuilder();
        boolean found = false;

        while (!found) {
            if (!in.hasRemaining()) {
                in.clear();
                bytes = sc.read(in);
                in.flip();
            }

            if (in.hasRemaining()) {
                char lastchar = ' ';

                while(in.hasRemaining() && !found) {
                    char ch = (char) in.get();

                    if (ch != '\r' && ch != '\n') {
                        response.append(ch);
                    } else if (ch == '\n' && lastchar == '\r') {
                        found = true;
                    }

                    lastchar = ch;
                }
            } else {
                response = new StringBuilder();
                response.append("");
                found = true;
            }
        }

        return response.toString();
    }

    private String nextLine(ByteBuffer in, SocketChannel sc) throws IOException {
        boolean found = false;
        int index = 0;
        String result = "";

        while (!found) {
            if (!in.hasRemaining()) {
                in.clear();
                sc.read(in);
                in.flip();
            }

            if (!in.hasRemaining()) {
                found = true;
            }

            while (!found && in.hasRemaining() && index < LINE_BUFFER_SIZE) {
                charbuffer[index++] = (char)in.get();

                if (index > 1 && charbuffer[index-2] == '\r' && charbuffer[index-1] == '\n') {
                    result = new String(charbuffer); //linebuffer, 0, index-2, charset);
                    found = true;
                }
            }
        }

        return result;
    }

    //inarray
    //String tmp = new String(inarray, inBuffer.position(), inBuffer.position()+index)
    // inBuffer setPosition();
    private String nextLine2(ByteBuffer in, SocketChannel sc) throws IOException {
        boolean found = false;
        StringBuilder result = new StringBuilder();

        while (!found) {
            int index = 0;
            if (!in.hasRemaining()) {
                in.clear();
                sc.read(in);
                in.flip();
            }

            if (!in.hasRemaining()) {
                found = true;
            }

            for (int p = inBuffer.position(); !found && p < inBuffer.limit(); p++) {
                linebuffer[index++] = inarray[p];
                if (p > inBuffer.position() && inarray[p-1] == '\r' && inarray[p] == '\n') {
                    // found one line
                    found = true;

                    result.append(new String(linebuffer, 0, index-2, charset));

                    /*
                    result.append(new String(inarray, inBuffer.position(), (p - inBuffer.position()) -1, isoCharset));
                    */
                    inBuffer.position(p + 1);

                }
            }

            if (!found) {
                result.append(new String(inarray, inBuffer.position(), inBuffer.limit(), charset));
                inBuffer.position(inBuffer.limit());
            }
        }

        return result.toString();
    }

    /*
    chars.clear();

        decoder.decode(in, chars, false);

        chars.flip();

        String tmp = new String(chararray, chars.position(), chars.remaining());
*/

    //inarray
    //String tmp = new String(inarray, inBuffer.position(), inBuffer.position()+index)
    // inBuffer setPosition();
    private String nextLine3(ByteBuffer in, SocketChannel sc) throws IOException {
        boolean found = false;
        StringBuilder result = new StringBuilder();

        while (!found) {
            if (!in.hasRemaining()) {
                in.clear();
                sc.read(in);
                in.flip();
            }

            if (!in.hasRemaining()) {
                found = true;
            }

            for (int p = inBuffer.position(); !found && p < inBuffer.limit(); p++) {
                if (p > inBuffer.position() && inarray[p-1] == '\r' && inarray[p] == '\n') {
                    // found one line
                    found = true;

                    int oldLimit = inBuffer.limit();

                    inBuffer.limit(p-1);

                    chars.clear();

                    decoder.decode(in, chars, false);

                    chars.flip();

                    String tmp = new String(chararray, chars.position(), chars.remaining());

                    result.append(tmp);
                    inBuffer.limit(oldLimit);
                    inBuffer.position(p + 1);
                }
            }

            if (!found) {
                result.append(new String(inarray, inBuffer.position(), inBuffer.limit(), charset));
                inBuffer.position(inBuffer.limit());
            }
        }

        return result.toString();
    }



    private String readCharacters(ByteBuffer in, SocketChannel sc, int size) throws IOException {
        byte [] result = new byte[size];
        int index = 0;

        while (index < size) {
            if (in.hasRemaining()) {
                result[index++] = in.get();
            } else {
                in.clear();
                int bytes = sc.read(in);
                in.flip();

                if (bytes == -1) {
                    break;
                }
            }
        }

        return new String(result, charset);
    }

    public void run() {

        try {
            ByteBuffer outBuffer = ByteBuffer.allocate(OUT_BUFFER);

            inBuffer.clear();
            inBuffer.flip();
            outBuffer.clear();

            while (running && keepalive) {
                //List<String> lines = readBlock(inBuffer, sc);
                //String [] test = readBlock2(inBuffer, sc);

                List<String> lines = readBlock(inBuffer, sc);

//                for (String l : test) {
//                    lines.add(l);
//                }

                keepalive = false;

                long requestStart = System.nanoTime();

                SimpleHttpRequest request = null;
                SimpleHttpResponse response = null;

                if (!lines.isEmpty()) {
                    String in = lines.get(0);
                    System.out.println("IN: "+in);
                    if (in.startsWith("GET")) {
                        if (in.endsWith(HTTP_1_1)) {
                            request = new SimpleHttpRequest(server, HttpMethod.GET, in.substring(4, in.length() - HTTP_1_1.length()), true);
                            request.readHeaders(lines);
                        } else if (in.endsWith(HTTP_1_0)) {
                            request = new SimpleHttpRequest(server, HttpMethod.GET, in.substring(4, in.length() - HTTP_1_0.length()), false);
                            request.readHeaders(lines);
                        } else {
                            throw new IllegalStateException("Don't know how to handle: [" + in + "]");
                        }
                    } else if (in.startsWith("POST")) {
                        if (in.endsWith(HTTP_1_1)) {
                            request = new SimpleHttpRequest(server, HttpMethod.POST, in.substring(5, in.length() - HTTP_1_1.length()), true);
                            request.readHeaders(lines);
                            request.parseRequestParameters(readCharacters(inBuffer, sc, request.getContentLength()));
                        } else if (in.endsWith(HTTP_1_0)) {
                            request = new SimpleHttpRequest(server, HttpMethod.POST, in.substring(5, in.length() - HTTP_1_0.length()), false);
                            request.readHeaders(lines);
                            request.parseRequestParameters(readCharacters(inBuffer, sc, request.getContentLength()));
                        } else {
                            throw new IllegalStateException("Don't know how to handle: [" + in + "]");
                        }
                    }

                    keepalive = request.getKeepAlive();
                }

                if (request == null) {
                    response = new SimpleHttpResponse(server);
                    response.sendError(404, "Unknown request type (only GET supported!)");
                } else {
                    HttpServlet servlet = server.findHandlingServlet(request.getRequestURI());

                    if (servlet == null) {
                        response = new SimpleHttpResponse(server);
                        response.sendError(404, "No handler defined for: " + request.getRequestURI());
                    } else {
                        response = new SimpleHttpResponse(server);

                        servlet.service(request, response);

                        try {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();

                            response.writeToOutputStream(request, new DataOutputStream(out));
                            //byte [] bytes = ((SimpleServletOutputStream)response.getOutputStream()).getBytes();

                            writeBytesToChannel(out.toByteArray(), outBuffer, sc);
                            writeBytesToChannel(((SimpleServletOutputStream)response.getOutputStream()).getBytes(), outBuffer, sc);

                        } catch (SocketException e) {
                            // connection reset by client, ignoring
                        }

                        System.out.println("Request " + request.getRequestURI() + " took " + Util.formatNano(System.nanoTime() - requestStart));
                    }
                }
            }
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sc != null) {
                    sc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeBytesToChannel(byte [] bytes, ByteBuffer outBuffer, SocketChannel sc) throws IOException {
        int offset = 0;

        while(offset < bytes.length) {
            int length = OUT_BUFFER;

            if ((offset + length) > bytes.length) {
                length = bytes.length - offset;
            }

            outBuffer.clear();
            outBuffer.put(bytes, offset, length);

            outBuffer.flip();

            while(outBuffer.hasRemaining()) {
                sc.write(outBuffer);
            }

            outBuffer.flip();

            offset += OUT_BUFFER;
        }

    }


}