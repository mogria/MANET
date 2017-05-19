package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class Router implements Runnable {

    @FunctionalInterface
    public interface MessageHandler {
        void handleMessage(MulticastMessage message);
    }

    private FixedSizeList messageWindow;

    private ByteBuffer receiveBuffer;
    private byte[] messageBuffer;
    private DatagramChannel udpChannel;
    private Selector selector;
    private double retransmissionProbability;

    private List<MessageHandler> messageHandlers;

    private Logger logger = LogManager.getLogger(Router.class);

    private static final int RECEIVE_BUFFER_SIZE = 2000;

    public Router(final FixedSizeList messageWindow) {
        receiveBuffer = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE);
        messageBuffer = new byte[MulticastMessage.TELEGRAM_L];
        this.messageWindow = messageWindow;
        this.messageHandlers = new ArrayList<MessageHandler>();
    }

    public double getRetransmissionProbability() {
        return retransmissionProbability;
    }

    public void setRetransmissionProbability(double retransmissionProbability) {
        this.retransmissionProbability = retransmissionProbability;
    }

    public void addMessageHandler(MessageHandler messageHandler) {
        messageHandlers.add(messageHandler);
    }

    public void openChannel() throws IOException {
        udpChannel = DatagramChannel.open();
        final InetAddress multicastAddr = InetAddress.getByAddress(new byte[]{(byte)239, (byte)255, (byte)255, (byte)250});
        udpChannel.socket().bind(new InetSocketAddress(multicastAddr, 1337));
    }


    public void run() {
        try {
            openChannel();
            // cisco7039-0360
            selector = Selector.open();
            udpChannel.register(selector, SelectionKey.OP_READ);

        } catch (IOException ex) {
            logger.fatal("Could not fücking kreate the UDP fuckin socket dings");
            logger.fatal(ex);
            return;
        }

        while (true) {
            final Set<SelectionKey> readyChannels = selector.selectedKeys();

            final Iterator<SelectionKey> iter = readyChannels.iterator();
            while (iter.hasNext()) {
                 onSelecionKey(iter.next());
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                logger.warn("Router got interrupted");
                logger.warn(ex);
                return;
            }
        }
    }

    private void onSelecionKey(final SelectionKey key) {
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

    private void onRead(final SocketChannel channel) {
        try {
            channel.read(receiveBuffer);
            if (isFullMessageInBuffer()) {
                receiveBuffer.flip();
                receiveBuffer.put(messageBuffer);
                onMessage(messageBuffer);
                receiveBuffer.flip();
            }

        } catch (IOException ex) {
            logger.warn("Could not read from channel");
            logger.warn(ex);
        }
    }

    private void onMessage(final byte[] messageBytes) {
        final MulticastMessage message = new MulticastMessage(messageBytes);

        if(!messageWindow.contains(message)) {
            messageWindow.add(message);
            if(ThreadLocalRandom.current().nextDouble() < retransmissionProbability) {
                sendMessage(messageBytes);
            }
        }

        messageHandlers.forEach((messageHandler) -> {
            messageHandler.handleMessage(message);
        });
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
