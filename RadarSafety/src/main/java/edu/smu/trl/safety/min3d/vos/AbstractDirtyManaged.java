package edu.smu.trl.safety.min3d.vos;

import edu.smu.trl.safety.min3d.interfaces.IDirtyManaged;
import edu.smu.trl.safety.min3d.interfaces.IDirtyParent;


public abstract class AbstractDirtyManaged implements IDirtyManaged {
    protected IDirtyParent _parent;
    protected boolean _dirty;

    public AbstractDirtyManaged(IDirtyParent $parent) {
        _parent = $parent;
    }

    public boolean isDirty() {
        return _dirty;
    }

    public void setDirtyFlag() {
        _dirty = true;
        if (_parent != null) _parent.onDirty();
    }

    public void clearDirtyFlag() {
        _dirty = false;
    }
}