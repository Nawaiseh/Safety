package edu.smu.trl.safety.min3d;


import android.content.Context;
import edu.smu.trl.safety.min3d.core.Renderer;
import edu.smu.trl.safety.min3d.core.TextureManager;

/**
 * Holds static references to TextureManager, RendererActivity, and the application Context.
 */
public class Shared {
    private static Context _context;
    private static Renderer _renderer;
    private static TextureManager _textureManager;


    public static Context context() {
        return _context;
    }

    public static void context(Context $c) {
        _context = $c;
    }

    public static Renderer renderer() {
        return _renderer;
    }

    public static void renderer(Renderer $r) {
        _renderer = $r;
    }

    /**
     * You must access the TextureManager instance through this accessor
     */
    public static TextureManager textureManager() {
        return _textureManager;
    }

    public static void textureManager(TextureManager $bm) {
        _textureManager = $bm;
    }
}