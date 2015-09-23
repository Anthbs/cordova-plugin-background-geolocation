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
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Anthbs on 23/09/2015.
 */
public class BluetoothGPSPlugin extends CordovaPlugin {
    private final String ACTION_START = "start";
    private final String ACTION_STOP = "stop";
    private final String ACTION_CONFIGURE = "configure";
    BluetoothSerial mService;
    boolean mBound = false;
    public CallbackContext locationEventCallback = null;

    private BroadcastReceiver Location_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if(extras.containsKey("location")){
                    BluetoothGPSPlugin.this.sendLocationEvent((NemaLocation)extras.get("location"));
                }
            }
            BluetoothGPSPlugin.this.sendLocationEvent(null);
        }
    };

    public void sendLocationEvent(NemaLocation location) {
        Log.d("BluetoothGPSPlugin", "Trying to send cordova location...");
        if(this.locationEventCallback != null) {
            this.locationEventCallback.sendPluginResult(new PluginResult(PluginResult.Status.OK, location.toJSONObject()));
            Log.d("BluetoothGPSPlugin", "Sent cordova location...");
        }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Activity activity = this.cordova.getActivity();

        if(ACTION_START.equals(action)) {
            mService.connect("XGPS");
        } else if(ACTION_STOP.equals(action)) {
            if(this.locationEventCallback != null) {
                this.locationEventCallback.success();
                this.locationEventCallback = null;
            }
            if (mBound) {
                activity.unbindService(mConnection);
                mBound = false;
            }
        } else if(ACTION_CONFIGURE.equals(action)) {
            this.locationEventCallback = callbackContext;
            activity.registerReceiver(Location_Receiver, new IntentFilter("LocationBroadcast"));
            activity.bindService(activity.getIntent(), mConnection, Context.BIND_AUTO_CREATE);
        }

        return true;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GenericBluetooth.LocalBinder binder = (GenericBluetooth.LocalBinder) service;
            mService = (BluetoothSerial) binder.getService();
            mBound = true;
            mService.scanForDevices();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
