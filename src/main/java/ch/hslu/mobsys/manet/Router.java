package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class Router {

    private FixedSizeList messageWindow;

    private ByteBuffer receiveBuffer;
    private byte[] messageBuffer;
    private DatagramChannel udpChannel;
    private Selector selector;

    private Logger logger = LogManager.getLogger(Router.class);

    private static final int RECEIVE_BUFFER_SIZE = 2000;

    public Router(final FixedSizeList messageWindow) {
        receiveBuffer = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE);
        messageBuffer = new byte[MulticastMessage.TELEGRAM_L];
        this.messageWindow = messageWindow;

    }

    public void run() throws IOException {
        udpChannel = DatagramChannel.open();
        final InetAddress multicastAddr = InetAddress.getByAddress(new byte[]{(byte)239, (byte)255, (byte)255, (byte)250});
        udpChannel.socket().bind(new InetSocketAddress(multicastAddr, 1337));
        // cisco7039-0360
        selector = Selector.open();
        udpChannel.register(selector, SelectionKey.OP_READ);

        while (true) {
            final Set<SelectionKey> readyChannels = selector.selectedKeys();

            final Iterator<SelectionKey> iter = readyChannels.iterator();
            while (iter.hasNext()) {
                 onSelecionKey(iter.next());
            }
        }
    }

    public void onSelecionKey(final SelectionKey key) {
        if (!key.isValid()) {
            return;
        }

        if (key.isReadable()) {
            final SocketChannel socketChannel = (SocketChannel) key.channel();
            onRead(socketChannel);
        }

    }

    private boolean isFullMessageInBuffer() {
        return receiveBuffer.remaining() < RECEIVE_BUFFER_SIZE - MulticastMessage.TELEGRAM_L;
    }

    public void onRead(final SocketChannel channel) {
        try {
            channel.read(receiveBuffer);
            if (isFullMessageInBuffer()) {
                receiveBuffer.flip();
                receiveBuffer.put(messageBuffer);
                onMessage(messageBuffer);
                receiveBuffer.flip();
            }

        } catch (IOException ex) {
            logger.warn(ex);
        }
    }

    public void onMessage(final byte[] messageBytes) {
        final MulticastMessage message = new MulticastMessage(messageBytes);

        if(!messageWindow.contains(message)) {
            messageWindow.add(message);
            sendMessage(messageBytes);
        }
    }

    public void sendMessage(final MulticastMessage message) {
        sendMessage(message.getTelegram());
    }

    public void sendMessage(final byte[] message) {
        ByteBuffer writeBuffer = ByteBuffer.allocate(MulticastMessage.TELEGRAM_L);
        writeBuffer.put(message);
        writeBuffer.flip();
        try {
            udpChannel.write(writeBuffer);
        } catch (IOException ex) {
            logger.warn(ex);
        }
    }
}
