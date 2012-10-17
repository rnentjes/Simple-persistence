package nl.astraeus.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 8:08 PM
 */
public class SimpleNodeManager {
    private final static Logger logger = LoggerFactory.getLogger(SimpleNodeManager.class);

    private SimpleNode current = null;
    private Set<SimpleNode> others = new CopyOnWriteArraySet<SimpleNode>();

    private static SimpleNodeManager instance = new SimpleNodeManager();

    public static SimpleNodeManager get() {
        return instance;
    }

    public void init(String ip, int port, int divider, int remainder) {
        try {
            current = new SimpleNode(ip, port, divider, remainder);

            logger.info("init ip:{}, port:{}, divider:{}, remainder:{}", new Object [] {ip, port, divider, remainder});

            // start socket listener

            // start discovery thread
        } catch (UnknownHostException e) {
            current = null;

            throw new IllegalStateException("Unable to start up system, unknown ip-address "+ip);
        }
    }

    public SimpleNode getCurrent() {
        return current;
    }

    public void cast(List<SimpleTransaction.Action> actions) {
        for (SimpleNode node : others) {
            cast(node, actions);
        }
    }

    private void cast(SimpleNode node, List<SimpleTransaction.Action> actions) {
        logger.info("Casting transaction to {}:{}", node.getHost().getHostAddress(), node.getPort());
    }
}
