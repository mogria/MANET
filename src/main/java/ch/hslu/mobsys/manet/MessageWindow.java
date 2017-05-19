package ch.hslu.mobsys.manet;

import java.util.ArrayList;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public class MessageWindow {

    private ArrayList<Message> slidingWindow = new ArrayList<Message>();
    private int currentIndex = 0;
    private int size;

    public MessageWindow(int size) {
        this.size = size;
    }

    public boolean wasMessageSeen(final Message message) {
        return slidingWindow.contains(message);
    }

    public void slideWindow(final Message message) {
        slidingWindow.add(currentIndex, message);
        currentIndex = (currentIndex + 1) % size;
    }
}
