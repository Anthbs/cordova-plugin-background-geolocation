package agf.com.trackinglibrary;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Anthbs on 22/09/2015.
 * Generic Bluetooth class
 */
public class GenericBluetooth extends Service {
    private static final String LOG_TAG = "BluetoothSerial";
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    public GenericBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();




    }

    @Override
    public void onCreate(){
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    discoveryStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    discoveryStopped();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceFound(device);
                    break;
            }
        }
    };

    /**
     * Called each time discovery starts
     * Clears devices ArrayList<BluetoothDevice>
     * @return void
     */
    public void discoveryStarted() {
        devices.clear();
        Log.d(LOG_TAG, "Discovery Started");
    }

    /**
     * Called each time a device is discovered
     * Added device to devices ArrayList<BluetoothDevice>
     * @return void
     */
    public void deviceFound(BluetoothDevice device) {
        if(device == null || devices.contains(device)) {
            return;
        }
        devices.add(device);
        Log.d(LOG_TAG, "Device Found: " + device.getName());
    }

    /**
     * Called each time discovery starts
     * @return void
     */
    public void discoveryStopped() {
        Log.d(LOG_TAG, "Discovery Stopped - List Devices");
        for(BluetoothDevice device : devices) {
            Log.d(LOG_TAG, "Device: " + device.getName());
        }
        Log.d(LOG_TAG, "Discovery Stopped - Complete");
    }

    public boolean enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        return true;
    }

    public boolean disableBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
        return true;
    }

    /**
     * @return boolean Bluetooth is paired to any device
     */
    public boolean isPaired() {
        return mBluetoothAdapter != null && mBluetoothAdapter.getBondedDevices().size() > 0;
    }

    /**
     * @return boolean Bluetooth is paired to specific device
     */
    public boolean isPairedTo(BluetoothDevice device) {
        return mBluetoothAdapter != null && mBluetoothAdapter.getBondedDevices().contains(device);
    }

    /**
     * @return String The friendly buletooth name of this device
     */
    public String getName() {
        if(mBluetoothAdapter == null) {
            return null;
        }
        return  mBluetoothAdapter.getName();
    }

    /**
     * @return Boolean The friendly buletooth name of this device
     */
    public boolean setName(String name) {
        return mBluetoothAdapter != null && mBluetoothAdapter.setName(name);
    }

    /**
     * Possible values are: SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE.
     * @return int A BluetoothAdapter state
     */
    public int getScanMode() {
        if(mBluetoothAdapter == null) {
            return BluetoothAdapter.SCAN_MODE_NONE;
        }
        return mBluetoothAdapter.getScanMode();
    }

    /**
     * Possible return values are STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF.
     * @return int A BluetoothAdapter state
     */
    public int getState() {
        if(mBluetoothAdapter == null) {
            return BluetoothAdapter.STATE_OFF;
        }
        return mBluetoothAdapter.getScanMode();
    }

    /**
     * @return boolean
     */
    public boolean isDiscovering() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering();
    }

    /**
     * @return boolean
     */
    public boolean startDiscovery() {
        return mBluetoothAdapter != null && mBluetoothAdapter.startDiscovery();
    }

    /**
     * @return boolean
     */
    public boolean stopDiscovery() {
        return mBluetoothAdapter != null && mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        GenericBluetooth getService() {
            // Return this instance of LocalService so clients can call public methods
            return GenericBluetooth.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
