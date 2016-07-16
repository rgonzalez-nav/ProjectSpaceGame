/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle.control;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Administrator
 */
public abstract class MovementControl extends AbstractControl {

    protected static final float ADJUSTMENT_ANGLE_Y = FastMath.PI / 2;
    protected static boolean isPositionInPlace(boolean movingFoward, float position, float destination) {
        if (movingFoward && position >= destination) {
            return true;
        } else {
            return !movingFoward && position <= destination;
        }
    }

    protected float velocity;
    protected Vector3f destination;


    public MovementControl() {
    }

    protected float lookAt(Vector3f rotatePoint) {
        Vector3f position = spatial.getLocalTranslation();
        float dx = rotatePoint.x - position.x;
        float dy = rotatePoint.z - position.z;
        float angle = rotationAngle(dx, dy);
        angle += ADJUSTMENT_ANGLE_Y;
        spatial.setLocalRotation(new Quaternion(new float[]{0, angle, 0}));
        return angle;
    }

    protected float rotationAngle(float opposite, float adyacent) {
        if (adyacent == 0) {
            return 0;
        }
        float angle = (float) Math.atan(opposite / adyacent);
        if (adyacent < 0) {
            angle += FastMath.PI;
        }
        return angle;
    }

    protected Vector3f nextPosition(float movement) {
        Vector3f position = spatial.getLocalTranslation();
        float opposite = destination.z - position.z;
        float adyacent = destination.x - position.x;
        float hypotenuse = FastMath.sqrt(FastMath.pow(opposite, 2) + FastMath.pow(adyacent, 2));
        float dx = (adyacent * movement) / hypotenuse;
        float dz = (opposite * movement) / hypotenuse;
        Vector3f nextPosition = position.clone();
        nextPosition.x += dx;
        nextPosition.z += dz;
        return nextPosition;
    }

    protected void moveTo(Vector3f movePosition) {
        if (isMoveInDestination(movePosition)) {
            spatial.setLocalTranslation(destination.x, 0, destination.z);
            destination = null;
        } else {
            spatial.setLocalTranslation(movePosition);
        }
    }

    protected boolean isMoveInDestination(Vector3f movePosition) {
        Vector3f position = spatial.getLocalTranslation();
        boolean movingFowardX = position.x < destination.x;
        boolean movingFowardZ = position.z < destination.z;
        return isPositionInPlace(movingFowardX, movePosition.x, destination.x) && isPositionInPlace(movingFowardZ, movePosition.z, destination.z);
    }

}
