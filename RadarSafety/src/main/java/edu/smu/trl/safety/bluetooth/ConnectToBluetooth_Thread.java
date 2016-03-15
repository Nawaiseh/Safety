package edu.smu.trl.safety.bluetooth;
/**
 * Created by TRL on 3/7/2016.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import edu.smu.trl.safety.utilities.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
public class ConnectToBluetooth_Thread extends Thread {

    private static final String TAG = "BluetoothChatService";
    private static final UUID MY_UUID = UUID.fromString("66841278-c3d1-11df-ab31-001de000a901");
    private static final String NAME = "AndroidLocomateMessaging";


    private final BluetoothChatService BluetoothChatService;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice BluetoothDevice;

    public ConnectToBluetooth_Thread(BluetoothChatService BluetoothChatService, BluetoothDevice BluetoothDevice) {
        this.BluetoothChatService = BluetoothChatService;
        this.BluetoothDevice = BluetoothDevice;
        BluetoothSocket tmp = null;
        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {

            tmp = BluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException Exception) {
            Log.e(TAG, "Create() Failed", Exception);
        }
        mmSocket = tmp;
    }

    public void run() {
        Log.e(TAG, "Begin mConnectThread.");
        setName("ConnectThread");

        // Always cancel discovery because it will slow down a connection
        BluetoothChatService.Renderer_Activity.BluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mmSocket.connect();
        } catch (IOException Exception) {
            // Close the socket
            try {
                mmSocket.close();
            } catch (IOException Exception2) {
                Log.e(TAG, "Unable To Close() Socket During Connection Failure", Exception2);
            }
            BluetoothChatService.Start();
            return;
        }

        // Reset the ConnectThread because we're done
        synchronized (BluetoothChatService) {
            BluetoothChatService.ConnectToBluetoothDevice_Thread = null;
        }

        // Start the connected thread
        BluetoothChatService.Connected(mmSocket, BluetoothDevice);
    }

    public void Cancel() {
        try {
            mmSocket.close();
        } catch (IOException Exception) {
            Log.e(TAG, "Close() Of Connect Socket Failed", Exception);
        }
    }
}

