package nl.astraeus.persistence.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: rnentjes
 * Date: 11/22/12
 * Time: 9:11 PM
 */
public class FastByteArrayOutputStream extends OutputStream {

    protected byte [] buffer;
    protected int size = 0;

    public FastByteArrayOutputStream() {
        this(2<<16);
    }

    public FastByteArrayOutputStream(int initialSize) {
        this.buffer = new byte[initialSize];
        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    /**
     * Ensures that we have a large enough buffer for the given size.
     */
    private void verifyBufferSize(int sz) {
        if (sz > buffer.length) {
            byte[] old = buffer;
            buffer = new byte[Math.max(sz, 2 * buffer.length )];
            System.arraycopy(old, 0, buffer, 0, old.length);
            old = null;
        }
    }

    /**
     * Returns the byte array containing the written data. Note that this
     * array will almost always be larger than the amount of data actually
     * written.
     */
    public byte[] getByteArray() {
        return buffer;
    }

    public final void write(byte b[]) {
        verifyBufferSize(size + b.length);
        System.arraycopy(b, 0, buffer, size, b.length);
        size += b.length;
    }

    public final void write(byte b[], int off, int len) {
        verifyBufferSize(size + len);
        System.arraycopy(b, off, buffer, size, len);
        size += len;
    }

    public final void write(int b) {
        verifyBufferSize(size + 1);
        buffer[size++] = (byte) b;
    }

    public void reset() {
        size = 0;
    }

    /**
     * Returns a ByteArrayInputStream for reading back the written data
     */
    public InputStream getInputStream() {
        return new FastByteArrayInputStream(buffer, size);
    }
}
