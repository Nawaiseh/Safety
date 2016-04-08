package edu.smu.trl.safety.min3d.interfaces;

/**
 * Created by TRL on 3/4/2016.
 */

import java.util.TreeMap;

import edu.smu.trl.safety.min3d.core.Object3d;

/**
 * Using Actionscript 3 nomenclature for what are essentially "pass-thru" methods to an underlying ArrayList
 */
public interface IObject3dContainer {
    public void addChild(String $Key, Object3d $child);

    // public void addChildAt(Object3d $child, int $index);

    public void removeChild(String $Key);

    public TreeMap<String, Object3d> children();


    //  public Object3d removeChildAt(int $index);

    //  public Object3d getChildAt(int $index);

    public Object3d getChildByName(String $string);

    // public int getChildIndexOf(Object3d $o);

    public int numChildren();
}
