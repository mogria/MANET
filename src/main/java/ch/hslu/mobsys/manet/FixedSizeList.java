package main;

import com.sun.javafx.collections.ObservableListWrapper;

import java.util.List;

public class FixedSizeList extends ObservableListWrapper<MulticastMessage> {

    //<editor-fold desc="Constants">
    protected static final int DEFAULT_SIZE = 10;
    //</editor-fold>

    //<editor-fold desc="Fields">
    private int size;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public FixedSizeList(List<MulticastMessage> list, int size) {
        super(list);
        this.size = size;
    }
    public FixedSizeList(List<MulticastMessage> list) {
        this(list, DEFAULT_SIZE);
    }
    //</editor-fold>

    //<editor-fold desc="Overrides">
    /**
     * Adds a message to the list. If the message is already in list it's CountReceived is increased.
     * If there are is the maximum number of items in the list the oldest is removed.
     * @param message
     * @return boolean whether adding element was successful or not
     */
    @Override
    public boolean add(MulticastMessage message) {

        // Check whether message is already in list
        // Equals operator overridden
        int foundMessageIndex = this.indexOf(message);
        if(foundMessageIndex == -1) {

            // Check size and remove oldest item if needed
            if(this.size() == this.size) {
                this.remove(0);
            }

            return super.add(message);
        } else {

            // Update count if message already exists
            this.get(foundMessageIndex).setCountReceived(this.get(foundMessageIndex).getCountReceived()+1);

            return false;
        }
    }
    //</editor-fold>

}
