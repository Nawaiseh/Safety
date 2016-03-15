package edu.smu.trl.safety.bluetooth;

/**
 * Created by TRL on 3/7/2016.
 */

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import edu.smu.trl.safety.utilities.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * This thread runs while listening for incoming connections. It behaves
 * like a server-side client. It runs until a connection is accepted
 * (or until cancelled).
 */
public class AcceptNewBluetoothConnection_Thread extends Thread {
    private static final String TAG = "BluetoothChatService";
    private static final UUID MY_UUID = UUID.fromString("66841278-c3d1-11df-ab31-001de000a901");
    private static final String NAME = "AndroidLocomateMessaging";
    // The local server socket
    private final BluetoothServerSocket BluetoothServerSocket;
    private final BluetoothChatService BluetoothChatService;


    public AcceptNewBluetoothConnection_Thread(BluetoothChatService BluetoothChatService) {
        this.BluetoothChatService = BluetoothChatService;
        BluetoothServerSocket tmp = null;

        // Create a new listening server socket
        try {

            tmp = BluetoothChatService.Renderer_Activity.BluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

        } catch (IOException Exception) {
            Log.e(TAG, "Listen() Failed", Exception);
        }
        BluetoothServerSocket = tmp;
    }

    public void run() {
        Log.e(TAG, "BEGIN mAcceptThread" + this);
        setName("AcceptNewBluetoothConnection_Thread");

        BluetoothSocket socket = null;

        // Listen to the server socket if we're not connected
        while (BluetoothChatService.ConnectionState != Constants.STATE_CONNECTED) {
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = BluetoothServerSocket.accept();
            } catch (IOException Exception) {
                Log.e(TAG, "Accept() Failed", Exception);
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                synchronized (BluetoothChatService) {
                    switch (BluetoothChatService.ConnectionState) {
                        case Constants.STATE_LISTEN:
                        case Constants.STATE_CONNECTING:
                            BluetoothChatService.Connected(socket, socket.getRemoteDevice());
                            break;
                        case Constants.STATE_NONE:
                        case Constants.STATE_CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException Exception) {
                                Log.e(TAG, "Could Not Close Unwanted Socket", Exception);
                            }
                            break;
                    }
                }
            }
        }
        Log.e(TAG, "End mAcceptThread.");
    }

    public void Cancel() {
        Log.e(TAG, "Cancel " + this);
        try {
            BluetoothServerSocket.close();
        } catch (IOException Exception) {
            Log.e(TAG, "Close() of server failed", Exception);
        }
    }
}


