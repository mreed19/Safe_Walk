package edu.purdue.cs.cs180.channel;

/**
 * A listener interface to allow clients and servers to be notified when a new message is received.
 * 
 * @author fmeawad
 * 
 */
public interface MessageListener {
    /**
     * Notifies listener of new messages.
     * 
     * @param message
     *            The message received.
     * @param clientID
     *            The id of the channel the message was received from.
     */
    public void messageReceived(String message, int clientID);
}
