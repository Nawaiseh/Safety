package edu.smu.trl.safety.min3d;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import edu.smu.trl.safety.min3d.core.Object3d;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Utils {
    public static final float DEG = (float) (Math.PI / 180F);

    private static final int BYTES_PER_FLOAT = 4;

    /**
     * Convenience method to create a Bitmap given a Context's drawable resource ID.
     */
    public static Bitmap makeBitmapFromResourceId(Context $context, int $id) {
        InputStream is = $context.getResources().openRawResource($id);

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore.
            }
        }

        return bitmap;
    }

    /**
     * Convenience method to create a Bitmap given a drawable resource ID from the application Context.
     */
    public static Bitmap makeBitmapFromResourceId(int $id) {
        return makeBitmapFromResourceId(Shared.context(), $id);
    }

    /**
     * Add two triangles to the Object3d's faces using the supplied indices
     */
    public static void addQuad(Object3d $o, int $upperLeft, int $upperRight, int $lowerRight, int $lowerLeft) {
        $o.faces().add((short) $upperLeft, (short) $lowerRight, (short) $upperRight);
        $o.faces().add((short) $upperLeft, (short) $lowerLeft, (short) $lowerRight);
    }

    public static FloatBuffer makeFloatBuffer3(double $a, double $b, double $c) {
        ByteBuffer b = ByteBuffer.allocateDirect(3 * BYTES_PER_FLOAT);
        b.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put((float) $a);
        buffer.put((float) $b);
        buffer.put((float) $c);
        buffer.position(0);
        return buffer;
    }

    public static FloatBuffer makeFloatBuffer4(double $a, double $b, double $c, double $d) {
        ByteBuffer b = ByteBuffer.allocateDirect(4 * BYTES_PER_FLOAT);
        b.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put((float) $a);
        buffer.put((float) $b);
        buffer.put((float) $c);
        buffer.put((float) $d);
        buffer.position(0);
        return buffer;
    }
}