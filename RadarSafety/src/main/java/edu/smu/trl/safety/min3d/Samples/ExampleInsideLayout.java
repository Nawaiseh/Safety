package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by TRL on 3/4/2016.
 */


import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.objectPrimitives.Box;
import edu.smu.trl.safety.min3d.vos.Light;
import edu.smu.trl.safety.radarsafety.R;

/**
 * Example of adding an OpenGL scene within a conventional Android application layout.
 * Entails overriding RenderActivity's onCreateSetContentView() function, and
 * adding _glSurfaceView to the appropriate View...
 *
 * @author Lee
 */
public class ExampleInsideLayout extends RendererActivity implements View.OnClickListener {
    Object3dContainer _cube;

    @Override
    protected void onCreateSetContentView() {
        setContentView(R.layout.custom_layout_example);

        LinearLayout ll = (LinearLayout) this.findViewById(R.id.scene1Holder);
        ll.addView(_glSurfaceView);

        Button b;
        b = (Button) this.findViewById(R.id.layoutOkay);
        b.setOnClickListener(this);
        b = (Button) this.findViewById(R.id.layoutCancel);
        b.setOnClickListener(this);
    }

    public void onClick(View $v) {
        finish();
    }

    //

    public void initScene() {
        scene.lights().add(new Light());

        scene.backgroundColor().setAll(0xff444444);
        _cube = new Box(1, 1, 1);
        scene.addChild(_cube);
    }

    @Override
    public void updateScene() {
        _cube.rotation().y++;
    }

}