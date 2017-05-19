package ch.hslu.mobsys.manet;

import java.util.ArrayList;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class CommandLineRouter {
    public static void main(final String[] args) {
        final FixedSizeList messageWindow = new FixedSizeList(new ArrayList());
        Sender sender = new Sender();
        final Router router = new Router(messageWindow, sender);
        router.addMessageHandler(message -> {
            System.out.println(message.getUniqueIdentifier() + "|" + message.getIdentifier() + "|" + message.getMessage());

        });
        router.run();
    }
}
