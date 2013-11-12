import java.util.*;

import edu.purdue.cs.cs180.channel.ChannelException;
import edu.purdue.cs.cs180.channel.MessageListener;
import edu.purdue.cs.cs180.channel.TCPChannel;

public class Requester implements MessageListener {

    private Scanner sc = new Scanner(System.in);
    private int input = 0;
    private String string;
    private TCPChannel channel;
 
    public Requester (TCPChannel channel) {
        this.channel = channel;
        this.channel.setMessageListener(this);
        sendMessage();
    }
    @Override
    public void messageReceived(String message, int clientID) {
        System.out.println("Volunteer " + message.substring(10) + " assigned and will arrive shortly.");
        sendMessage();
    }
 
    public void sendMessage () {  
        while (true) {
            try {
                System.out.println("1. CL50 - Class of 1950 Lecture Hall\n" +
                         "2. EE - Electrical Engineering Building\n" +
                         "3. LWSN - Lawson Computer Science Building\n" +
                         "4. PMU - Purdue Memorial Union\n" +
                         "5. PUSH - Purdue University Student Health Center");
                System.out.print("Enter your location (1-5): ");
                string = sc.nextLine();
                input = Integer.parseInt(string);
                if (input == 1 || input == 2 || input == 3 || input == 4 || input == 5) {
                    break;
                }
                System.out.println("Invalid input. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
                input = 0;
                continue;
            } 
        }
    	try {
    		switch (input) {
    			case 1:
    				channel.sendMessage("REQUEST CL50");
    				break;
    			case 2:
    				channel.sendMessage("REQUEST EE");
    				break;
    			case 3:
    				channel.sendMessage("REQUEST LWSN");
    				break;
    			case 4:
    				channel.sendMessage("REQUEST PMU");
    				break;
    			case 5:
    				channel.sendMessage("REQUEST PUSH");
    				break;
    		}
    	} catch (ChannelException e) {
    		e.printStackTrace();
    	}
    	System.out.println("Waiting for volunteer...");
    }
 
    public static void main (String[] args) {
    	Requester requester = null;
    	try {
    		requester = new Requester(new TCPChannel(args[0], Integer.parseInt(args[1])));
    	}
    	catch (NumberFormatException e) {
    		System.out.println("Arguments must be a string and then an integer.");
    		System.exit(0);
    	}
    	catch (ChannelException e) {
    		e.printStackTrace();
    	}
    }
}
