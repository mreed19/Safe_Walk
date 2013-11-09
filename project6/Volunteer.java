import java.util.*;

import edu.purdue.cs.cs180.channel.ChannelException;
import edu.purdue.cs.cs180.channel.MessageListener;
import edu.purdue.cs.cs180.channel.TCPChannel;

public class Volunteer implements MessageListener {

	private Scanner sc = new Scanner(System.in);
	private TCPChannel channel;
 
	public Volunteer (TCPChannel channel) {
		this.channel = channel;
		this.channel.setMessageListener(this);
		sendMessage();
	}
	@Override
	public void messageReceived(String message, int clientID) {
		System.out.println("Proceed to " + message.substring(9));
		sendMessage();
	}
 
	public void sendMessage () {
		System.out.println("Press ENTER when ready:");
		sc.nextLine();
		try {
			channel.sendMessage("VOLUNTEER " + channel.getID());
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		System.out.println("Waiting for assignment...");
	}
 
	public static void main (String[] args) {
		Volunteer volunteer = null;
		try {
			volunteer = new Volunteer(new TCPChannel(args[0], Integer.parseInt(args[1])));
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
