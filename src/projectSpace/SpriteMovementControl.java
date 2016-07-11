/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafagonz
 */
public class SpriteMovementControl extends AbstractControl {

    private final float spriteVelocity;
    private final Globals globals;

    public SpriteMovementControl(float spriteVelocity, Globals globals) {
        this.spriteVelocity = spriteVelocity;
        this.globals = globals;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setUserData("moving", false);
        spatial.setUserData("newPosition", new Vector3f(0, 0, 0));
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getUserData("moving")) {
            float posX = spatial.getLocalTranslation().x;
            float posZ = spatial.getLocalTranslation().z;
            float newPosX = ((Vector3f) spatial.getUserData("newPosition")).x;
            float newPosZ = ((Vector3f) spatial.getUserData("newPosition")).z;
            Boolean fowardX = false;
            Boolean fowardZ = false;

            //decide movement increment and direction
            float movement = tpf * globals.getGlobalSpeed() * spriteVelocity;
            if (posX < newPosX) {
                posX += movement;
                fowardX = true;
            } else if (posX > newPosX) {
                posX -= movement;
            }
            if (posZ < newPosZ) {
                fowardZ = true;
                posZ += movement;
            } else if (posZ > newPosZ) {
                posZ -= movement;
            }

            //set new position
            if (isPositionInPlace(fowardX, posX, newPosX) && isPositionInPlace(fowardZ, posZ, newPosZ)) {
                spatial.setUserData("moving", false);
                spatial.setLocalTranslation(newPosX, 0, newPosZ);
            } else {
                spatial.setLocalTranslation(posX, 0, posZ);
                if (globals.getSelectedSprite().equals(spatial)) {
                    globals.getCircle().setLocalTranslation(posX, 0, posZ);
                }
            }
        }
    }

    private static boolean isPositionInPlace(boolean foward, float position, float destination) {
        if (foward && position >= destination) {
            return true;
        } else {
            return !foward && position <= destination;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //TO DO:
    }
}