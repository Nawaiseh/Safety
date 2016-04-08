package edu.smu.trl.safety.min3d.core;

/**
 * Created by TRL on 3/4/2016.
 */

import java.util.TreeMap;

import edu.smu.trl.safety.min3d.interfaces.IObject3dContainer;

public class Object3dContainer extends Object3d implements IObject3dContainer {
    protected TreeMap<String, Object3d> _children = new TreeMap<String, Object3d>();

    public Object3dContainer() {
        super(0, 0, false, false, false);
    }

    /**
     * Adds container functionality to Object3d.
     * <p/>
     * Subclass Object3dContainer instead of Object3d if you
     * believe you may want to add children to that object.
     */
    public Object3dContainer(int $maxVerts, int $maxFaces) {
        super($maxVerts, $maxFaces, true, true, true);
    }

    public Object3dContainer(int $maxVerts, int $maxFaces, Boolean $useUvs, Boolean $useNormals, Boolean $useVertexColors) {
        super($maxVerts, $maxFaces, $useUvs, $useNormals, $useVertexColors);
    }

    /**
     * This constructor is convenient for cloning purposes
     */
    public Object3dContainer(Vertices $vertices, FacesBufferedList $faces, TextureList $textures) {
        super($vertices, $faces, $textures);
    }

    public void addChild(String $Key, Object3d $o) {
        _children.put($Key, $o);

        $o.parent(this);
        $o.scene(this.scene());
    }


    public void removeChild(String $Key) {
        Object3d b = _children.remove($Key);

        if (b != null) {
            b.parent(null);
            b.scene(null);
        }
    }

    public Object3d removeChildAt(int $index) {
        Object3d o = _children.remove($index);
        if (o != null) {
            o.parent(null);
            o.scene(null);
        }
        return o;
    }

    public Object3d getChildAt(int $index) {
        return _children.get($index);
    }

    /**
     * TODO: Use better lookup
     */
    public Object3d getChildByName(String $name) {
        try {
            return _children.get($name);
        } catch (Exception Exception) {
            int x = 0;
        }

  /*  for (int i = 0; i < _children.size(); i++) {
            if (_children.get(i).name().equals($name)) return _children.get(i);
        }*/
        return null;
    }

    //public int getChildIndexOf(Object3d $o) {
    //  return _children.indexOf($o);
    // }


    public int numChildren() {
        return _children.size();
    }

    /*package-private*/
    public TreeMap<String, Object3d> children() {
        return _children;
    }

    public Object3dContainer clone() {
        Vertices v = _vertices.clone();
        FacesBufferedList f = _faces.clone();

        Object3dContainer clone = new Object3dContainer(v, f, _textures);

        clone.position().x = position().x;
        clone.position().y = position().y;
        clone.position().z = position().z;

        clone.rotation().x = rotation().x;
        clone.rotation().y = rotation().y;
        clone.rotation().z = rotation().z;

        clone.scale().x = scale().x;
        clone.scale().y = scale().y;
        clone.scale().z = scale().z;

        for (String Key : this.children().keySet()) {
            clone.addChild(Key, children().get(Key));
        }
        return clone;
    }

}