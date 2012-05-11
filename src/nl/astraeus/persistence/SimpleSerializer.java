package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import org.prevayler.foundation.serialization.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:07 PM
 */
public class SimpleSerializer implements Serializer {

    private Charset charset = Charset.forName("UTF-8");

    public void writeObject(OutputStream stream, Object object) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);

        try {
            writeObject(dos, object);
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } finally {
            dos.close();
        }
    }

    public void writeObject(DataOutputStream stream, Object object) throws IOException, IllegalAccessException {
        DataOutputStream dos = new DataOutputStream(stream);

        String name = object.getClass().getName();

        dos.writeBytes(name);
        dos.writeBytes(": {\n");

        List<Field> fields = ReflectHelper.get().getFieldsFromClass(object.getClass());

        for (Field field : fields) {
            Object obj = field.get(object);

            dos.writeBytes(field.getName());
            dos.writeBytes(":");

            if (obj != null) {
                if (obj instanceof SimpleList) {
                    SimpleList list = (SimpleList)obj;
                    dos.writeBytes("List: ");
                    for (Object id : list.getIdList()) {
                        dos.writeBytes(String.valueOf(id));
                        dos.writeBytes(",");
                    }
                } else if (obj instanceof SimpleReference) {
                    SimpleReference ref = (SimpleReference)obj;
                    dos.writeBytes("Ref: "+ref.getId());
                } else if (obj instanceof Long[]) {
                    for (Long l : (Long[])obj) {
                        dos.writeBytes(Long.toString(l));
                        dos.writeBytes(",");
                    }
                } else if (obj instanceof long[]) {
                    for (Long l : (long[])obj) {
                        dos.writeBytes(Long.toString(l));
                        dos.writeBytes(",");
                    }
                } else {
                    dos.writeBytes(String.valueOf(obj));
                }
            }
            dos.writeBytes("\n");

        }

        dos.writeBytes("}\n");
    }

    public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
        return new Object();
    }
}
