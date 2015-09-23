package agf.com.trackinglibrary;

import android.util.Log;

/**
 * Created by Anthbs on 22/09/2015.
 */
public class NmeaEventListener implements NmeaEvent {
    private static final String LOG_TAG = "BLUETOOTH_AGF";
    private NemaLocation lastLocation = null;

    @Override
    public void locationChanged(NemaLocation location) {
        if(location.getLatitude() != lastLocation.getLatitude() || location.getLongitude() != lastLocation.getLongitude()) {
            Log.v(LOG_TAG, location.toString());
        }

        lastLocation = location;
    }
}
