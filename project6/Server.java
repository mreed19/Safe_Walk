/**
 * Project 6
 * @author reed136
 * @author duffy10
 */

import java.util.*;
import edu.purdue.cs.cs180.channel.ChannelException;
import edu.purdue.cs.cs180.channel.MessageListener;
import edu.purdue.cs.cs180.channel.TCPChannel;

public class Server implements MessageListener {

	private TCPChannel channel;
	private ArrayList<String> requesterLocation = new ArrayList<String>();
	private ArrayList<Integer> requesterID = new ArrayList<Integer>();
	private ArrayList<Integer> volunteerID = new ArrayList<Integer>();
 
	public Server(TCPChannel channel) {
		this.channel = channel;
		this.channel.setMessageListener(this);
	}
 
	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			Server server = new Server(new TCPChannel(port));
		}
		catch (NumberFormatException e) {
			System.out.println("Argument must be an integer");
			System.exit(0);
		}
	}
 
	@Override
	public void messageReceived(String message, int clientID) {
		try {
			if (message.charAt(0) == 'V') {
				volunteerID.add(clientID);
				if (!(requesterLocation.isEmpty() && requesterID.isEmpty())) {
					channel.sendMessage("LOCATION " + requesterLocation.get(0), volunteerID.get(0));
					channel.sendMessage("VOLUNTEER " + volunteerID.get(0), requesterID.get(0));
					requesterLocation.remove(0);
					requesterID.remove(0);
					volunteerID.remove(0);
				}
			}
			else if (message.charAt(0) == 'R') {
				requesterLocation.add(message.substring(8));
				requesterID.add(clientID);
				if (!volunteerID.isEmpty()) {
					channel.sendMessage("LOCATION " + requesterLocation.get(0), volunteerID.get(0));
					channel.sendMessage("VOLUNTEER " + volunteerID.get(0), requesterID.get(0));
					requesterLocation.remove(0);
					requesterID.remove(0);
					volunteerID.remove(0);
				}
			}
		}
		catch (ChannelException e) {
			e.printStackTrace();
		}
	}
}
