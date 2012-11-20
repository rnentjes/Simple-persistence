package nl.astraeus.persistence.serializer;

import nl.astraeus.persistence.Persistent;
import nl.astraeus.persistence.PersistentManager;
import nl.astraeus.persistence.SimpleJournalSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * User: rnentjes
 * Date: 7/8/12
 * Time: 11:51 AM
 */
public class CollectionSerializer implements ObjectSerializer<Collection> {

    private SimpleJournalSerializer sjs = null;
    @Override
    public void setSimpleJournalSerializer(SimpleJournalSerializer sjs) {
        this.sjs = sjs;
    }

    @Override
    public void write(DataOutputStream dos, Collection value) throws IOException {
        StringSerializer stringSerializer = new StringSerializer();

        Class cls = null;

        for (Object o : value) {
            if (cls == null) {
                cls = o.getClass();
            } else if (!cls.equals(o.getClass())) {
                throw new IllegalStateException("Collections of mixed type are not supported, found "+cls.getCanonicalName()+" and "+o.getClass().getCanonicalName());
            }
        }

        boolean sm = Persistent.class.isAssignableFrom(cls);

        stringSerializer.write(dos, value.getClass().getCanonicalName());
        dos.writeInt(value.size());

        if (!value.isEmpty() && cls != null) {
            dos.writeBoolean(sm);

            if (sm) {
                stringSerializer.write(dos, cls.getCanonicalName());
            }

            for (Object o : value) {
                if (sm) {
                    //dos.writeLong(((Persistent)o).getId());
                } else {
                    sjs.writeSingleObject(dos, o);
                }
            }
        }
    }

    @Override
    public Collection read(DataInputStream dis) throws IOException {
        Collection result = null;

        StringSerializer stringSerializer = new StringSerializer();

        String canonicalClass = stringSerializer.read(dis);
        int size = dis.readInt();
        Class cls = null;

        try {
            result = (Collection)Class.forName(canonicalClass).newInstance();

            if (size > 0) {
                boolean sm = dis.readBoolean();

                if (sm) {
                    String clsName = stringSerializer.read(dis);
                    cls = Class.forName(clsName);

                    while(size-- > 0) {
                        long id = dis.readLong();

                        Object o = PersistentManager.get().find(cls, id);

                        result.add(o);
                    }
                } else {
                    while (size-- > 0) {
                        Object o = sjs.readSingleObject(dis);

                        result.add(o);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }
}
