package nl.astraeus.persistence;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 8:07 PM
 */
public class SimpleNode {

    private InetAddress host;
    private int port;
    private int divider;
    private int remainder;

    public SimpleNode(String ip, int port, int divider, int remainder) throws UnknownHostException {
        this.host = InetAddress.getByName(ip);
        this.port = port;
        this.divider = divider;
        this.remainder = remainder;
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getDivider() {
        return divider;
    }

    public int getRemainder() {
        return remainder;
    }

    public boolean matched(long id) {
        return id % divider == remainder;
    }
}
