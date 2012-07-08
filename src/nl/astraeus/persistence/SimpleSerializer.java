package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import nl.astraeus.persistence.serializer.ObjectSerializer;
import nl.astraeus.persistence.serializer.StringSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:07 PM
 */
public class SimpleSerializer implements Serializer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleSerializer.class);

    private final static byte TYPE_BOOLEAN          =  1;
    private final static byte TYPE_BYTE             =  2;
    private final static byte TYPE_CHAR             =  3;
    private final static byte TYPE_SHORT            =  4;
    private final static byte TYPE_INT              =  5;
    private final static byte TYPE_LONG             =  6;
    private final static byte TYPE_FLOAT            =  7;
    private final static byte TYPE_DOUBLE           =  8;
    private final static byte TYPE_STRING           =  9;
    private final static byte TYPE_OBJECT           = 10;
    private final static byte TYPE_SP_OBJECT        = 11;
    private final static byte TYPE_SP_COLLECTION    = 12;

    private static Map<Byte, ObjectSerializer> serializers = new HashMap<Byte, ObjectSerializer>();

    static {
        serializers.put(TYPE_STRING, new StringSerializer());
    }

    private Charset charset = Charset.forName("UTF-8");
    private byte [] endline = "\n".getBytes(charset);

    public void writeObject(OutputStream stream, Object object) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(stream);
        DataOutputStream dos = new DataOutputStream(bos);
        ObjectOutputStream oos = new ObjectOutputStream(dos);

        // if object == PrevalentSystem || SimpleModel

        try {
            writeObject(dos, oos, object);
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } finally {
            oos.close();
            dos.close();
            bos.close();
        }
    }

    private int getHash(String value) {
        int result = 0;

        for (int index = 0; index < value.length(); index++) {
            result += value.charAt(index);
        }

        return result;
    }

    public void writeObject(DataOutputStream dos, ObjectOutputStream oos, Object object) throws IOException, IllegalAccessException {
        String name = object.getClass().getName();

        dos.write(name.getBytes(charset));
        dos.write(endline);

        List<Field> fields = ReflectHelper.get().getFieldsFromClass(object.getClass());

        dos.writeInt(fields.size());

        for (Field field : fields) {
            Class<?> type = field.getType();
            Object obj = field.get(object);

            if (obj != null) {
                dos.writeInt(getHash(field.getName()));

                if (obj instanceof SimpleModel) {
                    dos.writeLong(((SimpleModel)obj).getId());
                } else if (obj instanceof Collection) {
                    // if all elements are instanceof SimpleModel
                    // -> just serialize id's
                    Collection collection = (Collection)obj;
                    boolean sm = true;

                    for (Object o : collection) {
                        if (!(o instanceof SimpleModel)) {
                            sm = false;
                        }
                    }

                    dos.writeInt(collection.size());
                    dos.write(collection.getClass().getName().getBytes(charset));
                    dos.write(endline);

                    if (!collection.isEmpty()) {
                        dos.writeBoolean(sm);

                        for (Object o : collection) {
                            if (sm) {
                                dos.writeLong(((SimpleModel)o).getId());
                            } else {
                                oos.writeObject(o);
                            }
                        }
                    }
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
                    //writeObject(dos, obj);
                }
            }
        }

        dos.write(endline);
        dos.write(endline);
    }

    public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(stream);
        ObjectInputStream ois = new ObjectInputStream(dis);
        Object result = null;

        try {
            String className = readLine(dis);

            System.out.println("ClassName: " + className);
            Class<?> cls = Class.forName(className);
            result = cls.newInstance();

            int fieldCount = dis.readInt();
            System.out.println("Number of fields: " + fieldCount);

            for (int index = 0; index < fieldCount; index++) {
                System.out.println("Reading field: " + index);

                int fieldHash = dis.readInt();
                System.out.println("Hash: "+fieldHash);

                List<Field> fields = ReflectHelper.get().getFieldsFromClass(cls);
                for (Field field : fields) {
                    if (getHash(field.getName()) == fieldHash) {
                        System.out.println("Found field: "+field.getName()+" -> "+field.getType());

                        if (Collection.class.isAssignableFrom(field.getType())) {
                            int size = dis.readInt();
                            String collectionClassName = readLine(dis);
                            System.out.println("Collection type: "+collectionClassName);

                            System.out.println("Collection size: "+size);
                            if (size > 0) {
                                boolean simpleModelCollection = dis.readBoolean();

                                System.out.println("SimpleModelCollection: "+simpleModelCollection);
                                while(size-- > 0) {
                                    if (simpleModelCollection) {
                                        long id = dis.readLong();
                                        System.out.println("Id: "+id);
                                    } else {
                                        Object object = ois.readObject();
                                        System.out.println("Object: "+object);
                                    }
                                }
                            }
                        } else if (SimpleModel.class.isAssignableFrom(field.getType())) {
                            long id = dis.readLong();
                            System.out.println("SimpleModel <> id: "+id);
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ois.close();
            dis.close();
        }

        return result;
    }

    // readline as ASCII, ending with '\n', '\n' is not returned
    public String readLine(DataInputStream dis) throws IOException {
        StringBuilder result = new StringBuilder();
        byte ch;

        while ((ch = dis.readByte()) != '\n') {
            result.append((char)(ch & 0xFF));
        }

        return result.toString();
    }

    public void writePrimitive(DataOutputStream out, Object value) throws IOException {

        if (value.getClass().equals(boolean.class) || value.getClass().equals(Boolean.class)) {

        } else if (value.getClass().equals(long.class)) {
            out.writeByte(TYPE_LONG);
            out.writeLong((Long)value);
        }

    }

    public void writeString(DataOutputStream out, String value) throws IOException {
        byte [] data = value.getBytes(charset);

        out.writeInt(data.length);
        out.write(data);
    }

    public String readString(DataInputStream in) throws IOException {
        int size = in.readInt();

        byte [] data = new byte[size];

        in.read(data);

        return new String(data, charset);
    }

    ThreadLocal<ByteArrayOutputStream> outputBuffer = new ThreadLocal<ByteArrayOutputStream>() {
        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream();
        }
    };

    public void writeSingleObject(DataOutputStream dos, Object object) throws IOException {
        ByteArrayOutputStream baos = outputBuffer.get();

        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(object);

        byte [] data = baos.toByteArray();

        dos.writeInt(data.length);
        dos.write(data);

        baos.reset();
    }

    public Object readSingleObject(DataInputStream dis) throws IOException, ClassNotFoundException {
        int size = dis.readInt();
        byte [] data = new byte[size];

        int length = dis.read(data);

        assert length == data.length;

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object result = ois.readObject();

        ois.close();
        bais.close();

        return result;
    }

}
