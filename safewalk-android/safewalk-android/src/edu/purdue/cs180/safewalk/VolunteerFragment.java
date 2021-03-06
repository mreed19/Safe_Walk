package edu.purdue.cs180.safewalk;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * VolunteerFragment is the starting point for the Volunteer application.
 *
 * INSTRUCTIONS: Study RequesterFragment.java and fill in comparable code where TODO markers are.
 * 
 * @author reed136
 * @author duffy10
 */
@SuppressLint("DefaultLocale")
public class VolunteerFragment extends Fragment implements 
	ChannelListener, View.OnClickListener, Observer, OnItemSelectedListener {
    final static Model MODEL = new Model();
    final static AndroidChannel CHANNEL = MainActivity.initializeChannel();

    private Button button;
    private TextView status;
    private Spinner locations;

    /**
     * Creates the Volunteer user interface, and sets up the MODEL and CHANNEL to the server.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.volunteer, container, false);
        button = (Button) rootView.findViewById(R.id.volunteer_button);
        status = (TextView) rootView.findViewById(R.id.volunteer_status_textview);
        locations = (Spinner) rootView.findViewById(R.id.locations_spinner);

        // TODO: add this Fragment object to the MODEL observer and AndroidChannel listener...
        MODEL.addObserver(this);
        CHANNEL.setChannelListener(this);

        // TODO: set/restore the UI state from the MODEL...
        update(MODEL, null);

        // TODO: add listeners for the Volunteer button and spinner...
        button.setOnClickListener(this);
        locations.setOnItemSelectedListener(this);
        return rootView;
    }
    
    /**
     * Cleans up on exit by deleting the MODEL observer.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO: add code to delete this Observer from the MODEL
        MODEL.deleteObserver(this);
    }

    /**
     * Handles processing of "Press When Ready" button press.
     */
    @Override
    public void onClick(View v) {
        status.setText("Volunteer not implemented yet"); // TODO Delete this line (!)

        // TODO: disable controls until request assigned (or failure sent)...
        MODEL.setUIEnabled(false);
        MODEL.setStatus("Sending message to server; waiting for requester");
        MODEL.notifyObservers();

        // TODO: prepare message and send to server...
        String selectedItem = (String) locations.getSelectedItem();
        String[] words = selectedItem.split(" ");
        String message = "VOLUNTEER " + words[0];
        System.out.printf("Sending: %s\n", message);
        CHANNEL.sendMessage(message);
    }

    /**
     * Updates the view widgets whenever the MODEL changes.
     */
    @Override
    public void update(Observable observable, Object data) {
        // TODO: using data from the MODEL, update the UI widgets (locations, button, and status)
    	locations.setSelection(MODEL.getLocation(), true);
    	button.setEnabled(MODEL.isUIEnabled());
    	locations.setEnabled(MODEL.isUIEnabled());
    	status.setText(MODEL.getStatus());
    }

    /**
     * Receives a message from the AndroidChannel on the UI thread, so safe to call UI methods. This method is for the
     * Volunteer application, so the only message it knows about it is the LOCATION message: "LOCATION id urgency".
     */
    public void messageReceived(String message) {
        // TODO: handle message from server (only valid one is "LOCATION BLDG URGENCY") by updating MODEL
        // TODO: don't forget to notify observers
    	String[] fields = message.split(" ");
    	
    	if (fields[0].equals("LOCATION")) {
    		MODEL.setUIEnabled(true);
    		MODEL.setStatus(String.format("Proceed to %s with urgency %s", fields[1], fields[2]));
    	}
    	else
    		MODEL.setStatus("Unexpected message: " + message);
    	MODEL.notifyObservers();
    }

    /**
     * Handles changes to the "locations" and "urgencies" spinners. Updates the MODEL.
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO: when an item is selected on the spinner, update the MODEL to keep track of it
    	if (arg0 == locations)
    		MODEL.setLocation(arg2);
    	else
    		System.err.printf("ERROR: Unexpected spinner (%s)\n", arg0.toString());
    }

    /**
     * Handles resets to the spinner; which we ignore.
     */
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing
    }

    /**
     * Updates the status in the event of server connection failure. Called by AndroidChannel.
     */
    public void notifySendFailure() {
        // TODO: the network connection is down or server unavailable; update status message in MODEL
        // TODO: don't forget to notify observers
    	MODEL.setStatus("Error sending message to server; check server status and network connection");
        MODEL.setUIEnabled(true);
        MODEL.notifyObservers();
    }
}
