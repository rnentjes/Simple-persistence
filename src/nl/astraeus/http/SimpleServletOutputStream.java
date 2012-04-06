package nl.astraeus.http;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 8:54 AM
 */
public class SimpleServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream outputStream;

    private ByteArrayOutputStream getOutputStream() {
        if (outputStream == null) {
            outputStream = new ByteArrayOutputStream(8192);
        }

        return outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        getOutputStream().write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        getOutputStream().write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        getOutputStream().write(b, off, len);
    }

    public int length() {
        return getOutputStream().size();
    }

    public byte [] getBytes() {
        return getOutputStream().toByteArray();
    }

}
