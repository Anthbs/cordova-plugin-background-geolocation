package agf.com.trackinglibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;

/**
 * Created by Anthbs on 22/09/2015.
 */
public class AGFBluetoothDevice extends Thread {
    private static final String LOG_TAG = "BLUETOOTH_AGF";
    public final BluetoothDevice device;
    private final BluetoothSocket socket;
    private final InputStream in;
    private final OutputStream outBinary;
    private final PrintStream outString;
    private boolean ready = false;
    private boolean enabled = false;
    private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final Nmea nemaParser = new Nmea(this);
    private Context context = null;

    public AGFBluetoothDevice(BluetoothDevice device, Context context) throws IOException {
        this.device = device;
        this.context = context;
        this.socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        PrintStream tmpOut2 = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
            if (tmpOut != null){
                tmpOut2 = new PrintStream(tmpOut, false, "US-ASCII");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "error while getting socket streams", e);
        }
        in = tmpIn;
        outBinary = tmpOut;
        outString = tmpOut2;
    }

    public NemaLocation lastLocation = null;

    public void broadcastLocationChange(NemaLocation location) {
        Intent intent = new Intent();
        intent.setAction("LocationBroadcast");
        intent.putExtra("location", location);
        context.sendBroadcast(intent);
    }

    public void locationChangedEvent(NemaLocation location) {
        if(location != null && (lastLocation == null || (location.getLatitude() != lastLocation.getLatitude() && location.getLongitude() != lastLocation.getLongitude()))) {
            Log.v(LOG_TAG, location.toString());
            Log.d(LOG_TAG, location.toString());
            broadcastLocationChange(location);
            if(lastLocation != null) {
                Log.d(LOG_TAG, lastLocation.toString());
            }
        }

        lastLocation = location;
    }

    public boolean isReady(){
        return ready;
    }

    @Override
    public void run() {
        this.read();
    }

    public void read(){
        try {
            if(!this.socket.isConnected()) {
                this.socket.connect();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"US-ASCII"));
            String s;
            long now = SystemClock.uptimeMillis();
            long lastRead = now;
            this.enabled = true;
            while((enabled) && (now < lastRead+5000)){
                if (reader.ready()){
                    s = reader.readLine();
                    //Log.v(LOG_TAG, "       data: " + System.currentTimeMillis() + " " + s);
                    Nmea.GPSPosition l = nemaParser.parse(s);


                    ready = true;
                    lastRead = SystemClock.uptimeMillis();
                } else {
                    Log.d(LOG_TAG, "data: not ready "+System.currentTimeMillis());
                    SystemClock.sleep(500);
                }
                now = SystemClock.uptimeMillis();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "error while getting data", e);
        } finally {
            // cleanly closing everything...
            this.close();
        }
    }
    public void write(byte[] buffer) {
        try {
            do {
                Thread.sleep(100);
            } while ((enabled) && (! ready));
            if ((enabled) && (ready)){
                outBinary.write(buffer);
                outBinary.flush();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception during write", e);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Exception during write", e);
        }
    }

    public void write(String buffer) {
        try {
            do {
                Thread.sleep(100);
            } while ((enabled) && (! ready));
            if ((enabled) && (ready)){
                outString.print(buffer);
                outString.flush();
            }
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Exception during write", e);
        }
    }

    public synchronized void close(){
        this.ready = false;
        this.enabled = false;

        try {
            Log.d(LOG_TAG, "closing Bluetooth GPS output sream");
            in.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "error while closing GPS NMEA output stream", e);
        } finally {
            try {
                Log.d(LOG_TAG, "closing Bluetooth GPS input streams");
                outString.close();
                outBinary.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "error while closing GPS input streams", e);
            } finally {
                try {
                    Log.d(LOG_TAG, "closing Bluetooth GPS socket");
                    socket.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "error while closing GPS socket", e);
                }
            }
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
