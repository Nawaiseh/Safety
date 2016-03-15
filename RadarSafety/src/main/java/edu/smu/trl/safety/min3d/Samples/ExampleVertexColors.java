package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by trl.safety on 3/4/2016.
 */

import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.objectPrimitives.Box;

/**
 * @author Lee
 */
public class ExampleVertexColors extends RendererActivity {
    Object3dContainer _cube;

    public void initScene() {
        /**
         * Rem, the Box class automatically adds vertex colors (a different color for each side).
         */
        _cube = new Box(1, 1, 1);
        _cube.colorMaterialEnabled(true);
        scene.addChild(_cube);

        /**
         * Turn off lighting so that colors come thru "as-is", without any changes in brightness based on
         * any extant lights or light settings.
         */
        scene.lightingEnabled(false);
    }

    @Override
    public void updateScene() {
        _cube.rotation().y += 1;
        _cube.rotation().z += 0.2f;
    }
}
