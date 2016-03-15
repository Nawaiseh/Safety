package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by TRL on 3/4/2016.
 */


import edu.smu.trl.safety.min3d.animation.AnimationObject3d;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.parser.IParser;
import edu.smu.trl.safety.min3d.parser.Parser;
import edu.smu.trl.safety.min3d.vos.Light;

public class ExampleLoadMD2File extends RendererActivity {
    private AnimationObject3d ogre;

    @Override
    public void initScene() {

        scene.lights().add(new Light());

        IParser parser = Parser.createParser(Parser.Type.MD2,
                getResources(), "edu.smu.trl.safety.radarsafety:raw/ogro", false);
        parser.parse();

        ogre = parser.getParsedAnimationObject();
        ogre.scale().x = ogre.scale().y = ogre.scale().z = .07f;
        ogre.rotation().z = -90;
        ogre.rotation().x = -90;
        scene.addChild(ogre);
        ogre.setFps(70);
        ogre.play();
    }

    @Override
    public void updateScene() {

    }

}