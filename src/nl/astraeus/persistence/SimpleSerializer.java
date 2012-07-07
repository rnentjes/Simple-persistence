package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import org.prevayler.foundation.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:07 PM
 */
public class SimpleSerializer implements Serializer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleSerializer.class);

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
                        } else if (field.getType().isAssignableFrom(SimpleModel.class)) {
                            long id = dis.readLong();
                            System.out.println("SimpleModel id: "+id);
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

}
