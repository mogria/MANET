package ch.hslu.mobsys.manet;

/**
 * @author marco
 */
public class CommandLineMSN {

    public static void main(String[] args) {
        final Sender sender = new Sender();
        String payload = args[0];
        MulticastMessage message = new MulticastMessage();
        message.setMessage(payload);
        message.setCountReceived(0);
        message.setIdentifier("marco");
        message.setRetransmitted(false);
        sender.sendMessage(message);
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
