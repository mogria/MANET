package main;

import javafx.beans.property.*;

/**
 * Multicast message
 * Uses Link Properties to be bound by GUIs
 */
public class MulticastMessage {

    //<editor-fold desc="Constants">
    public static final int UID_L = 4;
    public static final int IDENTIFIER_L = 10;
    public static final int MESSAGE_L = 160;
    public static final int TELEGRAM_L = UID_L+IDENTIFIER_L+MESSAGE_L;
    //</editor-fold>

    //<editor-fold desc="Fields">
    private IntegerProperty uId;
    private StringProperty identifier;
    private StringProperty message;
    private IntegerProperty countReceived;
    private BooleanProperty retransmitted;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    /**
     * Empty constructor to initialize default values
     */
    public MulticastMessage() {
        uId = new SimpleIntegerProperty(0);
        identifier = new SimpleStringProperty("");
        message = new SimpleStringProperty("");
        countReceived = new SimpleIntegerProperty(0);
        retransmitted = new SimpleBooleanProperty(false);
    }

    /**
     * Constructor to create message from byte[]
     * @param telegram the telegram to be converted
     */
    public MulticastMessage(byte[] telegram) {
        this();

        byte[] bUid = new byte[UID_L];
        byte[] bIdentifier = new byte[IDENTIFIER_L];
        byte[] bMessage = new byte[MESSAGE_L];

        System.arraycopy(telegram, 0, bUid, 0, UID_L);
        System.arraycopy(telegram, UID_L, bIdentifier, 0, IDENTIFIER_L);
        System.arraycopy(telegram, UID_L + IDENTIFIER_L, bMessage, 0, MESSAGE_L);

        // Get int of byte[] representation
        setUId(byteToInt(bUid));
        setIdentifier((new String(bIdentifier).trim()));
        setMessage((new String(bMessage)).trim());
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    /**
     * Convert int to its byte[] representation
     * @param value the value to be converted
     * @return int as byte[]
     */
    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    /**
     * Converts byte[] to its int representation
     * @param value the value to be converted
     * @return byte[] as int
     */
    private int byteToInt(byte[] value) {
        return (value[0]<<24)&0xff000000|
                (value[1]<<16)&0x00ff0000|
                (value[2]<< 8)&0x0000ff00|
                (value[3]<< 0)&0x000000ff;
    }

    //</editor-fold>

    //<editor-fold desc="Public Methods">
    /**
     * Generate byte[] Telegram of the message
     * @return the telegram
     */
    public byte[] getTelegram() {
        // Get byte[] representation of properties
        byte[] bTelegram = new byte[TELEGRAM_L];
        byte[] bUid = intToByteArray(getUId());
        byte[] bIdentifier = getIdentifier().getBytes();
        byte[] bMessage = getMessage().getBytes();

        // Concatenate result byte[] message
        System.arraycopy(bUid, 0, bTelegram, 0, bUid.length);
        System.arraycopy(bIdentifier, 0, bTelegram, UID_L, bIdentifier.length);
        System.arraycopy(bMessage, 0, bTelegram, UID_L + IDENTIFIER_L, bMessage.length);

        return bTelegram;
    }

    /**
     * Gets a unique identifier for the message
     * - UID + identifier
     * @return the unique identifier
     */
    public StringProperty getUniqueIdentifierProperty() {
        return new SimpleStringProperty(getUId() + "_" + getIdentifier());
    }
    //</editor-fold>

    //<editor-fold desc="Overrides">

    protected MulticastMessage clone() {
        return new MulticastMessage(this.getTelegram());
    }

    /**
     * Checks to message for equality using its unique identifier (uid + identifier)
     * @params obj the object to check with this instance
     * @returns boolean whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MulticastMessage) {
            MulticastMessage foreign = (MulticastMessage)obj;
            return getUniqueIdentifierProperty().getValue().equals(foreign.getUniqueIdentifierProperty().getValue());
        } else {
            return false;
        }
    }

    /**
     * Converts the message to a multiline string representation
     * @return readable string representation of the message
     */
    @Override
    public String toString() {
        StringBuilder builtString = new StringBuilder();
        builtString.append("Uid:\t\t");builtString.append(uId.toString());builtString.append("\n");
        builtString.append("Identifier:\t");builtString.append(identifier);builtString.append("\n");
        builtString.append("Message:\t");builtString.append(message);builtString.append("\n");
        return builtString.toString();
    }
    //</editor-fold>

    //<editor-fold desc="Getter / Setter">

    public String getUniqueIdentifier() {
        return getUniqueIdentifierProperty().get();
    }

    public boolean getRetransmitted() {
        return retransmitted.get();
    }

    public BooleanProperty retransmittedProperty() {
        return retransmitted;
    }

    public void setRetransmitted(boolean retransmitted) {
        this.retransmitted.set(retransmitted);
    }

    public int getCountReceived() {
        return countReceived.get();
    }

    public IntegerProperty countReceivedProperty() {
        return countReceived;
    }

    public void setCountReceived(int countReceived) {
        this.countReceived.set(countReceived);
    }

    public String getMessage() {
        // Limit size

        String returnMessage = message.get();
        if(returnMessage != null && returnMessage.length() > MESSAGE_L) {
            returnMessage = returnMessage.substring(0, MESSAGE_L);
        }
        return returnMessage;
    }

    public StringProperty messageProperty() {
        return message;
    }
    public void setMessage(String message) {
        this.message.set(message);
    }

    public int getUId() {
        return this.uId.get();
    }

    public IntegerProperty uIDProperty() {
        return uId;
    }

    public void setUId(int uId) {
        this.uId.set(uId);
    }

    public String getIdentifier() {

        // Limit size
        String resultIdentifier = identifier.get();
        if(resultIdentifier != null && resultIdentifier.length() > IDENTIFIER_L) {
            resultIdentifier = resultIdentifier.substring(0, IDENTIFIER_L);
        }

        return resultIdentifier;
    }

    public StringProperty identifierProperty() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }
    //</editor-fold>
}
