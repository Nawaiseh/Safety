package edu.smu.trl.safety.min3d.core;

/**
 * Created by TRL on 3/4/2016.
 */


import android.util.Log;

import java.util.TreeMap;

import edu.smu.trl.safety.min3d.Min3d;
import edu.smu.trl.safety.min3d.interfaces.IDirtyParent;
import edu.smu.trl.safety.min3d.interfaces.IObject3dContainer;
import edu.smu.trl.safety.min3d.interfaces.ISceneController;
import edu.smu.trl.safety.min3d.vos.CameraVo;
import edu.smu.trl.safety.min3d.vos.Color4;
import edu.smu.trl.safety.min3d.vos.Color4Managed;
import edu.smu.trl.safety.min3d.vos.FogType;


public class Scene implements IObject3dContainer, IDirtyParent {
    private TreeMap<String, Object3d> _children = new TreeMap<>();

    private ManagedLightList _lights;
    private CameraVo _camera;

    private Color4Managed _backgroundColor;
    private boolean _lightingEnabled;

    private Color4 _fogColor;
    private float _fogFar;
    private float _fogNear;
    private FogType _fogType;
    private boolean _fogEnabled;

    private ISceneController _sceneController;


    public Scene(ISceneController $sceneController) {
        _sceneController = $sceneController;
        _lights = new ManagedLightList();
        _fogColor = new Color4(255, 255, 255, 255);
        _fogNear = 0;
        _fogFar = 10;
        _fogType = FogType.LINEAR;
        _fogEnabled = false;
    }

    /**
     * Allows you to use any Class implementing ISceneController
     * to drive the Scene...
     *
     * @return
     */
    public ISceneController sceneController() {
        return _sceneController;
    }

    public void sceneController(ISceneController $sceneController) {
        _sceneController = $sceneController;
    }

    //

    /**
     * Resets Scene to default settings.
     * Removes and clears any attached Object3ds.
     * Resets light list.
     */
    public void reset() {
        clearChildren(this);

        _children = new TreeMap<>();

        _camera = new CameraVo();

        _backgroundColor = new Color4Managed(0, 0, 0, 255, this);

        _lights = new ManagedLightList();

        lightingEnabled(true);
    }

    /**
     * Adds Object3d to Scene. Object3d's must be added to Scene in order to be rendered
     * Returns always true.
     */
    public void addChild(String $Key, Object3d $child) {
        try {
            if (_children.containsKey($Key)) return;
            _children.put($Key, $child);
            $child.parent(this);
            $child.scene(this);
        } catch (Exception Exception) {
            int x = 0;
        }
    }

 /*   public void addChildAt(Object3d $o, int $index) {
        if (_children.contains($o)) return;

        _children.add($index, $o);
    }*/

    /**
     * Removes Object3d from Scene.
     * Returns false if unsuccessful
     */
    public void removeChild(String $Key) {
        try {
            Object3d $O = _children.get($Key);
            if ($O != null) {
                _children.get($Key).parent(null);
                _children.get($Key).scene(null);
                _children.remove($Key);
            }
        } catch (Exception Exception) {
            int x = 0;
        }
    }

/*    public Object3d removeChildAt(int $index) {
        Object3d o = _children.remove($index);

        if (o != null) {
            o.parent(null);
            o.scene(null);
        }
        return o;
    }*/

    // public Object3d getChildAt(int $index) {
    //    return _children.get($index);
    //}

    /**
     * TODO: Use better lookup
     */
    public Object3d getChildByName(String $name) {

        try {
            return _children.get($name);
        } catch (Exception Exception) {
            int x = 0;
        }
        return null;

       /* for (int i = 0; i < _children.size(); i++) {
            if (_children.get(0).name() == $name) return _children.get(0);
        }
        return null;*/
    }

  /*  public int getChildIndexOf(Object3d $o) {
        return _children.indexOf($o);
    }*/

    public int numChildren() {
        return _children.size();
    }

    /**
     * Scene's camera
     */
    public CameraVo camera() {
        return _camera;
    }

    public void camera(CameraVo $camera) {
        _camera = $camera;
    }

    /**
     * Scene instance's background color
     */
    public Color4Managed backgroundColor() {
        return _backgroundColor;
    }

    /**
     * Lights used by the Scene
     */
    public ManagedLightList lights() {
        return _lights;
    }

    /**
     * Determines if lighting is enabled for Scene.
     */
    public boolean lightingEnabled() {
        return _lightingEnabled;
    }

    public void lightingEnabled(boolean $b) {
        _lightingEnabled = $b;
    }

    //

	/*
    public boolean backgroundTransparent() {
		return _backgroundTransparent;
	}
	public void backgroundTransparent(boolean backgroundTransparent) {
		this._backgroundTransparent = backgroundTransparent;
	}
	*/

    public Color4 fogColor() {
        return _fogColor;
    }

    public void fogColor(Color4 _fogColor) {
        this._fogColor = _fogColor;
    }

    public float fogFar() {
        return _fogFar;
    }

    public void fogFar(float _fogFar) {
        this._fogFar = _fogFar;
    }

    public float fogNear() {
        return _fogNear;
    }

    public void fogNear(float _fogNear) {
        this._fogNear = _fogNear;
    }

    public FogType fogType() {
        return _fogType;
    }

    public void fogType(FogType _fogType) {
        this._fogType = _fogType;
    }

    public boolean fogEnabled() {
        return _fogEnabled;
    }

    public void fogEnabled(boolean _fogEnabled) {
        this._fogEnabled = _fogEnabled;
    }

    /**
     * Used by RendererActivity
     */
    void init() /*package-private*/ {
        Log.i(Min3d.TAG, "Scene.init()");

        this.reset();

        _sceneController.initScene();
        _sceneController.getInitSceneHandler().post(_sceneController.getInitSceneRunnable());
    }

    void update() {
        _sceneController.updateScene();
        _sceneController.getUpdateSceneHandler().post(_sceneController.getUpdateSceneRunnable());
    }

    /**
     * Used by RendererActivity
     */
    public TreeMap<String, Object3d> children() /*package-private*/ {
        return _children;
    }

    private void clearChildren(IObject3dContainer $c) {
        try {
            for (Object3d Object3d : $c.children().values()) {
                Object3d.clear();
                if (Object3d instanceof Object3dContainer) {
                    clearChildren((Object3dContainer) Object3d);
                }
            }
        } catch (Exception Exception) {
            int x = 0;
        }

    }

    public void onDirty() {
        //
    }
}