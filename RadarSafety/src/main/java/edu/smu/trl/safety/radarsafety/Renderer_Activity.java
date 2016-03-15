package edu.smu.trl.safety.radarsafety;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.smu.trl.safety.bluetooth.BluetoothChatService;
import edu.smu.trl.safety.bluetooth.Constants;
import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.parser.IParser;
import edu.smu.trl.safety.min3d.parser.Parser;
import edu.smu.trl.safety.min3d.vos.Light;
import edu.smu.trl.safety.utilities.Log;
import edu.smu.trl.safety.utilities.SoundAnimation;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by TRL on 3/7/2016.
 */
public class Renderer_Activity extends RendererActivity {

    private static final String TAG = "Safety"; //  "BluetoothChatFragment";
    private final static long _1Minute = 60000;
    public static int NormalColor;
    public static int AlerColor;
    public static int UsedColor;
    public static android.animation.ValueAnimator ValueAnimator;
    public static LinearLayout Layout;
    public static Object Lock = "5";
    public BluetoothChatService BluetoothChatService;
    public android.bluetooth.BluetoothAdapter BluetoothAdapter = null;
    public StringBuffer StringBuffer;
    public MediaPlayer Player;
    public SoundAnimation SoundAnimation;
    public CharSequence Title;
    public TreeMap<Double, Object3dContainer> OpenGL_Cars = new TreeMap<>();
    public TreeMap<Double, Car> Cars = new TreeMap<>();
    public Menu Menu;
    public MenuItem BluetoothMenuItem;
    public Car MyCar = new Car();
    // public int ConnectionState = Constants.STATE_NONE;
    private String ConnectedDeviceName = null;
    public final Handler MessageHandler = new Handler() {

        @Override
        public void handleMessage(Message Message) {

            switch (Message.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    MessageStatusChanged(Message);
                    break;
                case Constants.MESSAGE_WRITE:
                    MessageWrite(Message);
                    break;
                case Constants.MESSAGE_READ:
                    DisplayMessage(Message);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    MessageDeviceName(Message);
                    break;
                case Constants.MESSAGE_TOAST:
                    MessageToast(Message);
                    break;
                case Constants.PLAY_ALERT_SOUND:
                    MessagePlaySound(Message);
                    break;
                case Constants.STOP_ALERT_SOUND:
                    MessageStopSound(Message);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //   this.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        Player = MediaPlayer.create(this, R.raw.beep);
        Player.setLooping(true);


        SoundAnimation = new SoundAnimation(this, R.raw.beep);
        Layout = (LinearLayout) findViewById(R.id.Container);
        NormalColor = Layout.getSolidColor();
        AlerColor = getResources().getColor(R.color.alert_color);
        UsedColor = NormalColor;


        if (BluetoothAdapter == null) {
            BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth Is Not Enabled", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    SetupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.e(TAG, "Bluetooth Is Not Enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu Menu) {
        getMenuInflater().inflate(R.menu.bluetooth_chat, Menu);
        RestoreActionBar();

        this.Menu = Menu;
        BluetoothMenuItem = Menu.findItem(R.id.ShowBluetoothNameToOthers);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "- On Start BluetoothChatFragment -");
        if (BluetoothAdapter == null) {
            return;
        }
        if (!BluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        } else if (BluetoothChatService == null) {
            SetupChat();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "- On Resume BluetoothChatFragment -");

        if (BluetoothChatService != null) {
            if (BluetoothChatService.getConnectionState() == BluetoothChatService.STATE_NONE) {
                BluetoothChatService.Start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.e(TAG, "- On Pause BluetoothChatFragment -");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "- On Stop BluetoothChatFragment -");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "- On Destroy BluetoothChatFragment -");
        if (BluetoothChatService != null) {
            BluetoothChatService.Stop();
        }
    }

    public void EnsureDiscoverable() {
        Log.e(TAG, "- Ensure Discoverable BluetoothChatFragment -");
        if (BluetoothAdapter.getScanMode() != android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            try {
                Intent DiscoverableIntent = new Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                DiscoverableIntent.putExtra(android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

                startActivity(DiscoverableIntent);

                Toast.makeText(this, "Bluetooth Connection Enabled!", Toast.LENGTH_SHORT).show();

                //MenuItem.setIcon(getResources().getDrawable(R.drawable.bluetooth_disconnected));
                if (BluetoothMenuItem != null) {
                    BluetoothMenuItem.setIcon(R.drawable.bluetooth_disconnected);
                }
                int x = 0;
            } catch (Exception e) {
                Log.e("EnsureDiscoverable:-", "Error Ensuring Bluetooth Discoverability", e);
                int x = 0;
            }
        } else {
            Log.i("EnsureDiscoverable:-", "Bluetooth is Already Discoverable");
            return;

        }
        Log.i(TAG, "- Ensure Discoverable  Extended for 300 Seconds");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem MenuItem) {

        switch (MenuItem.getItemId()) {
            case R.id.ShowBluetoothNameToOthers: {
                // Ensure this device is discoverable by others
                BluetoothMenuItem = MenuItem;
                EnsureDiscoverable();
                return true;
            }
            case R.id.StartAlert: {

                synchronized (Lock) {

                    BluetoothChatService.LastUpdate = System.currentTimeMillis();
                    BluetoothChatService.AlertIsGoing = true;
                    ValueAnimator = StartColorAnimation(Layout);
                }

                return true;
            }
            case R.id.StopAlert: {
                StopColorAnimation(Layout, ValueAnimator, NormalColor);
                synchronized (Lock) {
                    BluetoothChatService.AlertIsGoing = false;
                }

                return true;
            }

            case R.id.Five: {
                synchronized (Lock) {
                    BluetoothChatService.ThreasholdDistance = 5;
                }
                return true;
            }

            case R.id.Ten: {
                synchronized (Lock) {
                    BluetoothChatService.ThreasholdDistance = 10;
                }
                return true;
            }

            case R.id.Fifteen: {
                synchronized (Lock) {
                    BluetoothChatService.ThreasholdDistance = 15;
                }
                return true;
            }

            case R.id.Twenty: {
                synchronized (Lock) {
                    BluetoothChatService.ThreasholdDistance = 20;
                }
                return true;
            }
            case R.id.TwentyFive: {
                synchronized (Lock) {
                    BluetoothChatService.ThreasholdDistance = 25;
                }
                return true;
            }

        }
        return super.onOptionsItemSelected(MenuItem);
    }

    public void RestoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(Title);
    }

    private void SetupChat() {
        Log.e(TAG, "setupChat()");
        if (BluetoothChatService == null) {
            BluetoothChatService = new BluetoothChatService(this, MessageHandler);
        }
        // Initialize the FloatBuffer for outgoing messages
        StringBuffer = new StringBuffer("");
    }

    private Object3dContainer LoadCar() {
        IParser ModelReader = Parser.createParser(Parser.Type.OBJ, getResources(), "edu.smu.trl.safety.radarsafety:raw/camaro_obj", true);
        ModelReader.parse();

        Object3dContainer Car = ModelReader.getParsedObject();
        Car.scale().x = Car.scale().y = Car.scale().z = 1f;
        Car.position().x = Car.position().y = Car.position().z = 0;


        return Car;
    }

    @Override
    public void initScene() {
        synchronized (OpenGL_Cars) {
            Light light = new Light();
            light.ambient.setAll(0xff888888);
            light.position.setAll(3, 0, 3);
            scene.lights().add(light);


            OpenGL_Cars.put(0.0, LoadCar());
            /*
            for (double Index = 1; Index < 10; Index++) {
                OpenGL_Cars.put(Index, LoadCar());
            }
            */

            scene.addChild(OpenGL_Cars.get(0.0));

          /*
          scene.addChild(OpenGL_Cars.get(1.0));

            OpenGL_Cars.get(1.0).position().y = OpenGL_Cars.get(1.0).position().z = 0;
            OpenGL_Cars.get(1.0).position().x = -2;
            OpenGL_Cars.get(1.0).position().y = -2;
            */
        }
        scene.backgroundColor().r((short) 50);
        scene.backgroundColor().g((short) 50);
        scene.backgroundColor().b((short) 50);


        scene.camera().position.rotateX(0);
        scene.camera().position.setAll(0, 0, 30);
    }

    @Override
    public void updateScene() {

        long TimeNow = System.currentTimeMillis();
        synchronized (Lock) {
            ArrayList<Car> OutDatedCars = new ArrayList();
            for (Car Car : Cars.values()) {
                if (Car == MyCar) {
                    continue;
                }
                synchronized (Car) {
                    if ((TimeNow - Car.LastUpdated) > _1Minute) {
                        OutDatedCars.add(Car);
                        scene.removeChild(OpenGL_Cars.get(Car.ID));
                        OpenGL_Cars.remove(Car.ID);
                    } else {
                        if (!OpenGL_Cars.containsKey(Car.ID)) {
                            OpenGL_Cars.put(Car.ID, LoadCar());
                            scene.addChild(OpenGL_Cars.get(Car.ID));
                        }
                        OpenGL_Cars.get(Car.ID).position().x = Car.Location.x - MyCar.Location.x;
                        OpenGL_Cars.get(Car.ID).position().y = Car.Location.y - MyCar.Location.y;
                    }
                }
            }
        }
    /*
      synchronized (BluetoothChatService.CarLocations) {
            if (BluetoothChatService.CarLocations.containsKey(1) && OpenGL_Cars.containsKey(1)) {

                OpenGL_Cars.get(1).position().x = BluetoothChatService.CarLocations.get(1).x;
                OpenGL_Cars.get(1).position().y = BluetoothChatService.CarLocations.get(1).y;
            }
        }
        */

       /* MyCar.rotation().x++;
        MyCar.rotation().z++;

        SecondCar.rotation().x--;
        SecondCar.rotation().z--;

        */
    }

    @Override
    protected void onCreateSetContentView() {
        setContentView(R.layout.renderer_layout);
        LinearLayout LinearLayout = (LinearLayout) this.findViewById(R.id.Container);
        LinearLayout.addView(_glSurfaceView);

    }

    public final String GetString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    private void DisplayMessage(Message msg) {
        if (msg.obj instanceof String) {
            String readMessage = (String) msg.obj;
        } else {
            byte[] readBuf = (byte[]) msg.obj;
            String readMessage = new String(readBuf, 0, msg.arg1);
        }
    }

    public ValueAnimator StartColorAnimation(View View) {
        int colorStart = View.getSolidColor();
        int colorEnd = 0xFFFF0000;

        ValueAnimator ValueAnimator = ObjectAnimator.ofInt(View, "backgroundColor", colorStart, colorEnd);

        // colorAnim.setDuration(ValueAnimator.INFINITE);
        ValueAnimator.setEvaluator(new ArgbEvaluator());
        ValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        ValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        //colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator.start();
        SoundAnimation.Start();
        return ValueAnimator;
    }

    public void StopColorAnimation(View View, ValueAnimator ValueAnimator, int Color) {
        int colorStart = Color;
        int colorEnd = 0xFFFF0000;
        if (View != null && ValueAnimator != null) {
            ValueAnimator.cancel();
            SoundAnimation.Stop();
            View.setBackgroundColor(Color);
        }
    }

    private void SetStatus(int ResourceID) {

        final android.app.ActionBar ActionBar = getActionBar();
        if (ActionBar == null) {
            return;
        }
        ActionBar.setSubtitle(ResourceID);
    }

    private void SetStatus(CharSequence SubTitle) {

        final android.app.ActionBar ActionBar = getActionBar();
        if (ActionBar == null) {
            return;
        }
        ActionBar.setSubtitle(SubTitle);
    }

    private void MessageStatusChanged(Message Message) {
        switch (Message.arg1) {
            case Constants.STATE_CONNECTED:
                String Status = GetString(R.string.title_connected_to, ConnectedDeviceName);
                SetStatus(Status);
                if (BluetoothMenuItem != null) {
                    BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
                }

                break;
            case Constants.STATE_CONNECTING:
                SetStatus(R.string.title_connecting);
                if (BluetoothMenuItem != null) {
                    BluetoothMenuItem.setIcon(R.drawable.bluetooth_disconnected);
                }
                break;
            case Constants.STATE_LISTEN:
            case Constants.STATE_NONE:
                SetStatus(R.string.title_not_connected);
                if (BluetoothMenuItem != null) {
                    BluetoothMenuItem.setIcon(R.drawable.bluetooth_disconnected);
                }
                break;
        }
    }

    private void MessageWrite(Message Message) {
        byte[] writeBuf = (byte[]) Message.obj;
        String writeMessage = new String(writeBuf);
    }

    private void MessageDeviceName(Message Message) {
        ConnectedDeviceName = Message.getData().getString(Constants.DEVICE_NAME);
        Toast.makeText(this, "Connected To: " + ConnectedDeviceName, Toast.LENGTH_SHORT).show();
        if (BluetoothMenuItem != null) {
            BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
        }


    }

    private void MessageToast(Message Message) {
        Toast.makeText(this, Message.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
    }

    private void MessagePlaySound(Message Message) {
        DisplayMessage(Message);
        if (!BluetoothChatService.AlertIsGoing) {
            BluetoothChatService.AlertIsGoing = true;
            ValueAnimator = StartColorAnimation(Layout);
        }
    }

    private void MessageStopSound(Message Message) {
        DisplayMessage(Message);
        if (BluetoothChatService.AlertIsGoing) {
            BluetoothChatService.AlertIsGoing = false;
            StopColorAnimation(Layout, ValueAnimator, NormalColor);
        }
    }


}
