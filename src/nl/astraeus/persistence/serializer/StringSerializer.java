package nl.astraeus.persistence.serializer;

import nl.astraeus.persistence.SimpleJournalSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: rnentjes
 * Date: 7/8/12
 * Time: 11:09 AM
 */
public class StringSerializer implements ObjectSerializer<String> {
    private static Charset charset = Charset.forName("UTF-8");

    @Override
    public void setSimpleJournalSerializer(SimpleJournalSerializer sjs) {}

    @Override
    public void write(DataOutputStream dos, String value) throws IOException {
        byte [] data = value.getBytes(charset);

        dos.writeInt(data.length);
        dos.write(data);
    }

    @Override
    public String read(DataInputStream dis) throws IOException {
            int size = dis.readInt();

            byte [] data = new byte[size];

            dis.read(data);

            return new String(data, charset);
    }

}
