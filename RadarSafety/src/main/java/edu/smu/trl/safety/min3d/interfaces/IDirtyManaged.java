package edu.smu.trl.safety.min3d.interfaces;

/**
 * Created by TRL on 3/4/2016.
 */
public interface IDirtyManaged {
    public boolean isDirty();

    public void setDirtyFlag();

    public void clearDirtyFlag();
}