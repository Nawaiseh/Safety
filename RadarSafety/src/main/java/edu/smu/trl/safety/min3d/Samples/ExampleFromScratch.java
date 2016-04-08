package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by TRL on 3/4/2016.
 */

import edu.smu.trl.safety.min3d.Utils;
import edu.smu.trl.safety.min3d.core.Object3d;
import edu.smu.trl.safety.min3d.core.RendererActivity;

/**
 * XXXXXXXXXXXXXXXXXXX NOT COMPLETE XXXXXXXXXXXXXXXXXXX
 * Example of creating an object from scratch.
 * <p/>
 * In the real world, you'd do this synchronously, not on-enter-frame.
 * <p/>
 * I'm building the object frame-by-frame in part to demonstrate how
 * adding vertices post-construction is not bad for performance.
 * <p/>
 * Also note that based on the shape of the object made here, it'd be
 * optimal to the faces to share vertices, which I'm not bothering
 * with here...
 *
 * @author Lee
 */
public class ExampleFromScratch extends RendererActivity {
    int _count;
    Object3d _currentObject;

    public void initScene() {
        _count = 0;
    }

    @Override
    public void updateScene() {
        if (_count % 500 == 0) {
            _currentObject = new Object3d(500, 500);
            _currentObject.normalsEnabled(false);
            _currentObject.vertexColorsEnabled(true);
            _currentObject.doubleSidedEnabled(true);
            scene.addChild(String.format("Object[%d]", _count), _currentObject);
        }

        addToObject();
    }

    private void addToObject() {
        float count = _count % 500;
        float rad = 0.5f + (count / 500f) * 1.0f;
        float x1 = (float) (Math.sin(count * Utils.DEG * 3) * rad);
        float z1 = (float) (Math.cos(count * Utils.DEG * 3) * rad);
        float y1 = count / 500f;

        // _currentObject.vertices().addVertex($pointX, $pointY, $pointZ, $textureU, $textureV, $normalX, $normalY, $normalZ, $colorR, $colorG, $colorB, $colorA);

        // .. not bothering with U/V info
    }
}