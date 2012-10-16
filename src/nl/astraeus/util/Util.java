package nl.astraeus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 12:28 PM
 */
public class Util {
    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    // utility functions
    public static String formatNano(long l) {
        NumberFormat format = new DecimalFormat("###,##0.000");

        return format.format((double) l / 1000000.0);
    }

    public static void printMemoryUsage() {
        logger.info("Used  memory: "+((Runtime.getRuntime().totalMemory() / (1024*1024))-(Runtime.getRuntime().freeMemory() / (1024*1024))));
        logger.info("Free  memory: "+(Runtime.getRuntime().freeMemory() / (1024*1024)));
        logger.info("Total memory: "+(Runtime.getRuntime().totalMemory() / (1024*1024)));
        logger.info("Max   memory: "+(Runtime.getRuntime().maxMemory() / (1024*1024)));
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte [] buffer = new byte[8196];
        int length = 0;

        while((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

}
