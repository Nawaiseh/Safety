package edu.smu.trl.safety.radarsafety;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TreeMap;

import edu.smu.trl.safety.bluetooth.BluetoothChatService;
import edu.smu.trl.safety.bluetooth.Constants;
import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.vos.Number3d;
import edu.smu.trl.safety.utilities.Log;
import edu.smu.trl.safety.utilities.SoundAnimation;


public class Renderer_Activity extends AppCompatActivity {
    private static final String TAG = "Safety"; //  "BluetoothChatFragment";
    private final static long _LastUpdateThreashold = 5000;
    public static int NormalColor;
    public static int AlerColor;
    public static int UsedColor;
    public static ValueAnimator ValueAnimator;
    public static LinearLayout Layout;
    public static Object Lock = "5";
    public Menu Menu;
    public MenuItem BluetoothMenuItem;
    public CharSequence Title = "V2V Interface";
    public BluetoothChatService BluetoothChatService;
    public android.bluetooth.BluetoothAdapter BluetoothAdapter = null;
    //public TreeMap<String, Object3dContainer> OpenGL_Cars = new TreeMap<>();
    public Queue<Object3dContainer> CarsPool = new LinkedList<>();
    public TreeMap<String, Car> Cars = new TreeMap<>();
    public float RotationXAngle = 0.523599f;
    public Car MyCar = new Car();
    public SoundAnimation SoundAnimation;
    protected PowerManager.WakeLock WakeLock;
    TextView TextView;
    java.io.FileOutputStream FileOutputStream = null;
    java.text.SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
    private ListView BluetoothChatView;
    private ArrayAdapter<String> BluetoothChatArrayAdapter;

    //  Writer.write(textmsg.getText().toString());
    private String ConnectedDeviceName = null;
    public final Handler MessageHandler = new Handler() {
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
    //<editor-fold defaultstate="collapsed" desc=" ~~~~~~~~~~~~~~~~~~~~~~~~~~  InitializeCPLEX ~~~~~~~~~~~~~~~~~~~~~~~~~~ ">
    private boolean RotationAllowed = false;

    public void EnsureDiscoverable() {
        Log.e(TAG, "- Ensure Discoverable BluetoothChatFragment -");
        if (BluetoothAdapter.getScanMode() != android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            try {
                Intent DiscoverableIntent = new Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                DiscoverableIntent.putExtra(android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Integer.MAX_VALUE);

                startActivity(DiscoverableIntent);

                BluetoothChatArrayAdapter.add("Bluetooth Connection Is Enabled");
                //   Toast.makeText(this, "Bluetooth Connection Enabled!", Toast.LENGTH_SHORT).show();

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

            case R.id.Direction: {
                if (MenuItem.getTitle().toString().toUpperCase().startsWith("ENABLE")) {
                    MenuItem.setTitle("Disable Angle Rotation");
                    RotationAllowed = true;
                } else {
                    MenuItem.setTitle("Enable Angle Rotation");
                    RotationAllowed = false;
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

    protected void DisplayMessage(Message msg) {
        try {
            if (msg.obj instanceof String) {
                String readMessage = (String) msg.obj;

                BluetoothChatArrayAdapter.add(readMessage);
            } else {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                BluetoothChatArrayAdapter.add(readMessage);
            }
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    protected void SetStatus(int ResourceID) {

        try {
            final android.app.ActionBar ActionBar = getActionBar();
            if (ActionBar == null) {
                return;
            }
            ActionBar.setSubtitle(ResourceID);
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    protected void SetStatus(CharSequence SubTitle) {
        try {
            final android.app.ActionBar ActionBar = getActionBar();
            if (ActionBar == null) {
                return;
            }
            ActionBar.setSubtitle(SubTitle);
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    public final String GetString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    protected void MessageStatusChanged(Message Message) {
        switch (Message.arg1) {
            case Constants.STATE_CONNECTED:
                String Status = GetString(R.string.title_connected_to, ConnectedDeviceName);
                SetStatus(Status);
                if (BluetoothMenuItem != null) {
                    BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
                }
                BluetoothChatArrayAdapter.clear();
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

    protected void MessageWrite(Message Message) {
        try {
            byte[] writeBuf = (byte[]) Message.obj;
            String writeMessage = new String(writeBuf);

            BluetoothChatArrayAdapter.add("Me:  " + writeMessage);
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    protected void MessageDeviceName(Message Message) {
        try {
            ConnectedDeviceName = Message.getData().getString(Constants.DEVICE_NAME);
            BluetoothChatArrayAdapter.add(String.format("Connected To: %s", ConnectedDeviceName));
            // Toast.makeText(this, "Connected To: " + ConnectedDeviceName, Toast.LENGTH_SHORT).show();
            TextView.setText("Connected To: " + ConnectedDeviceName);

            if (BluetoothMenuItem != null) {
                BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
            }

        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    protected void MessageToast(Message Message) {
        DisplayMessage(Message);
        //Toast.makeText(this, Message.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
    }

    protected void MessagePlaySound(Message Message) {
        DisplayMessage(Message);
        if (!BluetoothChatService.AlertIsGoing) {
            BluetoothChatService.AlertIsGoing = true;
            ValueAnimator = StartColorAnimation(Layout);
            BluetoothChatArrayAdapter.add("Alert!:- A Car is Too Close");
        }
    }

    protected void MessageStopSound(Message Message) {
        DisplayMessage(Message);
        if (BluetoothChatService.AlertIsGoing) {
            BluetoothChatService.AlertIsGoing = false;
            //  BluetoothChatArrayAdapter.add("Cars Are Far Enough Now");
            StopColorAnimation(Layout, ValueAnimator, NormalColor);
        }
    }

    protected void SetupChat() {
        Log.e(TAG, "setupChat()");
        if (BluetoothChatService == null) {
            BluetoothChatService = new BluetoothChatService(this, MessageHandler);
        }

        BluetoothChatArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        BluetoothChatView.setAdapter(BluetoothChatArrayAdapter);
        //  // Initialize the FloatBuffer for outgoing messages
        // StringBuffer = new StringBuffer("");
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
        if (WakeLock != null) {
            this.WakeLock.release();
        }
        super.onDestroy();
        Log.e(TAG, "- On Destroy BluetoothChatFragment -");
        if (BluetoothChatService != null) {
            BluetoothChatService.Stop();
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

    public void RestoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(Title);
    }

    public ValueAnimator StartColorAnimation(View View) {
        try {
            int colorStart = View.getSolidColor();
            int colorEnd = getResources().getColor(R.color.alert_color);


            ValueAnimator ValueAnimator = ObjectAnimator.ofInt(View, "backgroundColor", colorStart, colorEnd);

            // colorAnim.setDuration(ValueAnimator.INFINITE);
            ValueAnimator.setEvaluator(new ArgbEvaluator());
            ValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            ValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
            //colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator.start();
            SoundAnimation.Start();
            return ValueAnimator;
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
        return null;
    }

    public void StopColorAnimation(View View, ValueAnimator ValueAnimator, int Color) {
        int colorStart = Color;
        int colorEnd = 0xFFFF0000;
        try {
            if (View != null && ValueAnimator != null) {
                ValueAnimator.cancel();
                SoundAnimation.Stop();
                View.setBackgroundColor(Color);
            }
        } catch (Exception Exception) {
            Log.e(TAG, "Exception", Exception);
        }
    }

    //</editor-fold>


    protected void onDraw(Canvas Canvas) {

    }

    private void SendFileToEmail(String FileName, String Path, String EmailTo, String Subject) {

        File File = new File(Path, FileName);
        if (File.exists()) {
            try {
                Uri FileUri = Uri.fromFile(File);
                Intent EmailIntent = new Intent(Intent.ACTION_SEND);
                EmailIntent.setType("vnd.android.cursor.dir/email");
                EmailIntent.putExtra(Intent.EXTRA_EMAIL, EmailTo);
                EmailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(File.getAbsolutePath()));
                EmailIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
                startActivity(Intent.createChooser(EmailIntent, "Send Email..."));

            } catch (Exception Exception) {
                Exception.printStackTrace();
                int x = 0;
            }
        }
    }

    /* final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
     private void AskForPermissions(String [] Permissions){
         List<String> NeededPermissions = new ArrayList<>();
         for(String Permission : Permissions) {
             int WritePermission = this.checkSelfPermission(Permission);
             if (WritePermission != PackageManager.PERMISSION_GRANTED) {
                 NeededPermissions.add(Permission);
             }
         }

         String [] RequestestPermissions = NeededPermissions.toArray(new String[NeededPermissions.size()]);
         if (RequestestPermissions!= null && RequestestPermissions.length>0) {
             requestPermissions(RequestestPermissions, REQUEST_CODE_ASK_PERMISSIONS);
         }
     }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renderer_);
        MyCar.Type = Car.CarType.MyCar;
        BluetoothChatView = (ListView) this.findViewById(R.id.ListView);


        String[] PermissionsToCheck = new String[]{
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_PRIVILEGED,
                android.Manifest.permission.WRITE_SETTINGS,
                android.Manifest.permission.WAKE_LOCK,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //   AskForPermissions(PermissionsToCheck);

        String DocumentsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        //  SendFileToEmail("Bluetooth_Data.txt",DocumentsPath, "nawaiseh@gmail.com", "Bluetooth_Data.txt");
        //  SendFileToEmail("UI_Data.txt", DocumentsPath, "nawaiseh@gmail.com", "Bluetooth_Data.txt");


        BluetoothChatView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        final PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        this.WakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.WakeLock.acquire();

        TextView = (TextView) this.findViewById(R.id.ConnectionStatus);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);


        SoundAnimation = new SoundAnimation(this, R.raw.beep);
        Layout = (LinearLayout) findViewById(R.id.DrawingView);
        NormalColor = Layout.getSolidColor();
        AlerColor = getResources().getColor(R.color.normal_color);
        UsedColor = NormalColor;

        //Layout.onDrawForeground();
        if (BluetoothAdapter == null) {
            BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothAdapter == null) {
                //  Toast.makeText(this, "Bluetooth Is Not Enabled", Toast.LENGTH_LONG).show();
                finish();
            }
        }


        Layout.addView(new MyView(this));
    }

    public class MyView extends View {
        public final Handler ViewMessageHandler = new Handler() {
            public void handleMessage(Message Message) {
                invalidate();
            }
        };
        Paint BackGroundPaint = new Paint();
        Paint MyCarPaint = new Paint();
        Paint OtherCarPaint = new Paint();
        Number3d Center = new Number3d();
        Number3d OtherCarLocation = new Number3d();
        Typeface Typeface;
        ArrayList<Car> OutDatedCars = new ArrayList<>();
        Timer Timer = new Timer();
        TimerTask TimerTask = new TimerTask(this);
        OutputStreamWriter Writer;

        public MyView(Context context) {
            super(context);


            try {
                File Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                boolean PathExist = Path.exists();
                if (!PathExist) {
                    PathExist = Path.mkdirs();
                    Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    int x = 0;
                }
                if (PathExist) {
                    FileOutputStream = new FileOutputStream(new File(Path, "UI_Data.txt"));
                    Writer = new OutputStreamWriter(FileOutputStream);
                } else {

                }
            } catch (Exception Exception) {
                Exception.printStackTrace();
                Log.e(TAG, "Exception", Exception);
            }


            this.Typeface = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");


            /*Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth()-100;
            int height = display.getHeight() - 20;
            this.setMinimumWidth(width);
            this.setMinimumHeight(height);*/

            BackGroundPaint.setStyle(Paint.Style.FILL);
            BackGroundPaint.setColor(NormalColor);

            MyCarPaint.setStyle(Paint.Style.FILL);
            MyCarPaint.setColor(getResources().getColor(R.color.MyCar_color));

            MyCarPaint.setTypeface(this.Typeface);
            OtherCarPaint.setStyle(Paint.Style.FILL);
            OtherCarPaint.setTypeface(this.Typeface);
            OtherCarPaint.setColor(getResources().getColor(R.color.OtherCars_color));

            //  Timer.schedule(TimerTask, 0, 5);
        }

        private void DrawRectangle(Canvas Canvas, Paint Paint, Number3d Location) {
            Canvas.drawRect(Location.x - 10, Location.y - 20, Location.x + 10, Location.y + 20, Paint);
        }

        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.e(TAG, "ORIENTATION_LANDSCAPE");
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.e(TAG, "ORIENTATION_PORTRAIT");
            }
        }

        @Override
        protected void onDraw(Canvas Canvas) {

            String Message;
            float MinDistance = Float.MAX_VALUE;
            super.onDraw(Canvas);
            if (Cars.isEmpty()) {
                invalidate();

            } else {
                //   synchronized (Lock) {
                try {
                    long TimeNow = System.currentTimeMillis();
                    Calendar _Calendar = Calendar.getInstance();
                    String TimeString = SimpleDateFormat.format(_Calendar.getTime());
                    Center.x = getWidth() / 2F;
                    Center.y = getHeight() / 2F;
                    Canvas.drawPaint(BackGroundPaint);


                    DrawRectangle(Canvas, MyCarPaint, Center);
                    int Y = 20;
                    //Canvas.drawText("MyCar[" + MyCar.ID + "] @ (" + MyCar.OpenGLLocation.x + " , " + MyCar.OpenGLLocation.y + ")", 5, Y, MyCarPaint);
                    if (MyCar != null && MyCar.ID != null) {
                        String Msg = String.format("[%s] Located @ (%.2f , %.2f ) Distance = %.3f, Speed = %.2f M/H", MyCar.ID, MyCar.Location.x, MyCar.Location.y, MyCar.DistanceInMeters, MyCar.Speed);
                        Canvas.drawText(Msg, 5, Y, MyCarPaint);
                        if (Writer != null) {
                            Writer.write(String.format("%s:\t%s\n", SimpleDateFormat.format(_Calendar.getTime()), Msg.replace(",", "\t").replace("@", "@\t").replace("=", "=\t")));
                        }
                    }
                    Y += 20;
                    Log.i(TAG, String.format("MyCar:- %s", Center.toString()));


                    for (Car Car : Cars.values()) {

                        if (MinDistance > Car.DistanceInMeters) {
                            MinDistance = Car.DistanceInMeters;
                        }
                        if ((TimeNow - Car.LastUpdated) > _LastUpdateThreashold) {
                            OutDatedCars.add(Car);
                        } else {
                            OtherCarLocation.x = Center.x + ((Car.OpenGLLocation.x - MyCar.OpenGLLocation.x) * 3);
                            OtherCarLocation.y = Center.y + ((Car.OpenGLLocation.y - MyCar.OpenGLLocation.y) * 3);


                            Log.i(TAG, String.format("OtherCar:- %s", OtherCarLocation.toString()));
                            DrawRectangle(Canvas, OtherCarPaint, OtherCarLocation);
                            if (Car != null && Car.ID != null) {
                                String Msg = String.format("[%s] Located @ (%.2f , %.2f ) Distance = %.3f, Speed = %.2f M/H", Car.ID, Car.Location.x, Car.Location.y, Car.DistanceInMeters, Car.Speed);
                                if (Writer != null) {
                                    Writer.write(String.format("%s:\t%s\n", SimpleDateFormat.format(_Calendar.getTime()), Msg.replace(",", "\t").replace("@", "@\t").replace("=", "=\t")));
                                }
                                Canvas.drawText(Msg, 5, Y, OtherCarPaint);
                            }
                            Y += 20;
                        }
                    }


                    if (MinDistance <= BluetoothChatService.ThreasholdDistance) {
                        Message = String.format("Alert!:- A Car is Closer than %d", BluetoothChatService.ThreasholdDistance);
                        MessageHandler.obtainMessage(Constants.PLAY_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                        BluetoothChatService.LastUpdate = System.currentTimeMillis();
                    } else {

                        Message = String.format("Cars Are Far Enough");
                        MessageHandler.obtainMessage(Constants.STOP_ALERT_SOUND, Message.length(), -1, Message).sendToTarget();
                    }
                    for (Car Car : OutDatedCars) {
                        Cars.remove(Car.ID);
                    }
                    if (Cars.isEmpty()) {
                        // if (BluetoothChatService.AlertIsGoing) {
                        edu.smu.trl.safety.bluetooth.BluetoothChatService.AlertIsGoing = false;
                        //  BluetoothChatArrayAdapter.add("Cars Are Far Enough Now");
                        StopColorAnimation(Layout, ValueAnimator, NormalColor);
                        //   }
                    }
                    OutDatedCars.clear();


                } catch (Exception Exception) {
                    Exception.printStackTrace();

                    Log.e(TAG, "Error", Exception);
                }
                invalidate();
            }
        }

        class TimerTask extends java.util.TimerTask {
            final private MyView MyView;

            public TimerTask(MyView MyView) {
                this.MyView = MyView;
            }

            @Override
            public void run() {
                ViewMessageHandler.obtainMessage(1).sendToTarget();
            }
        }
        // }
    }
}
