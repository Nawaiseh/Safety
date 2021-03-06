package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by TRL on 3/4/2016.
 */


import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.parser.IParser;
import edu.smu.trl.safety.min3d.parser.Parser;
import edu.smu.trl.safety.min3d.vos.Light;

public class ExampleLoad3DSFile extends RendererActivity {
    private final float CAM_RADIUS_X = 20;
    private final float CAM_RADIUS_Y = 15;
    private final float CAM_RADIUS_Z = 30;
    private final float ROTATION_SPEED = 1;
    private Object3dContainer monster;
    private float degrees;

    @Override
    public void initScene() {

        scene.lights().add(new Light());

        IParser parser = Parser.createParser(Parser.Type.MAX_3DS,
                getResources(), "edu.smu.trl.safety.radarsafety:raw/monster_high", false);
        parser.parse();

        monster = parser.getParsedObject();
        monster.scale().x = monster.scale().y = monster.scale().z = .5f;
        monster.position().y = -10;
        scene.addChild("Monster", monster);

        scene.camera().target = monster.position();
    }

    @Override
    public void updateScene() {
        float radians = degrees * ((float) Math.PI / 180);

        scene.camera().position.x = (float) Math.cos(radians) * CAM_RADIUS_X;
        scene.camera().position.y = (float) Math.sin(radians) * CAM_RADIUS_Y;
        scene.camera().position.z = (float) Math.sin(radians) * CAM_RADIUS_Z;

        degrees += ROTATION_SPEED;
    }
}
