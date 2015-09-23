package agf.com.trackinglibrary;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Anthbs on 22/09/2015.
 */
public class BluetoothSerial extends GenericBluetooth {
    private static final String LOG_TAG = "BluetoothSerial";

    public ArrayList<AGFBluetoothDevice> getAGFDevices() {
        ArrayList<AGFBluetoothDevice> agfDevices = new ArrayList<>();
        try {
            Context context = getApplicationContext();
            for(BluetoothDevice device : this.devices) {
                    agfDevices.add(new AGFBluetoothDevice(device, context));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return agfDevices;
    }

    public void connect(AGFBluetoothDevice agfDevice) {
        this.stopScanForDevices();
        agfDevice.start(); // Start thread
    }

    public void connect(String deviceName) {
        this.stopScanForDevices();
        for(AGFBluetoothDevice agfDevice : this.getAGFDevices()) {
            if(agfDevice.device.getName().startsWith(deviceName)) {
                this.connect(agfDevice);
                break;
            }
        }
    }

    public void disconnect(AGFBluetoothDevice device) {
        device.close();
    }

    public void scanForDevices() {
        this.startDiscovery();
    }

    public void stopScanForDevices() {
        this.stopDiscovery();
    }
}
