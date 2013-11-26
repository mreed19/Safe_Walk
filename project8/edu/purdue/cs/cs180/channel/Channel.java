package edu.purdue.cs.cs180.channel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The abstract channel class. Defines the listener for the channel.
 * 
 * @author fmeawad
 * 
 */
public abstract class Channel {
    /**
     * Message listener of this channel.
     */
    private MessageListener messageListener = null;

    /**
     * Use to generate the channel IDs
     */
    private int seqGenerator = 0;

    /**
     * A pending messages list.
     */
    protected HashMap<Integer, ArrayList<Message>> pendingMessages = new HashMap<Integer, ArrayList<Message>>();

    /**
     * The channel ID. Used only by the clients.
     */
    protected int channelID = -1;

    /**
     * Gets the channel ID of this channel.
     * 
     * @return the channel ID (a value >= 0 if this is a client channel, -1 for a server channel).
     */
    public int getID() {
        return channelID;
    }

    /**
     * Sending the message through the actual channel implementation.
     * 
     * @param message
     * @throws ChannelException
     */
    protected abstract void sendMessage(Message message) throws ChannelException;

    /**
     * Sends a message from a client to the server.
     * 
     * @param message
     *            to be sent.
     * @throws ChannelException
     */
    public void sendMessage(String message) throws ChannelException {
        assert (message != null);
        sendMessage(new Message(Message.Type.NewMessage, message, this.channelID));
    }

    /**
     * Set the message listener for this channel.
     * 
     * @param messageListener
     */
    public void setMessageListener(MessageListener messageListener) {
        assert (messageListener != null);
        this.messageListener = messageListener;
    }

    /**
     * Sends a message from a server to the client.
     * 
     * @param message
     *            to be sent.
     * @throws ChannelException
     */
    public void sendMessage(String message, int clientID) throws ChannelException {
        assert (message != null);
        assert (clientID >= 0);
        sendMessage(new Message(Message.Type.NewMessage, message, clientID));
    }

    /**
     * Returns a pending message, if available; otherwise, return a Message of {@link Message.Type} NoMessage.
     * 
     * @param message
     * @return Pending message or no Message.
     */
    private Message checkMessageReceived(Message message) {
        assert (message != null);
        ArrayList<Message> messageList = pendingMessages.get(message.getChannelID());
        // each channel must have a channel list, even if the list is empty
        assert (messageList != null);
        if (messageList.size() > 0)
            return messageList.remove(0);

        return new Message(Message.Type.NoMessage, "", this.channelID);
    }

    /**
     * Invokes the listener when a message is received.
     * 
     * @param message
     * @return A reply message if applicable, null otherwise.
     * @throws InvalidMessageReceived
     */
    protected Message messageReceived(Message message) throws ChannelException {
        assert (message != null);
        switch (message.getType()) {
        case Added:
            // store the channel ID.
            this.channelID = Integer.parseInt(message.getValue());
            break;
        case CheckMessage:
            // check the queue for message, if there is return, otherwise send
            // NoMessage.
            return checkMessageReceived(message);
        case MessageReceived:
            // invoke the messageReceived from the listener.
            this.messageListener.messageReceived(message.getValue(), message.getChannelID());
            break;
        case NewMessage:
            // Left over from debugging...?
            // message.logPrint(false);
            
            // Invoke the messageReceived from the listener and send an ack reply...
            this.messageListener.messageReceived(message.getValue(), message.getChannelID());
            return new Message(Message.Type.Acknowledge, "", -1);
        case NoMessage:
            // do nothing.
            break;
        case Register:
            // generate a new channel ID and return it.
            int newChannelID = seqGenerator++;
            pendingMessages.put(newChannelID, new ArrayList<Message>());
            return new Message(Message.Type.Added, "" + newChannelID, -1);
        case Acknowledge:
            // do nothing.
            break;
        default:
            throw new ChannelException("Invalid message type received: " + message.getType());
        }
        return null;
    }

    /**
     * Closes the channel.
     * 
     * @throws ChannelException
     */
    public abstract void close() throws ChannelException;
}
