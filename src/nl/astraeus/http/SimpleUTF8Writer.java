package nl.astraeus.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 9:47 AM
 */
public class SimpleUTF8Writer extends Writer {

    private OutputStream outputStream;
    private Charset utf8;

    public SimpleUTF8Writer(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.utf8 = Charset.forName("UTF-8");
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (int i=off; i < off+len; i++) {
            if (cbuf.length > i) {
                builder.append(cbuf[i]);
            }
        }

        outputStream.write(builder.toString().getBytes(utf8));
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
