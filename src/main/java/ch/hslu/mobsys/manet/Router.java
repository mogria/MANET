package ch.hslu.mobsys.manet;

import com.sun.jmx.snmp.ThreadContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.CloseableThreadContext;
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
    private Sender sender;

    private ByteBuffer receiveBuffer;
    private byte[] messageBuffer;
    private DatagramChannel udpChannel;
    private MembershipKey membershipKey;
    private double retransmissionProbability;

    private List<MessageHandler> messageHandlers;

    private Logger logger = LogManager.getLogger(Router.class);

    private static final int RECEIVE_BUFFER_SIZE = 2000;

    private int messageCounter;

    public Router(final FixedSizeList messageWindow, final Sender sender) {
        receiveBuffer = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE);
        messageBuffer = new byte[MulticastMessage.TELEGRAM_L];
        this.messageWindow = messageWindow;
        this.messageHandlers = new ArrayList<MessageHandler>();
        this.sender = sender;
        messageCounter = 0;
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
        final InetAddress multicastAddr = InetAddress.getByAddress(new byte[]{(byte)127, (byte)0, (byte)0, (byte)1});
        final NetworkInterface ni = NetworkInterface.getByInetAddress(multicastAddr);
        final InetAddress multicastGroup =  InetAddress.getByAddress(new byte[]{(byte)239, (byte)255, (byte)255, (byte)250});
        udpChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(multicastAddr, 1337))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);

        udpChannel.configureBlocking(false);
        membershipKey = udpChannel.join(multicastGroup, ni);
    }


    public void run() {
        try {
            openChannel();
            // cisco7039-0360
        } catch (IOException ex) {
            logger.fatal("Could not fücking kreate the UDP fuckin socket dings");
            logger.fatal(ex);
            return;
        }

        while (true) {
            if (membershipKey.isValid()) {
                onRead(udpChannel);
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

    private boolean isFullMessageInBuffer() {
        return receiveBuffer.remaining() <= RECEIVE_BUFFER_SIZE - MulticastMessage.TELEGRAM_L;
    }

    private void onRead(final DatagramChannel channel) {
        logger.debug("read event occured");
        try {
            final SocketAddress networkParticipant = channel.receive(receiveBuffer);
            if(networkParticipant == null) return;

            // attach ip address / port to log4j messages
            try(final CloseableThreadContext.Instance ctc = CloseableThreadContext.put("socketaddress", networkParticipant.toString())) {
                ThreadContext.push("test", 5678);
                logger.info("Received data");
                if (isFullMessageInBuffer()) {
                    receiveBuffer.flip();
                    receiveBuffer.put(messageBuffer);
                    onMessage(messageBuffer);
                    receiveBuffer.flip();
                }
            }

        } catch (IOException ex) {
            logger.warn("Could not read from channel");
            logger.warn(ex);
        }
    }

    private void onMessage(final byte[] messageBytes) {
        logger.info("Received full message");
        final MulticastMessage message = new MulticastMessage(messageBytes);
        logger.info(message);
        messageCounter++;
        message.setCountReceived(messageCounter);
        if(!messageWindow.contains(message)) {
            logger.info("message was never seen before, temporarily save it");
            messageWindow.add(message);
            if(ThreadLocalRandom.current().nextDouble() < retransmissionProbability) {
                logger.info("retransmitting with probability of " + retransmissionProbability);
                sender.sendMessage(messageBytes);
                message.setRetransmitted(true);
            }
        } else {
            logger.info("not retransmitting, message has already been seen");
        }

        messageHandlers.forEach((messageHandler) -> {
            messageHandler.handleMessage(message);
        });
    }

}
