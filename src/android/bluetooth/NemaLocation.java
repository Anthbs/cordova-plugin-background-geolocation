package agf.com.trackinglibrary;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthbs on 22/09/2015.
 */
public class NemaLocation extends Location {

    public NemaLocation() {
        super("Nema");
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("latitude",this.getLatitude());
            jsonObject.put("longitude", this.getLongitude());
            jsonObject.put("speed", this.getSpeed());
            jsonObject.put("accuracy", this.getAccuracy());
            jsonObject.put("altitude", this.getAltitude());
            jsonObject.put("bearing", this.getBearing());
            jsonObject.put("time", this.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return  "Lat: " + this.getLatitude() +
                " Lng: " + this.getLongitude() +
                " Speed: " + this.getSpeed() +
                " Acc: " + this.getAccuracy() +
                " Alt: " + this.getAltitude() +
                " Bearing: " + this.getBearing() +
                " Time: " + this.getTime();
    }
}
