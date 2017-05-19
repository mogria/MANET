package ch.hslu.mobsys.manet;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class MessageBuilder {
    private byte[] identifier;

    public MessageBuilder(byte identifier[]) {
        this.identifier = identifier;
    }

    public Message buildMessage(final String messageContents) {
        final Message message = new Message();
        message.setUid(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
        message.setIdentifier(identifier);
        message.setMessage(messageContents);
        return message;
    }
}
