package edu.smu.trl.safety.min3d.core;

/**
 * Created by TRL on 3/4/2016.
 */


import edu.smu.trl.safety.min3d.vos.Number3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Number3dBufferList {
    public static final int PROPERTIES_PER_ELEMENT = 3;
    public static final int BYTES_PER_PROPERTY = 4;

    private FloatBuffer _FloatBuffer;
    private int _numElements = 0;

    public Number3dBufferList(FloatBuffer $b, int $size) {
        ByteBuffer bb = ByteBuffer.allocateDirect($b.limit() * BYTES_PER_PROPERTY);
        bb.order(ByteOrder.nativeOrder());
        _FloatBuffer = bb.asFloatBuffer();
        _FloatBuffer.put($b);
        _numElements = $size;
    }

    public Number3dBufferList(int $maxElements) {
        int numBytes = $maxElements * PROPERTIES_PER_ELEMENT * BYTES_PER_PROPERTY;
        ByteBuffer bb = ByteBuffer.allocateDirect(numBytes);
        bb.order(ByteOrder.nativeOrder());

        _FloatBuffer = bb.asFloatBuffer();
    }

    /**
     * The number of items in the list.
     */
    public int size() {
        return _numElements;
    }

    /**
     * The _maximum_ number of items that the list can hold, as defined on instantiation.
     * (Not to be confused with the Buffer's capacity)
     */
    public int capacity() {
        return _FloatBuffer.capacity() / PROPERTIES_PER_ELEMENT;
    }

    /**
     * Clear object in preparation for garbage collection
     */
    public void clear() {
        _FloatBuffer.clear();
    }

    //

    public Number3d getAsNumber3d(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        return new Number3d(_FloatBuffer.get(), _FloatBuffer.get(), _FloatBuffer.get());
    }

    public void putInNumber3d(int $index, Number3d $number3d) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        $number3d.x = _FloatBuffer.get();
        $number3d.y = _FloatBuffer.get();
        $number3d.z = _FloatBuffer.get();
    }

    public float getPropertyX(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        return _FloatBuffer.get();
    }

    public float getPropertyY(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 1);
        return _FloatBuffer.get();
    }

    public float getPropertyZ(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 2);
        return _FloatBuffer.get();
    }

    //

    public void add(Number3d $n) {
        set(_numElements, $n);
        _numElements++;
    }

    public void add(float $x, float $y, float $z) {
        set(_numElements, $x, $y, $z);
        _numElements++;
    }

    public void set(int $index, Number3d $n) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put((float) $n.x);
        _FloatBuffer.put((float) $n.y);
        _FloatBuffer.put((float) $n.z);
    }

    public void set(int $index, float $x, float $y, float $z) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put((float) $x);
        _FloatBuffer.put((float) $y);
        _FloatBuffer.put((float) $z);
    }

    public void setPropertyX(int $index, float $x) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put($x);
    }

    public void setPropertyY(int $index, float $y) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 1);
        _FloatBuffer.put($y);
    }

    public void setPropertyZ(int $index, float $z) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 2);
        _FloatBuffer.put($z);
    }

    //

    public FloatBuffer buffer() {
        return _FloatBuffer;
    }

    public void overwrite(float[] $newVals) {
        _FloatBuffer.position(0);
        _FloatBuffer.put($newVals);
    }

    public Number3dBufferList clone() {
        _FloatBuffer.position(0);
        Number3dBufferList c = new Number3dBufferList(_FloatBuffer, size());
        return c;
    }
}