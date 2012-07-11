package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import nl.astraeus.persistence.serializer.ArrayLongSerializer;
import nl.astraeus.persistence.serializer.ObjectSerializer;
import nl.astraeus.persistence.serializer.StringSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:07 PM
 */
public class SimpleJournalSerializer implements Serializer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleJournalSerializer.class);

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
    private final static byte TYPE_COLLECTION       = 11;
    private final static byte TYPE_SP_OBJECT        = 12;
    private final static byte TYPE_SP_COLLECTION    = 13;

    private final static byte TYPE_ARRAY_BOOLEAN    = 32;
    private final static byte TYPE_ARRAY_BYTE       = 33;
    private final static byte TYPE_ARRAY_CHAR       = 34;
    private final static byte TYPE_ARRAY_SHORT      = 35;
    private final static byte TYPE_ARRAY_INT        = 36;
    private final static byte TYPE_ARRAY_LONG       = 37;
    private final static byte TYPE_ARRAY_FLOAT      = 38;
    private final static byte TYPE_ARRAY_DOUBLE     = 39;
    private final static byte TYPE_ARRAY_STRING     = 40;
    private final static byte TYPE_ARRAY_OBJECT     = 41;

    private static Map<Byte, ObjectSerializer> serializers = new HashMap<Byte, ObjectSerializer>();
    private static Map<Class<?>, Byte> typeMapping = new HashMap<Class<?>, Byte>();

    static {
        typeMapping.put(boolean.class,      TYPE_BOOLEAN);
        typeMapping.put(Boolean.class,      TYPE_BOOLEAN);
        typeMapping.put(byte.class,         TYPE_BYTE);
        typeMapping.put(Byte.class,         TYPE_BYTE);
        typeMapping.put(char.class,         TYPE_CHAR);
        typeMapping.put(short.class,        TYPE_SHORT);
        typeMapping.put(Short.class,        TYPE_SHORT);
        typeMapping.put(int.class,          TYPE_INT);
        typeMapping.put(Integer.class,      TYPE_INT);
        typeMapping.put(long.class,         TYPE_LONG);
        typeMapping.put(Long.class,         TYPE_LONG);
        typeMapping.put(float.class,        TYPE_FLOAT);
        typeMapping.put(Float.class,        TYPE_FLOAT);
        typeMapping.put(double.class,       TYPE_DOUBLE);
        typeMapping.put(Double.class,       TYPE_DOUBLE);

        typeMapping.put(String.class,       TYPE_STRING);

        typeMapping.put(boolean[].class,    TYPE_ARRAY_BOOLEAN);
        typeMapping.put(Boolean[].class,    TYPE_ARRAY_BOOLEAN);
        typeMapping.put(byte[].class,       TYPE_ARRAY_BYTE);
        typeMapping.put(Byte[].class,       TYPE_ARRAY_BYTE);
        typeMapping.put(char[].class,       TYPE_ARRAY_CHAR);
        typeMapping.put(short[].class,      TYPE_ARRAY_SHORT);
        typeMapping.put(Short[].class,      TYPE_ARRAY_SHORT);
        typeMapping.put(int[].class,        TYPE_ARRAY_INT);
        typeMapping.put(Integer[].class,    TYPE_ARRAY_INT);
        typeMapping.put(long[].class,       TYPE_ARRAY_LONG);
        typeMapping.put(Long[].class,       TYPE_ARRAY_LONG);
        typeMapping.put(float[].class,      TYPE_ARRAY_FLOAT);
        typeMapping.put(Float[].class,      TYPE_ARRAY_FLOAT);
        typeMapping.put(double[].class,     TYPE_ARRAY_DOUBLE);
        typeMapping.put(Double[].class,     TYPE_ARRAY_DOUBLE);

        serializers.put(TYPE_STRING,        new StringSerializer());
        serializers.put(TYPE_ARRAY_LONG,    new ArrayLongSerializer());

    }

    public void writeObject(OutputStream stream, Object object) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(stream);
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            writeSingleObject(dos, object);
        } finally {
            dos.close();
            bos.close();
        }
    }

    @Override
    public Object readObject(InputStream inputStream) throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(inputStream);
        Object result = null;

        try {
            result = readSingleObject(dis);

            // result instanceof SimpleMdodel
            // -> references
            // -> indexes
        } finally {
            dis.close();
        }

        return result;
    }

    public void writeSingleObject(DataOutputStream dos, Object object) throws IOException {
        StringSerializer stringSerializer = new StringSerializer();

        String name = object.getClass().getName();

        stringSerializer.write(dos, name);

        List<Field> fields = ReflectHelper.get().getFieldsFromClass(object.getClass());

        dos.writeInt(fields.size());

        try {
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                Object obj = field.get(object);

                if (obj != null) {
                    dos.writeInt(getHash(field.getName()));

                    byte type = getType(fieldType);

                    if (type == 0) {
                        throw new IllegalStateException("Don't know how to serialize: ["+field.getType()+"]");
                    }

                    ObjectSerializer serializer = serializers.get(type);

                    if (serializer == null) {
                        throw new IllegalStateException("No serializer found for type: ["+field.getType()+"]");
                    } else {
                        serializer.setSimpleJournalSerializer(this);
                        serializer.write(dos, obj);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object readSingleObject(DataInputStream dis) throws IOException, ClassNotFoundException {
        StringSerializer stringSerializer = new StringSerializer();
        Object result = null;

        try {
            String className = stringSerializer.read(dis);

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

                        byte type = dis.readByte();

                        System.out.println("Type: "+type);

                        ObjectSerializer ser = serializers.get(type);

                        Object value = ser.read(dis);

                        System.out.println("Field value: "+value);

                        field.set(result, value);
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            dis.close();
        }

        return result;
    }

    private byte getType(Class<?> cls) {
        Byte result = typeMapping.get(cls);

        if (result == null) {
            result = 0;
        }

        return result;
    }

    private int getHash(String value) {
        int result = 0;

        for (int index = 0; index < value.length(); index++) {
            result += value.charAt(index);
        }

        return result;
    }

}
