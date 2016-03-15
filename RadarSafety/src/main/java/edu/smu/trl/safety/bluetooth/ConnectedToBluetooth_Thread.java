package edu.smu.trl.safety.bluetooth;

/**
 * Created by TRL on 3/7/2016.
 */

import android.bluetooth.BluetoothSocket;
import edu.smu.trl.safety.radarsafety.Car;
import edu.smu.trl.safety.radarsafety.Car.DistanceUnit;
import edu.smu.trl.safety.utilities.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * * This thread runs during a connection with a remote device.
 * * It handles all incoming and outgoing transmissions.
 **/
public class ConnectedToBluetooth_Thread extends Thread {

    private static final String TAG = "BluetoothChatService";
    public static Object Lock;
    private final BluetoothChatService BluetoothChatService;
    private final BluetoothSocket BluetoothSocket;
    private InputStream InputStream = null;
    private OutputStream OutputStream = null;
    private Car MyCar;

    public ConnectedToBluetooth_Thread(BluetoothChatService BluetoothChatService, BluetoothSocket BluetoothSocket) {
        this.BluetoothChatService = BluetoothChatService;
        MyCar = BluetoothChatService.Renderer_Activity.MyCar;

        Lock = BluetoothChatService.Renderer_Activity.Lock;

        Log.e(TAG, "Create ConnectedThread.");
        this.BluetoothSocket = BluetoothSocket;


        // Get the BluetoothSocket input and output streams
        try {
            InputStream = BluetoothSocket.getInputStream();
            OutputStream = BluetoothSocket.getOutputStream();
        } catch (IOException Exception) {
            Log.e(TAG, "Temp Sockets Not Created", Exception);
            InputStream = null;
            OutputStream = null;
        }
    }


    public void run() {
        Log.e(TAG, "BEGIN mConnectedThread");
        byte[] Bluetooth_Message = new byte[1024];
        int NumberOfBytes;

        float N1 = 2147483647L;
        float N2 = 4294967296L;
        Car OtherCar = new Car();

        while (true) {

            try {
                NumberOfBytes = InputStream.read(Bluetooth_Message);  // Read from the InputStream
                Log.i(TAG, "NumberOfBytes:- " + NumberOfBytes);
                String Message = "";
                try {

                    MyCar.SetData(Bluetooth_Message, NumberOfBytes);
                    OtherCar.SetData(Bluetooth_Message);

                    double Distance = Car.ConvertDistance(MyCar.DistanceFrom(OtherCar), DistanceUnit.Meters);
                    Message = String.format("%s, %s, Distance = %f Meters", MyCar.Position(), OtherCar.Position(), Distance);

                    synchronized (Lock) {
                        if (BluetoothChatService.Renderer_Activity.Cars.containsKey(OtherCar.ID)) {
                            BluetoothChatService.Renderer_Activity.Cars.get(OtherCar.ID).SetData(OtherCar);
                        } else {

                            BluetoothChatService.Renderer_Activity.Cars.put(OtherCar.ID, OtherCar);
                            OtherCar = new Car();
                        }

                        if (Distance <= BluetoothChatService.ThreasholdDistance) {
                            BluetoothChatService.MessageHandler.obtainMessage(Constants.PLAY_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                            BluetoothChatService.LastUpdate = System.currentTimeMillis();
                        } else {
                            BluetoothChatService.MessageHandler.obtainMessage(Constants.STOP_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                        }
                    }
                } catch (Exception Exception) {
                    Log.e(TAG, "Exception", Exception);
                }

            } catch (IOException Exception) {
                Log.e(TAG, "Disconnected", Exception);
                BluetoothChatService.ConnectionLost();

            }
        }

    }

    public void Write(byte[] Bluetooth_Message) {
        Log.e(TAG, "- write BluetoothChatFragment - ConnectedThread -");
        try {
            OutputStream.write(Bluetooth_Message);

            // Share the sent message back to the UI Activity
            BluetoothChatService.MessageHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, Bluetooth_Message).sendToTarget();
        } catch (IOException Exception) {
            Log.e(TAG, "Exception During Write", Exception);
        }
    }

    public void Cancel() {
        try {
            BluetoothSocket.close();
        } catch (IOException Exception) {
            Log.e(TAG, "Close() Of Connect Socket Failed", Exception);
        }
    }
}
