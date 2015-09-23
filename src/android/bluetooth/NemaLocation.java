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

    public static NemaLocation fromJSONObject(JSONObject obj) {
        NemaLocation loc = new NemaLocation();
        try {
            loc.setLatitude(obj.getDouble("latitude"));
            loc.setLongitude(obj.getDouble("longitude"));
            loc.setSpeed(obj.getLong("speed"));
            loc.setAccuracy(obj.getLong("accuracy"));
            loc.setAltitude(obj.getDouble("altitude"));
            loc.setBearing(obj.getLong("bearing"));
            loc.setTime(obj.getLong("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loc;
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
