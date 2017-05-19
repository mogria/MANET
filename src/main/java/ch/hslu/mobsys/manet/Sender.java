/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class Sender {

    private DatagramChannel channel;
    private Logger logger = LogManager.getLogger(Sender.class);

    public Sender() {
        try {
            channel = DatagramChannel.open();
            final InetAddress multicastAddr = InetAddress.getByAddress(new byte[]{(byte)239, (byte)255, (byte)255, (byte)250});
            channel.connect(new InetSocketAddress(multicastAddr, 1337));
        } catch (IOException ex) {
            logger.fatal("Could not fücking kreate the UDP fuckin socket dings");
            logger.fatal(ex);
            return;
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
            channel.write(writeBuffer);
        } catch (IOException ex) {
            logger.warn(ex);
        }
    }
}
