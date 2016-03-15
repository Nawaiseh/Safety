package edu.smu.trl.safety.min3d.vos;


/**
 * Simple VO used for texture positioning
 */
public class Uv {
    public double u;
    public double v;

    public Uv() {
        u = 0;
        v = 0;
    }

    public Uv(double $u, double $v) {
        u = $u;
        v = $v;
    }

    public Uv clone() {
        return new Uv(u, v);
    }
    // Rem, v == 0 @ 'bottom', v == 1 @ 'top'
}