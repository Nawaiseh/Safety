package edu.smu.trl.safety.min3d.Samples;

/**
 * Created by TRL on 3/4/2016.
 */


import edu.smu.trl.safety.min3d.core.Object3dContainer;
import edu.smu.trl.safety.min3d.core.RendererActivity;
import edu.smu.trl.safety.min3d.parser.IParser;
import edu.smu.trl.safety.min3d.parser.Parser;
import edu.smu.trl.safety.min3d.vos.Color4;
import edu.smu.trl.safety.min3d.vos.Light;

/**
 * How to load a model from a .obj file
 *
 * @author dennis.ippel
 */
public class ExampleLoadObjFile extends RendererActivity {
    String Info = "Hello World";
    private Object3dContainer MyCar;
    private Color4 Color = new Color4(1F, 0, 0, 0);

    @Override
    public void initScene() {

        Light light = new Light();
        light.ambient.setAll(0xff888888);
        light.position.setAll(3, 0, 3);
        scene.lights().add(light);


        IParser parser = Parser.createParser(Parser.Type.OBJ, getResources(), "edu.smu.trl.safety.radarsafety:raw/camaro_obj_blue", true);
        parser.parse();

        MyCar = parser.getParsedObject();
        MyCar.scale().x = MyCar.scale().y = MyCar.scale().z = 1f;
        MyCar.position().x = MyCar.position().y = MyCar.position().z = 0;

        scene.addChild("MyCar", MyCar);


        scene.backgroundColor().r((short) 50);
        scene.backgroundColor().g((short) 50);
        scene.backgroundColor().b((short) 50);


        scene.camera().position.rotateX(0);
        scene.camera().position.setAll(0, 0, 30);
    }

    @Override
    public void updateScene() {
        MyCar.rotation().x++;
        MyCar.rotation().z++;


        Renderer.DisplayText(Info, MyCar.position(), Color);

    }
}