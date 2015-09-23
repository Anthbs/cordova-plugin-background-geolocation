package agf.com.trackinglibrary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anthbs on 23/09/2015.
 */
public class BluetoothGPSPlugin extends CordovaPlugin {
    private final String ACTION_START = "start";
    private final String ACTION_STOP = "stop";
    private final String ACTION_CONFIGURE = "configure";
    public static BluetoothSerial mService;
    boolean mBound = false;
    public CallbackContext locationEventCallback = null;
    public CallbackContext configureEventCallback = null;

    private BroadcastReceiver Location_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if(extras.containsKey("location")){
                    String loc = (String)extras.get("location");
                    try {
                        JSONObject location = new JSONObject(loc);
                        BluetoothGPSPlugin.this.sendLocationEvent(NemaLocation.fromJSONObject(location));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            BluetoothGPSPlugin.this.sendLocationEvent(null);
        }
    };

    public void sendLocationEvent(NemaLocation location) {
        try {
            Log.d("BluetoothGPSPlugin", "Trying to send cordova location...");
            if (this.locationEventCallback != null && location != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, location.toJSONObject());
                result.getKeepCallback();
                this.locationEventCallback.sendPluginResult(result);
                Log.d("BluetoothGPSPlugin", "Sent cordova location...");
            }
        }catch (Exception e) {
            Log.e("BluetoothGPSPlugin", e.getMessage());
        }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Activity activity = this.cordova.getActivity();

        if(ACTION_START.equals(action)) {
            if(BluetoothGPSPlugin.mService != null) {
                this.locationEventCallback = callbackContext;
                BluetoothGPSPlugin.mService.connect("XGPS");
            } else {
                callbackContext.error("BluetoothGPSPlugin.mService was undefined...");
            }
        } else if(ACTION_STOP.equals(action)) {
            if(this.locationEventCallback != null) {
                this.locationEventCallback.success();
                this.locationEventCallback = null;
            }
            if (mBound) {
                activity.unbindService(mConnection);
                mBound = false;
            }
            callbackContext.success();
        } else if(ACTION_CONFIGURE.equals(action)) {
            Log.d("BluetoothGPSPlugin", "Registering Location_Receiver :)");
            activity.registerReceiver(Location_Receiver, new IntentFilter("LocationBroadcast"));
            Log.d("BluetoothGPSPlugin", "Binding Service :)");
            Intent intent = new Intent(activity, BluetoothSerial.class);
            activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            configureEventCallback = callbackContext;
        }

        return true;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("BluetoothGPSPlugin", "Service Connected :)");
            GenericBluetooth.LocalBinder binder = (GenericBluetooth.LocalBinder) service;
            BluetoothGPSPlugin.mService = (BluetoothSerial) binder.getService();
            mBound = true;
            BluetoothGPSPlugin.mService.scanForDevices();
            configureEventCallback.success();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
