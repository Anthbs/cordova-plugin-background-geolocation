package com.tenforwardconsulting.cordova.bgloc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import agf.com.trackinglibrary.BluetoothSerial;

public class BackgroundGpsPlugin extends CordovaPlugin {
    private static final String TAG = "BackgroundGpsPlugin";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";
    public static final String ACTION_SET_CONFIG = "setConfig";
    public static final String ACTION_START_BLUETOOTH = "startBT";
    public static final String ACTION_STOP_BLUETOOTH = "stopBT";
    public static final String ACTION_CONNECT_BLUETOOTH = "connectBT";

    private Intent updateServiceIntent;
    private Intent updateBTServiceIntent;

    private Boolean isEnabled = false;

    private String url;
    private String params;
    private String headers;
    private String stationaryRadius = "30";
    private String desiredAccuracy = "100";
    private String distanceFilter = "30";
    private String locationTimeout = "60";
    private String isDebugging = "false";
    private String notificationTitle = "Background tracking";
    private String notificationText = "ENABLED";
    private String stopOnTerminate = "false";
    private String bluetoothMode = "false";

    public PluginResult execute(String action, JSONArray data, String callbackId) {
        Activity activity = this.cordova.getActivity();
        PluginResult.Status status = PluginResult.Status.OK;
        PluginResult progressResult = new PluginResult(PluginResult.Status.OK, "Interim 1");
        String result = "";
        Boolean result = false;
        updateServiceIntent = new Intent(activity, LocationUpdateService.class);
        updateBTServiceIntent = new Intent(activity, BluetoothSerial.class);

        if (ACTION_START.equalsIgnoreCase(action) && !isEnabled) {
            result = true;
            if (params == null || headers == null || url == null) {
                callbackContext.error("Call configure before calling start");
            } else {
                PluginResult progressResult = new PluginResult(PluginResult.Status.OK, "");
                progressResult.setKeepCallback(true);
                callbackContext.sendPluginResult(progressResult);
                updateServiceIntent.putExtra("url", url);
                updateServiceIntent.putExtra("params", params);
                updateServiceIntent.putExtra("headers", headers);
                updateServiceIntent.putExtra("stationaryRadius", stationaryRadius);
                updateServiceIntent.putExtra("desiredAccuracy", desiredAccuracy);
                updateServiceIntent.putExtra("distanceFilter", distanceFilter);
                updateServiceIntent.putExtra("locationTimeout", locationTimeout);
                updateServiceIntent.putExtra("desiredAccuracy", desiredAccuracy);
                updateServiceIntent.putExtra("isDebugging", isDebugging);
                updateServiceIntent.putExtra("notificationTitle", notificationTitle);
                updateServiceIntent.putExtra("notificationText", notificationText);
                updateServiceIntent.putExtra("stopOnTerminate", stopOnTerminate);
                updateServiceIntent.putExtra("bluetoothMode", bluetoothMode);
                if(bluetoothMode == "true" || bluetoothMode == true) {
                    activity.startService(updateBTServiceIntent);
                } else {
                    activity.startService(updateServiceIntent);
                }
                isEnabled = true;
            }
        } else if (ACTION_STOP.equalsIgnoreCase(action)) {
            isEnabled = false;
            result = true;
            if(bluetoothMode == "true" || bluetoothMode == true) {
                activity.stopService(updateBTServiceIntent);
            } else {
                activity.stopService(updateServiceIntent);
            }
            PluginResult progressResult = new PluginResult(PluginResult.Status.OK, "");
            progressResult.setKeepCallback(true);
            callbackContext.sendPluginResult(progressResult);
        } else if (ACTION_CONFIGURE.equalsIgnoreCase(action)) {
            result = true;
            try {
                // Params.
                //    0       1       2           3               4                5               6            7           8                9               10              11
                //[params, headers, url, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate]
                this.params = data.getString(0);
                this.headers = data.getString(1);
                this.url = data.getString(2);
                this.stationaryRadius = data.getString(3);
                this.distanceFilter = data.getString(4);
                this.locationTimeout = data.getString(5);
                this.desiredAccuracy = data.getString(6);
                this.isDebugging = data.getString(7);
                this.notificationTitle = data.getString(8);
                this.notificationText = data.getString(9);
                this.stopOnTerminate = data.getString(11);
                this.bluetoothMode = data.getString(12);
            } catch (JSONException e) {
                callbackContext.error("authToken/url required as parameters: " + e.getMessage());
            }
        } else if (ACTION_SET_CONFIG.equalsIgnoreCase(action)) {
            result = true;
            // TODO reconfigure Service
            callbackContext.success();
        }

        return result;
    }

    /**
     * Override method in CordovaPlugin.
     * Checks to see if it should turn off
     */
    public void onDestroy() {
        Activity activity = this.cordova.getActivity();

        if(isEnabled && stopOnTerminate.equalsIgnoreCase("true")) {
            activity.stopService(updateServiceIntent);
        }
    }
}
