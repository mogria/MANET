package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class Router {

    public Router() throws IOException {
        final DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(9999));
    }


}
