package edu.purdue.cs.cs180.channel;

/**
 * Channel Exception, to report all exceptions related to the channel.
 * 
 * @author fmeawad
 * 
 */
public class ChannelException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param message
     *            The error message.
     */
    public ChannelException(String message) {
        super(message);
    }

    /**
     * Constructor for an upgraded exception.
     * 
     * @param message
     *            The error message.
     * @param ex
     *            The caught exception.
     */
    public ChannelException(String message, Exception ex) {
        super(message + "(" + ex.getMessage() + ")", ex.getCause());
    }
}
