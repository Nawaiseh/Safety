package edu.smu.trl.safety.min3d.vos;

/**
 * Created by TRL on 3/4/2016.
 */

import edu.smu.trl.safety.min3d.Utils;
import edu.smu.trl.safety.min3d.interfaces.IDirtyParent;

import java.nio.FloatBuffer;

/**
 * 'Managed' version of Number3d VO
 */
public class Number3dManaged extends AbstractDirtyManaged {
    private float _x;
    private float _y;
    private float _z;

    private FloatBuffer _FloatBuffer;


    public Number3dManaged(IDirtyParent $parent) {
        super($parent);
        _x = 0;
        _y = 0;
        _z = 0;
        _FloatBuffer = this.toFloatBuffer();
        setDirtyFlag();
    }

    public Number3dManaged(float $x, float $y, float $z, IDirtyParent $parent) {
        super($parent);
        _x = $x;
        _y = $y;
        _z = $z;
        _FloatBuffer = this.toFloatBuffer();
        setDirtyFlag();
    }

    public float getX() {
        return _x;
    }

    public void setX(float x) {
        _x = x;
        setDirtyFlag();
    }

    public float getY() {
        return _y;
    }

    public void setY(float y) {
        _y = y;
        setDirtyFlag();
    }

    public float getZ() {
        return _z;
    }

    public void setZ(float z) {
        _z = z;
        setDirtyFlag();
    }

    public void setAll(float $x, float $y, float $z) {
        _x = $x;
        _y = $y;
        _z = $z;
        setDirtyFlag();
    }

    public void setAllFrom(Number3d $n) {
        _x = $n.x;
        _y = $n.y;
        _z = $n.z;
        setDirtyFlag();
    }

    public void setAllFrom(Number3dManaged $n) {
        _x = $n.getX();
        _y = $n.getY();
        _z = $n.getZ();
        setDirtyFlag();
    }

    public Number3d toNumber3d() {
        return new Number3d(_x, _y, _z);
    }

    @Override
    public String toString() {
        return _x + "," + _y + "," + _z;
    }

    /**
     * Convenience method
     */

    public FloatBuffer toFloatBuffer() {
        return Utils.makeFloatBuffer3(_x, _y, _z);
    }

    /**
     * Convenience method
     */


    public void toFloatBuffer(FloatBuffer $FloatBuffer) {
        $FloatBuffer.position(0);
        $FloatBuffer.put((float) _x);
        $FloatBuffer.put((float) _y);
        $FloatBuffer.put((float) _z);
        $FloatBuffer.position(0);
    }

    /**
     * Used by RendererActivity
     */
    public FloatBuffer FloatBuffer() {
        return _FloatBuffer;
    }

    /**
     * Used by RendererActivity
     */
    public void commitToFloatBuffer() {
        toFloatBuffer(_FloatBuffer);
    }
}
