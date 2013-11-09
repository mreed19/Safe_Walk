package edu.purdue.cs.cs180.channel;

/**
 * Message class used by the channel for a generic message type.
 * 
 * @author fmeawad
 * 
 */

class Message {
    /**
     * Message types (from the channel perspective only).
     */
    public enum Type {
        Register, // register a new client
        Added, // server response to a new client (message content should include an ID)
        NewMessage, // used to send a new message
        CheckMessage, // used to check if a message is waiting
        MessageReceived, // reply when a message was waiting
        NoMessage, // reply when there is no message waiting
        Acknowledge // acknowledge the receipt of a message
    };

    /**
     * store the type locally.
     */
    private Type type;

    /**
     * store the message value.
     */
    private String value;

    /**
     * store the client channel ID for this message.
     */
    private int channelID = -1;

    /**
     * Constructor used for received messages.
     * 
     * @param messageString
     *            The whole received message string including the type and not the value only.
     */
    public Message(String messageString) {
        String[] messageParts = messageString.split(":", 3);
        type = Type.valueOf(messageParts[0]);
        channelID = Integer.parseInt(messageParts[1]);
        value = messageParts[2];
    }

    /**
     * Constructor used for messages to be sent.
     * 
     * @param type
     * @param value
     * @param channelID
     */
    public Message(Type type, String value, int channelID) {
        this.type = type;
        this.value = value;
        this.channelID = channelID;
    }

    /**
     * Gets the message type.
     * 
     * @return the message {@link Type}.
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the message value.
     * 
     * @return the message value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts the message to a String.
     * 
     * @return a string representation of the message including the type.
     */
    public String toString() {
        return this.type.toString() + ":" + this.channelID + ":" + this.value;
    }

    /**
     * Gets the channel ID.
     * 
     * @return the channel the message was sent from.
     */
    public int getChannelID() {
        return this.channelID;

    }

    /**
     * Prints the message to the log (standard error).
     * 
     * @param isSend
     */
    public void logPrint(boolean isSend) {
        System.err.println((isSend ? "o$" : "i$") + this.channelID + "$" + this.value);
    }
}
