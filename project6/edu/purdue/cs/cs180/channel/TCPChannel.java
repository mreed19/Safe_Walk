package edu.purdue.cs.cs180.channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A TCP implementation of the channel. It depends on having a single server and multiple clients. The Server is always
 * listening for messages (connections). Clients, once initialized, register at the server. A client can send a message
 * to the server immediately. while servers have to wait until the client checks the server (mailbox) for new
 * messages. Clients cannot communicate with each other, instead they have to do it through the server.
 * 
 * @author fmeawad
 * 
 */
public class TCPChannel extends Channel implements Runnable {
    /**
     * The port the channel uses.
     */
    private int port = 0;

    /**
     * The server address, used only by clients.
     */
    private String server = null;

    /**
     * The server socket.
     */
    private ServerSocket serverSocket = null;

    /**
     * The client timer.
     */
    private Timer clientTimer = null;

    /**
     * Constructor used by the server.
     * 
     * @param port
     */
    public TCPChannel(int port) {
        super();
        this.port = port;
        new Thread(this).start();
    }

    /**
     * Constructor used by the clients.
     * 
     * @param port
     * @throws FailedToCreateChannelException
     */
    public TCPChannel(String server, int port) throws ChannelException {
        super();
        assert (server != null);
        this.port = port;
        this.server = server;
        this.sendMessage(new Message(Message.Type.Register, "", -1));
        while (this.channelID == -1)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new ChannelException("Error while waiting for the new channelID", e);
            }
        clientTimer = new Timer();
        clientTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendMessage(new Message(Message.Type.CheckMessage, "", channelID));
                } catch (ChannelException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);
    }

    @Override
    protected void sendMessage(Message message) throws ChannelException {
        // Left over from debugging...?
        // if (message.getType() == Type.NewMessage)
        //     message.logPrint(true);
        
        if (this.server != null) {
            try {
                Socket socket = new Socket(this.server, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(message.toString());
                out.flush();

                String input = in.readLine();

                socket.close();
                this.messageReceived(new Message(input));
            } catch (IOException e) {
                throw new ChannelException("Error sending message", e);
            }
        } else
            this.pendingMessages.get(message.getChannelID()).add(message);
    }

    @Override
    public void run() {
        assert (this.server == null);

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("Unable to open server port: " + e.getMessage());
            return;
        }
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String input = in.readLine();

                Message reply = this.messageReceived(new Message(input));
                out.println(reply.toString());
                out.flush();
                out.close();
                in.close();
            } catch (SocketException e) {
                if (e.getMessage().equalsIgnoreCase("Socket closed")) {
                    break;
                }
                System.err.println("Unable to accept connection 1: " + e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("Unable to accept connection 2: " + e.getMessage());
                break;
            } catch (ChannelException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the channel.
     *
     * @throws ChannelException
     */
    @Override
    public void close() throws ChannelException {
        if (this.server == null) {
            assert (serverSocket != null);
            // This is the server.
            try {
                if (!serverSocket.isClosed())
                    serverSocket.close();
            } catch (SocketException e) {
                // do nothing.
            } catch (IOException e) {
                throw new ChannelException("Unable to close the channel", e);
            }
        } else {
            // this a client.
            assert (clientTimer != null);
            clientTimer.cancel();
        }
    }
}
