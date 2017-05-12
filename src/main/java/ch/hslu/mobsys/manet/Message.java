package ch.hslu.mobsys.manet;

import java.util.Arrays;

public class Message {

    private int uid;
    private byte[] identifier;
    private String message;

    public int getUid() {
        return uid;
    }

    public void setUid(final int uid) {
        this.uid = uid;
    }

    public byte[] getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final byte[] identifier) {
        this.identifier = identifier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public boolean equals(final Message other) {
        if(!(other instanceof Message)) {
            return false;
        }
        
        return uid == this.getUid() && Arrays.equals(identifier, other.identifier);
    }


}
