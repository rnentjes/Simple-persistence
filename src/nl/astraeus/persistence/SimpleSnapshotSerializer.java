package nl.astraeus.persistence;

import org.prevayler.foundation.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:07 PM
 */
public class SimpleSnapshotSerializer implements Serializer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleSnapshotSerializer.class);

    public void writeObject(OutputStream stream, Object object) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(stream);
        DataOutputStream dos = new DataOutputStream(bos);

        PrevalentSystem ps = (PrevalentSystem)object;

        // bla bla bla
        // if object == PrevalentSystem || SimpleModel

        try {
//            writeObject(dos, oos, object);
//        } catch (IllegalAccessException e) {
//            throw new IOException(e);
        } finally {
            dos.close();
            bos.close();
        }
    }

    public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(stream);
        PrevalentSystem result = null;

        try {
//        } catch (InstantiationException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            dis.close();
        }

        return result;
    }

}
