package edu.smu.trl.safety.min3d.parser;

/**
 * Created by TRL on 3/4/2016.
 */

import android.content.res.Resources;

/**
 * Parser factory class. Specify the parser type and the corresponding
 * specialized class will be returned.
 *
 * @author dennis.ippel
 */
public class Parser {
    /**
     * Create a parser of the specified type.
     *
     * @param type
     * @param resources
     * @param resourceID
     * @return
     */
    public static IParser createParser(Type type, Resources resources, String resourceID, boolean generateMipMap) {
        switch (type) {
            case OBJ:
                return new ObjParser(resources, resourceID, generateMipMap);
            case MAX_3DS:
                return new Max3DSParser(resources, resourceID, generateMipMap);
            case MD2:
                return new MD2Parser(resources, resourceID, generateMipMap);
        }

        return null;
    }

    ;

    /**
     * Parser types enum
     *
     * @author dennis.ippel
     */
    public static enum Type {
        OBJ, MAX_3DS, MD2
    }
}