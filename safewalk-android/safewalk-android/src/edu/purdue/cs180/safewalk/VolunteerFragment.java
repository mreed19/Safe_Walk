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
 * @author YOU
 * @author YOUR-PARTNER
 */
@SuppressLint("DefaultLocale")
public class VolunteerFragment extends Fragment implements ChannelListener, View.OnClickListener, Observer, OnItemSelectedListener {
    final static Model model = new Model();
    final static AndroidChannel channel = MainActivity.initializeChannel();

    private Button button;
    private TextView status;
    private Spinner locations;

    /**
     * Creates the Volunteer user interface, and sets up the model and channel to the server.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.volunteer, container, false);
        button = (Button) rootView.findViewById(R.id.volunteer_button);
        status = (TextView) rootView.findViewById(R.id.volunteer_status_textview);
        locations = (Spinner) rootView.findViewById(R.id.locations_spinner);

        // TODO: add this Fragment object to the Model observer and AndroidChannel listener...

        // TODO: set/restore the UI state from the model...

        // TODO: add listeners for the Volunteer button and spinner...
        button.setOnClickListener(this);


        return rootView;
    }
    
    /**
     * Cleans up on exit by deleting the model observer.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO: add code to delete this Observer from the model
    }

    /**
     * Handles processing of "Press When Ready" button press.
     */
    @Override
    public void onClick(View v) {
        status.setText("Volunteer not implemented yet"); // TODO Delete this line (!)

        // TODO: disable controls until request assigned (or failure sent)...

        // TODO: prepare message and send to server...
    }

    /**
     * Updates the view widgets whenever the model changes.
     */
    @Override
    public void update(Observable observable, Object data) {
        // TODO: using data from the model, update the UI widgets (locations, button, and status)
    }

    /**
     * Receives a message from the AndroidChannel on the UI thread, so safe to call UI methods. This method is for the
     * Volunteer application, so the only message it knows about it is the LOCATION message: "LOCATION id urgency".
     */
    public void messageReceived(String message) {
        // TODO: handle message from server (only valid one is "LOCATION ID URGENCY") by updating model
        // TODO: don't forget to notify observers
    }

    /**
     * Handles changes to the "locations" and "urgencies" spinners. Updates the model.
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO: when an item is selected on the spinner, update the model to keep track of it
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
        // TODO: the network connection is down or server unavailable; update status message in model
        // TODO: don't forget to notify observers
    }
}
