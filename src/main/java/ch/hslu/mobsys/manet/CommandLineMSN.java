package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author marco
 */
public class CommandLineMSN {

    public static void main(String[] args) {
        final FixedSizeList messageWindow = new FixedSizeList(new ArrayList());
        final Router router = new Router(messageWindow);
        try {
            router.openChannel();
        } catch (IOException ex) {
            System.err.println("Failed to bind socket.");
        }
        String payload = args[0];
        MulticastMessage message = new MulticastMessage();
        message.setMessage(payload);
        message.setCountReceived(0);
        message.setIdentifier("marco");
        message.setRetransmitted(false);
        router.sendMessage(message);
    }

    /*
    private class udpSender implements Runnable {

        private DatagramChannel udpChannel;

        public udpSender(InetSocketAddress destination) {
            try {
                udpChannel = DatagramChannel.open();
                udpChannel.socket().bind(destination);
            } catch (IOException ex) {
                System.err.println("");
            }
        }

        public void run() {
            //asdf
        }
    }
     */
}
