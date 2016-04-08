package edu.smu.trl.safety.bluetooth;

/**
 * Created by TRL on 3/7/2016.
 */

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.smu.trl.safety.radarsafety.Car;
import edu.smu.trl.safety.radarsafety.Car.DistanceUnit;
import edu.smu.trl.safety.utilities.Log;


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

    private String BluetoothMessageAsString(byte[] Bluetooth_Message) {
        String Result = String.format("%s\n%s", Car.BytesToHEX(Bluetooth_Message, 0, 38).trim(), Car.BytesToHEX(Bluetooth_Message, 38, 38).trim());
        return Result;
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
                Bluetooth_Message = new byte[80];
                if (BluetoothSocket.isConnected()) {
                    if (InputStream.available() > 0) {
                        NumberOfBytes = InputStream.read(Bluetooth_Message);  // Read from the InputStream
                        if (NumberOfBytes == 76) {
                            String Bluetooth_MessageAsString = BluetoothMessageAsString(Bluetooth_Message);
                            Log.i(TAG, "NumberOfBytes:- " + NumberOfBytes);
                            String Message = "";
                            synchronized (Lock) {
                                try {
                                    boolean Different = false;
                                    for (int i = 1; i < 5; i++) {
                                        if (Bluetooth_Message[i] != Bluetooth_Message[i + 38]) {
                                            Different = true;

                                            break;
                                        }
                                    }
                                    if (!Different) {
                                        continue;
                                    }
                                    MyCar.SetData(Bluetooth_Message);
                                    OtherCar.SetData(Bluetooth_Message);

                                    OtherCar.DistanceInMeters = OtherCar.DistanceFrom(MyCar, DistanceUnit.Meters);
                                    Message = String.format("Me:- %s\n OtherCar:- %s\nDistance = %.1f Meters",
                                            MyCar.Position(Car.PositionType.NoAltitude), OtherCar.Position(Car.PositionType.NoAltitude), OtherCar.DistanceInMeters);


                                    BluetoothChatService.MessageHandler.obtainMessage(Constants.MESSAGE_READ, Message.length(), -1, Message).sendToTarget();


                                    if (BluetoothChatService.Renderer_Activity.Cars.containsKey(OtherCar.ID)) {
                                        BluetoothChatService.Renderer_Activity.Cars.get(OtherCar.ID).SetData(OtherCar);
                                    } else {

                                        BluetoothChatService.Renderer_Activity.Cars.put(OtherCar.ID, OtherCar);


                                        OtherCar = new Car();
                                    }

                                    OtherCar.OpenGLLocation.x = OtherCar.Location.x - MyCar.Location.x;
                                    OtherCar.OpenGLLocation.y = OtherCar.Location.x - MyCar.Location.y;
                                    OtherCar.OpenGLLocation.z = OtherCar.Location.z - MyCar.Location.z;


                                    OtherCar.OpenGLRotation.z = OtherCar.Direction - MyCar.Direction;



/*                            if (DistanceInMeters <= BluetoothChatService.ThreasholdDistance) {
                                Message = String.format("%s\nAlert!:- A Car is Closer than %d", Message, BluetoothChatService.ThreasholdDistance);
                                BluetoothChatService.MessageHandler.obtainMessage(Constants.PLAY_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                                BluetoothChatService.LastUpdate = System.currentTimeMillis();
                            } else {
                                Message = String.format("%s\nCar Is Far Enough Now", Message);
                                BluetoothChatService.MessageHandler.obtainMessage(Constants.STOP_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                            }*/

                                } catch (Exception Exception) {
                                    Log.e(TAG, "Disconnected", Exception);
                                    Log.e(TAG, "Exception", Exception);
                                }

                            }
                        } else {
                            Log.e(TAG, "Exception:- Number of Bytes <> 76");
                            int x = 0;
                        }
                    } else {
                        SystemClock.sleep(10);
                    }
                } else {

                    Log.e(TAG, "Disconnected");
                    BluetoothChatService.ConnectionLost();
                }
            } catch (Exception Exception) {
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
