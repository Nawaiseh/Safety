package edu.smu.trl.safety.min3d.core;

/**
 * Created by TRL on 3/4/2016.
 */

import edu.smu.trl.safety.min3d.vos.Uv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class UvBufferList {
    public static final int PROPERTIES_PER_ELEMENT = 2;
    public static final int BYTES_PER_PROPERTY = 4;

    private FloatBuffer _FloatBuffer;
    private int _numElements = 0;

    public UvBufferList(FloatBuffer $b, int $size) {
        ByteBuffer bb = ByteBuffer.allocateDirect($b.limit() * BYTES_PER_PROPERTY);
        bb.order(ByteOrder.nativeOrder());
        _FloatBuffer = bb.asFloatBuffer();
        _FloatBuffer.put($b);
        _numElements = $size;
    }

    public UvBufferList(int $maxElements) {
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

    public Uv getAsUv(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        return new Uv(_FloatBuffer.get(), _FloatBuffer.get());
    }

    public void putInUv(int $index, Uv $uv) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        $uv.u = _FloatBuffer.get();
        $uv.v = _FloatBuffer.get();
    }

    public double getPropertyU(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        return _FloatBuffer.get();
    }

    public double getPropertyV(int $index) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 1);
        return _FloatBuffer.get();
    }

    //

    public void add(Uv $uv) {
        set(_numElements, $uv);
        _numElements++;
    }

    public void add(double $u, double $v) {
        set(_numElements, $u, $v);
        _numElements++;
    }

    public void set(int $index, Uv $uv) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put((float) $uv.u);
        _FloatBuffer.put((float) $uv.v);
    }

    public void set(int $index, double $u, double $v) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put((float) $u);
        _FloatBuffer.put((float) $v);
    }

    public void setPropertyU(int $index, float $u) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT);
        _FloatBuffer.put($u);
    }

    public void setPropertyV(int $index, float $v) {
        _FloatBuffer.position($index * PROPERTIES_PER_ELEMENT + 1);
        _FloatBuffer.put($v);
    }

    //

    public FloatBuffer FloatBuffer() {
        return _FloatBuffer;
    }

    public UvBufferList clone() {
        _FloatBuffer.position(0);
        UvBufferList c = new UvBufferList(_FloatBuffer, size());
        return c;
    }
}