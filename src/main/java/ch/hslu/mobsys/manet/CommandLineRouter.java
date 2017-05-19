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
        final Router router = new Router(messageWindow);
        router.addMessageHandler(message -> {
            System.out.println(message.getUniqueIdentifier() + "|" + message.getIdentifier() + "|" + message.getMessage());

        });
        router.run();
    }
}
