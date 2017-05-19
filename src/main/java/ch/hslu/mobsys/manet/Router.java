package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class Router {

    private ByteBuffer receiveBuffer;
    private byte[] messageBuffer;
    private Selector selector;

    private Logger logger = LogManager.getLogger(Router.class);

    private static final int RECEIVE_BUFFER_SIZE = 2000;

    public Router() throws IOException {
        final DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(1337)); // 239.255.255.250
        // cisco7039-0360
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        receiveBuffer = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE);
        messageBuffer = new byte[MulticastMessage.TELEGRAM_L];

    }

    public void run() {

        while (true) {
            final Set<SelectionKey> readyChannels = selector.selectedKeys();

            final Iterator<SelectionKey> iter = readyChannels.iterator();
            while (iter.hasNext()) {
                final SelectionKey key = iter.next();
                if (key.isValid()) {
                    if (key.isReadable()) {
                        final SocketChannel socketChannel = (SocketChannel)key.channel();
                        onRead(socketChannel);
                    } else if (key.isWritable()) {

                    }
                }
            }
        }
    }

    public void onRead(final SocketChannel channel) {
        try {
            channel.read(receiveBuffer);
            if (receiveBuffer.remaining() < RECEIVE_BUFFER_SIZE - MulticastMessage.TELEGRAM_L) {
                receiveBuffer.flip();
                receiveBuffer.put(messageBuffer);
                receiveBuffer.flip();
            }

        }catch (IOException ex) {
            logger.warn(ex);
        }
    }

    public void onMessage() {
        final MulticastMessage message = new MulticastMessage();
    }

    public void sendMessage(final MulticastMessage message) {
        sendMessage(message.getTelegram());
    }

    public void sendMessage(final byte[] message) {

    }




}
