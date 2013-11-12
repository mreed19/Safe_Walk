/**
 * Project 7
 * @author reed136
 * @author duffy10
 */

import java.util.*;
import edu.purdue.cs.cs180.channel.ChannelException;
import edu.purdue.cs.cs180.channel.MessageListener;
import edu.purdue.cs.cs180.channel.TCPChannel;
import edu.purdue.cs.cs180.channel.Channel;

public class Server implements MessageListener {
    
    private Channel channel;
    private ArrayList<Request> requesters = new ArrayList<Request>();
    private ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
    private String algorithm;
    private final int[][] timeValues = {{0, 8, 6, 5, 4}, {8, 0, 4, 2, 5}, {6, 4, 0, 3, 1},
        {5, 2, 3, 0, 7}, {4, 5, 1, 7, 0}};
    private HashMap<String, Integer> buildings = new HashMap<String, Integer>();
    
    public Server(Channel channel, String algorithm) {
        this.channel = channel;
        this.channel.setMessageListener(this);
        this.algorithm = algorithm;
        buildings.put("CL50", 0);
        buildings.put("EE", 1);
        buildings.put("LWSN", 2);
        buildings.put("PMU", 3);
        buildings.put("PUSH", 4);
    }
    
    class Request {
        private String location;
        private String urgency;
        private int id;
        
        Request(String location, String urgency, int id) {
            this.location = location;
            this.urgency = urgency;
            this.id = id;
        }
        
        public int getID() {
            return id;
        }
        
        public String getLocation() {
            return location;
        }
        
        public String getUrgency() {
            return urgency;
        }
    }
    
    class Volunteer {
        private String location;
        private int id;
        
        Volunteer(String location, int id) {
            this.location = location;
            this.id = id;
        }
        
        public int getID() {
            return id;
        }
        
        public String getLocation() {
            return location;
        }
    }
    
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            Server server = new Server(new TCPChannel(port), args[1]);
        }
        catch (NumberFormatException e) {
            System.out.println("Argument must be an integer");
            System.exit(0);
        }
    }
    
    @Override
    public void messageReceived(String message, int clientID) {
        if (algorithm.equals("FCFS")) {
            fcfs(message, clientID);
        }
        else if (algorithm.equals("CLOSEST")) {
            sendClosest(message, clientID);
        }
        else if (algorithm.equals("URGENCY")) {
            sendUrgency(message, clientID);
        }
    }
    
    public void fcfs(String message, int clientID) {
        String[] m = message.split(" ");
        if (message.charAt(0) == 'V') {
            volunteers.add(new Volunteer(m[1], clientID));
            if (!requesters.isEmpty()) {
                Request r = requesters.get(0);
                Volunteer v = volunteers.get(0);
                sendMessage(v, r);
                requesters.remove(0);
                volunteers.remove(0);
            }
        }
        else if (message.charAt(0) == 'R') {
            requesters.add(new Request(m[1], m[2], clientID));
            if (!volunteers.isEmpty()) {
                Request r = requesters.get(0);
                Volunteer v = volunteers.get(0);
                sendMessage(v, r);
                volunteers.remove(0);
                requesters.remove(0);
            }
        }
    }
    
    public void sendClosest(String message, int clientID) {
        String[] m = message.split(" ");
        if (message.charAt(0) == 'R') {
            requesters.add(new Request(m[1], m[2], clientID));
        }
        else if (message.charAt(0) == 'V') {
            volunteers.add(new Volunteer(m[1], clientID));
        }
        if ((volunteers.size() > 1) && (requesters.size() == 1)) {
            Volunteer chosen = null;
            Request r = requesters.get(0);
            String fixedLocation = r.getLocation();
            int minDistance = 10;
            int distance;
            for (Volunteer v : volunteers) {
                distance = timeValues[buildings.get(v.getLocation())][buildings.get(fixedLocation)];
                if (distance < minDistance) {
                    minDistance = distance;
                    chosen = v;
                }
            }
            sendMessage(chosen, r);
            volunteers.remove(chosen);
            requesters.remove(0);
        }
        else if ((requesters.size() > 1) && (volunteers.size() == 1)) {
            Request chosen = null;
            Volunteer v = volunteers.get(0);
            String fixedLocation = v.getLocation();
            int minDistance = 10;
            int distance;
            for (Request r : requesters) {
                distance = timeValues[buildings.get(r.getLocation())][buildings.get(fixedLocation)];
                if (distance < minDistance) {
                    minDistance = distance;
                    chosen = r;
                }
            }
            sendMessage(v, chosen);
            volunteers.remove(0);
            requesters.remove(chosen);
        }
        else if ((requesters.size() == 1) && (volunteers.size() == 1)) {
            sendMessage(volunteers.get(0), requesters.get(0));
            volunteers.remove(0);
            requesters.remove(0);
        }
    }
    
    public void sendUrgency(String message, int clientID) {
        String[] m = message.split(" ");
        if (message.charAt(0) == 'R') {
            requesters.add(new Request(m[1], m[2], clientID));
        }
        else if (message.charAt(0) == 'V') {
            volunteers.add(new Volunteer(m[1], clientID));
        }
        
        if ((volunteers.size() >= 1) && (requesters.size() == 1)) {
            sendMessage(volunteers.get(0), requesters.get(0));
            volunteers.remove(0);
            requesters.remove(0);
        }
        else if ((requesters.size() > 1) && (volunteers.size() == 1)) {
            Volunteer v = volunteers.get(0);
            ArrayList<Request> emergency = new ArrayList<Request>();
            ArrayList<Request> urgent = new ArrayList<Request>();
            ArrayList<Request> normal = new ArrayList<Request>();
            for (Request r : requesters) {
                if (r.getUrgency().equals("EMERGENCY")) {
                    emergency.add(r);
                }
                else if (r.getUrgency().equals("URGENT")) {
                    urgent.add(r);
                }
                else if (r.getUrgency().equals("NORMAL")) {
                    normal.add(r);
                }
            }
            
            if (!emergency.isEmpty()) {
                sendMessage(v, emergency.get(0));
                volunteers.remove(0);
                requesters.remove(emergency.get(0));
            }
            else if (!urgent.isEmpty()) {
                sendMessage(v, urgent.get(0));
                volunteers.remove(0);
                requesters.remove(urgent.get(0));
            }
            else if (!normal.isEmpty()) {
                sendMessage(v, normal.get(0));
                volunteers.remove(0);
                requesters.remove(normal.get(0));
            }
        }
    }
    
    
    public void sendMessage(Volunteer v, Request r) {
        try {
            channel.sendMessage("LOCATION " + r.getLocation() + " " + r.getUrgency()
                                    , v.getID());
            channel.sendMessage("VOLUNTEER " + v.getID() + " " + 
                                timeValues[buildings.get(r.getLocation())][buildings.get(v.getLocation())]
                                    , r.getID());
        } catch (ChannelException e) {
            e.printStackTrace();
        }
    }
}
