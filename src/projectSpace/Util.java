/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class Util {
    //This constant could be replaced for a parameter to be received.
    private static final float ADJUSTMENT_ANGLE_Y = FastMath.PI / 2;
    
    public float lookAt(Vector3f rotatePoint, Spatial spatial) {
        Vector3f position = spatial.getLocalTranslation();
        float dx = rotatePoint.x - position.x;
        float dy = rotatePoint.z - position.z;
        float angle = rotationAngle(dx, dy);
        angle += ADJUSTMENT_ANGLE_Y;
        spatial.setLocalRotation(new Quaternion(new float[]{0, angle, 0}));
        return angle;
    }
    
     public static float rotationAngle(float opposite, float adyacent) {
        if (adyacent == 0) {
            return 0;
        }
        float angle = (float) Math.atan(opposite / adyacent);
        if (adyacent < 0) {
            angle += FastMath.PI;
        }
        return angle;
    }
}
