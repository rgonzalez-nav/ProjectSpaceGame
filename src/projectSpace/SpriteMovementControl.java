/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Objects;

/**
 *
 * @author rafagonz
 */
public class SpriteMovementControl extends AbstractControl {

    private static final float ADJUSTMENT_ANGLE_Y = FastMath.PI / 2;
    private final float spriteVelocity;
    private final Globals globals;

    private Vector3f destination;
    private boolean moving;

    public SpriteMovementControl(float spriteVelocity, Globals globals) {
        this.spriteVelocity = spriteVelocity;
        this.globals = globals;
    }

    public void move(Vector3f destination) {
        this.destination = destination;
        this.moving = true;
        rotateSpatialTo(destination);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        moving = false;
        destination = spatial.getLocalTranslation();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (moving) {
            Vector3f nextPosition = nextPosition(tpf);
            moveSpatialTo(nextPosition);
        }
    }

    private Vector3f nextPosition(float tpf) {
        Vector3f position = spatial.getLocalTranslation();
        float opposite = destination.z - position.z;
        float adyacent = destination.x - position.x;
        float hypotenuse = FastMath.sqrt(FastMath.pow(opposite, 2) + FastMath.pow(adyacent, 2));

        float movement = tpf * globals.getGlobalSpeed() * spriteVelocity;
        float dx = (adyacent * movement) / hypotenuse;
        float dz = (opposite * movement) / hypotenuse;

        Vector3f nextPosition = position.clone();
        nextPosition.x += dx;
        nextPosition.z += dz;
        return nextPosition;
    }

    private void moveSpatialTo(Vector3f movePosition) {
        if (isMoveInDestination(movePosition)) {
            moving = false;
            spatial.setLocalTranslation(destination.x, 0, destination.z);
        } else {
            spatial.setLocalTranslation(movePosition);
            if (Objects.equals(globals.getSelectedSprite(), spatial)) {
                globals.getCircle().setLocalTranslation(movePosition);
            }
        }
    }

    private boolean isMoveInDestination(Vector3f movePosition) {
        Vector3f position = spatial.getLocalTranslation();
        boolean movingFowardX = position.x < destination.x;
        boolean movingFowardZ = position.z < destination.z;
        return isPositionInPlace(movingFowardX, movePosition.x, destination.x) && isPositionInPlace(movingFowardZ, movePosition.z, destination.z);
    }

    private float rotateSpatialTo(Vector3f rotatePoint) {
        Vector3f position = spatial.getLocalTranslation();
        float dx = rotatePoint.x - position.x;
        float dy = rotatePoint.z - position.z;
        float angle = rotationAngle(dx, dy);
        angle += ADJUSTMENT_ANGLE_Y;
        spatial.setLocalRotation(new Quaternion(new float[]{0, angle, 0}));
        return angle;
    }

    private float rotationAngle(float opposite, float adyacent) {
        if (adyacent == 0) {
            return 0;
        }
        float angle = (float) Math.atan(opposite / adyacent);
        if (adyacent < 0) {
            angle += FastMath.PI;
        }
        return angle;
    }

    private static boolean isPositionInPlace(boolean movingFoward, float position, float destination) {
        if (movingFoward && position >= destination) {
            return true;
        } else {
            return !movingFoward && position <= destination;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //TO DO:
    }
}
