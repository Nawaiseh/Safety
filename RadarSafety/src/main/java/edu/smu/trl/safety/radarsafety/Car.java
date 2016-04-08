package edu.smu.trl.safety.radarsafety;


import android.graphics.Paint;

import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.Renderer;
import edu.smu.trl.safety.min3d.vos.Color4;
import edu.smu.trl.safety.min3d.vos.Number3d;
import edu.smu.trl.safety.utilities.LatLonToXY;
import edu.smu.trl.safety.utilities.LatLonToXY.Unit;
import edu.smu.trl.safety.utilities.Log;

/**
 * Created by TRL on 3/15/2016.
 */
public class Car {

    private static final String TAG = "BluetoothChatService";
    private static final char[] HEX_SYMBOLS = "0123456789ABCDEF".toCharArray();
    public float DistanceInMeters = 0;
    public long LastUpdated = System.currentTimeMillis();
    public String ID = null;
    public float Latitude = 0;
    public float Longitude = 0;
    public float Altitude = 0;
    public Number3d Location = new Number3d(0, 0, 0);
    public Number3d OpenGLLocation = new Number3d(0, 0, 0);
    public Number3d OpenGLRotation = new Number3d(0, 0, 0);
    public Number3d LatLongAltLocation = new Number3d(0, 0, 0);
    public float Speed = 0;
    public float Direction = 0;
    public float Width = 0;
    public float Length = 0;
    public float Height = 0;
    public CarType Type = CarType.OtherCar;
    public Object3dContainer OpenGLCar = null;
    private Paint Paint = new Paint();
    private Color4 Color = new Color4(1F, 0, 0, 0);

    public Car() {
        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }

        Paint.setTextSize(32);
        Paint.setAntiAlias(true);
        Paint.setARGB(0xff, 0x00, 0x00, 0x00);

    }


    public Car(String ID, float Latitude, float Longitude, float Altitude, float Speed, float Direction, float Width, float Length, float Height) {
        this.ID = ID;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Altitude = Altitude;

        LatLongAltLocation.x = this.Longitude;
        LatLongAltLocation.y = this.Latitude;
        LatLongAltLocation.z = this.Altitude;

        Location.x = (float) LatLonToXY.LatLonToXYInMiles(0, Longitude, 0, 0, Unit.Mile);
        Location.y = (float) LatLonToXY.LatLonToXYInMiles(Latitude, 0, 0, 0, Unit.Mile);
        Location.z = this.Altitude;

        this.Speed = Speed;
        this.Direction = Direction;
        this.Width = Width;
        this.Length = Length;
        this.Height = Height;
        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public Car(byte[] Blob) {
        UpdatePosition(this, Blob);
        UpdateSpeed(this, Blob);
        UpdateDirection(this, Blob);
        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public static float ConvertDistance(float Distance, DistanceUnit DistanceUnit) {

        // 1 Inch =   0.000016 Mile
        float InchRatio = 0.000016F;
        float MeterRatio = 0.000621371F;
        float KiloMeterRatio = 0.621371F;
        switch (DistanceUnit) {
            case Inches:
                return (Distance / InchRatio);
            case Meters:
                return (Distance / MeterRatio);
            case KiloMeters:
                return (Distance / KiloMeterRatio);
            default:
                return Distance;
        }
    }

    private static void UpdateID(Car Car, byte[] Blob) {
        //  Car.ID = BytesToHEX(Blob, ((Car.Type == CarType.MyCar) ? 2 : 42), 4).trim();
        Car.ID = BytesToHEX(Blob, ((Car.Type == CarType.MyCar) ? 1 : 39), 4).trim();
    }

    private static void UpdatePosition(Car Car, byte[] Blob) {
        String Latitude_Str = BytesToHEX(Blob, 7 + ((Car.Type == CarType.MyCar) ? 0 : 38), 4).trim();
        String LongitudeStr = BytesToHEX(Blob, 11 + ((Car.Type == CarType.MyCar) ? 0 : 38), 4).trim();
        String AltitudeStr = BytesToHEX(Blob, 15 + ((Car.Type == CarType.MyCar) ? 0 : 38), 2).trim();

        Car.Latitude = Long.parseLong(Latitude_Str, 16);
        Car.Longitude = Long.parseLong(LongitudeStr, 16);
        Car.Altitude = Long.parseLong(AltitudeStr, 16);


        float N1 = 2147483647L;
        float N2 = 4294967296L;


        Car.Latitude = (Car.Latitude > N1) ? (Car.Latitude - N2) : Car.Latitude;
        Car.Longitude = (Car.Longitude > N1) ? (Car.Longitude - N2) : Car.Longitude;

        Car.Latitude = Car.Latitude / 10000000F;
        Car.Longitude = Car.Longitude / 10000000F;
        Car.Altitude = Car.Altitude / 10F;

        Car.LatLongAltLocation.x = Car.Longitude;
        Car.LatLongAltLocation.y = Car.Latitude;
        Car.LatLongAltLocation.z = Car.Altitude;


        Car.Location.x = (float) LatLonToXY.LatLonToXYInMiles(0, Car.Longitude, 0, 0, Unit.Mile);
        Car.Location.y = (float) LatLonToXY.LatLonToXYInMiles(Car.Latitude, 0, 0, 0, Unit.Mile);
        Car.Location.z = Car.Altitude;

    }

    private static void UpdateSpeed(Car Car, byte[] Blob) {
        String SpeedStr = BytesToHEX(Blob, 21 + ((Car.Type == CarType.MyCar) ? 0 : 38), 2).trim();
        Car.Speed = Long.parseLong(SpeedStr, 16);
        Car.Speed = Car.Speed * 0.02F;
    }

    private static void UpdateDirection(Car Car, byte[] Blob) {
        String DirectionStr = BytesToHEX(Blob, 25 + ((Car.Type == CarType.MyCar) ? 0 : 38), 1).trim();
        Car.Direction = Long.parseLong(DirectionStr, 16);
        Car.Direction = Car.Direction * 1.5F;

    }

    private static String BytesToHEXWithGaps(byte[] Blob, int Start, int Count) {
        int End = Start + Count;

        char[] HexBuffer = new char[Count * 3];
        int ByteIndex = 0;
        for (int j = Start; j < End; j++) {
            int v = Blob[j] & 0xFF;
            HexBuffer[ByteIndex] = HEX_SYMBOLS[v >>> 4];
            ByteIndex++;
            HexBuffer[ByteIndex] = HEX_SYMBOLS[v & 0x0F];
            ByteIndex++;
            HexBuffer[ByteIndex] = ' ';
            ByteIndex++;
        }
        return new String(HexBuffer).trim();
    }

    public static String BytesToHEX(byte[] Blob, int Start, int Count) {
        int End = Start + Count;

        char[] HexBuffer = new char[Count * 2];
        int ByteIndex = 0;
        for (int j = Start; j < End; j++) {
            int v = Blob[j] & 0xFF;
            HexBuffer[ByteIndex] = HEX_SYMBOLS[v >>> 4];
            ByteIndex++;
            HexBuffer[ByteIndex] = HEX_SYMBOLS[v & 0x0F];
            ByteIndex++;
        }
        return new String(HexBuffer).trim();
    }

    public void SetData(String ID, float Latitude, float Longitude, float Altitude, float Speed, float Direction, float Width, float Length, float Height) {
        this.ID = ID;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Altitude = Altitude;

        LatLongAltLocation.x = this.Longitude;
        LatLongAltLocation.y = this.Latitude;
        LatLongAltLocation.z = this.Altitude;

        Location.x = (float) LatLonToXY.LatLonToXYInMiles(0, Longitude, 0, 0, Unit.Mile);
        Location.y = (float) LatLonToXY.LatLonToXYInMiles(Latitude, 0, 0, 0, Unit.Mile);
        Location.z = this.Altitude;

        this.Speed = Speed;
        this.Direction = Direction;
        this.Width = Width;
        this.Length = Length;
        this.Height = Height;
        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public void SetData(Car Car) {

        this.ID = Car.ID;
        this.Latitude = Car.Latitude;
        this.Longitude = Car.Longitude;
        this.Altitude = Car.Altitude;

        LatLongAltLocation.x = Car.LatLongAltLocation.x;
        LatLongAltLocation.y = Car.LatLongAltLocation.y;
        LatLongAltLocation.z = Car.LatLongAltLocation.z;

        Location.x = Car.Location.x;
        Location.y = Car.Location.y;
        Location.z = Car.Location.z;

        this.Speed = Car.Speed;
        this.Direction = Car.Direction;
        this.Width = Car.Width;
        this.Length = Car.Length;
        this.Height = Car.Height;

        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public void SetData(byte[] Blob) {
        UpdateID(this, Blob);
        UpdatePosition(this, Blob);
        UpdateSpeed(this, Blob);
        UpdateDirection(this, Blob);

        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public void SetData(byte[] buffer, int NumberOfBytes) {

        String LocalGPSData = new String(buffer, 38, NumberOfBytes);

        String[] DualTokens = LocalGPSData.split(",");
        for (String DualToken : DualTokens) {

            try {
                if (DualToken.split(":").length < 2) {
                    continue;
                }
                String Token1 = DualToken.split(":")[0].trim();
                String Token2 = DualToken.split(":")[1].trim();
                switch (Token1) {
                    case "MyID":
                        if (ID == "") {
                            ID = Token2;
                        }
                        break;
                    case "Latitude":
                        Latitude = Float.parseFloat(Token2);
                        LatLongAltLocation.y = Latitude;
                        Location.y = (float) LatLonToXY.LatLonToXYInMiles(Latitude, 0, 0, 0, Unit.Mile);
                        break;
                    case "Longitude":
                        Longitude = Float.parseFloat(Token2);
                        LatLongAltLocation.x = Longitude;
                        Location.x = (float) LatLonToXY.LatLonToXYInMiles(0, Longitude, 0, 0, Unit.Mile);
                        break;
                    case "Altitude":
                        Altitude = Float.parseFloat(Token2);
                        LatLongAltLocation.z = Altitude;
                        Location.z = this.Altitude;
                        break;
                    case "Speed":
                        Speed = Float.parseFloat(Token2);
                        break;
                    case "Direction":
                        Direction = Float.parseFloat(Token2);
                        break;
                    default:
                        Log.e(TAG, "Wrong Local Data Received From Bluetooth Device");
                        break;
                }

            } catch (Exception Exception) {
                Log.e(TAG, Exception.getMessage(), Exception);
            }
        }
        synchronized (this) {
            LastUpdated = System.currentTimeMillis();
        }
    }

    public float DistanceFrom(Car Car) {
        float DX_2 = (Location.x - Car.Location.x);
        DX_2 = DX_2 * DX_2;
        float DY_2 = (Location.y - Car.Location.y);
        DY_2 = DY_2 * DY_2;
        return (float) Math.sqrt(DX_2 + DY_2);


    }

    public float DistanceFrom(Car Car, DistanceUnit DistanceUnit) {
        float DX = ConvertDistance((Location.x - Car.Location.x), DistanceUnit);
        float DX_2 = DX * DX;
        float DY = ConvertDistance((Location.y - Car.Location.y), DistanceUnit);
        float DY_2 = DY * DY;
        OpenGLLocation.x = DX;
        OpenGLLocation.y = DY;
        OpenGLRotation.z = (Direction - Car.Direction);
        return (float) Math.sqrt(DX_2 + DY_2);

    }

    public String Position(PositionType PositionType) {
        switch (PositionType) {
            case Latitude:
                return String.format("(%.2f)", Latitude);
            case Longitude:
                return String.format("(%.2f)", Longitude);
            case Altitude:
                return String.format("(%.2f)", Altitude);
            case NoLatitude:
                return String.format("(%.2f,%.2f)", Longitude, Altitude);
            case NoLongitude:
                return String.format("(%.2f,%.2f)", Latitude, Altitude);
            case NoAltitude:
                return String.format("(%.2f,%.2f)", Latitude, Longitude);
            case All:
            default:
                return String.format("(%.2f,%.2f,%.2f)", Latitude, Longitude, Altitude);
        }
    }

    public void DisplayInfo(Renderer Renderer) {
        String Info = "Hello World";
        Renderer.DisplayText(Info, Location, Color);
    }

    public enum CarType {OtherCar, MyCar}

    public enum PositionType {Latitude, Longitude, Altitude, NoLatitude, NoLongitude, NoAltitude, All}

    public enum DistanceUnit {Miles, Meters, KiloMeters, Inches}
}
