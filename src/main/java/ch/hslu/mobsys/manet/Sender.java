/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hslu.mobsys.manet;

import java.io.IOException;
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
    private final Logger logger = LogManager.getLogger(Sender.class);

    public Sender() {
        try {
            channel = DatagramChannel.open();
            channel.connect(NetUtil.getLoopbackInetSocketAddress());
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
        final ByteBuffer writeBuffer = ByteBuffer.allocate(MulticastMessage.TELEGRAM_L);
        writeBuffer.put(message);
        writeBuffer.flip();
        try {
            channel.write(writeBuffer);
            logger.info("Message was sent");
        } catch (IOException ex) {
            logger.warn(ex);
        }
    }
}
