package nl.astraeus.persistence.serializer;

import nl.astraeus.persistence.SimpleJournalSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * User: rnentjes
 * Date: 7/8/12
 * Time: 11:07 AM
 */
public interface ObjectSerializer<T> {

    public void setSimpleJournalSerializer(SimpleJournalSerializer sjs);

    public void write(DataOutputStream dos, T value) throws IOException;

    public T read(DataInputStream dis) throws IOException;

}
