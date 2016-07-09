/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafagonz
 */
public class SpriteMovementControl extends AbstractControl{
    private final float spriteVelocity;
    private final Globals globals;
    
    public SpriteMovementControl(float spriteVelocity, Globals globals){
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
        if(spatial.getUserData("moving")){
            float tempX = spatial.getLocalTranslation().x;
            float tempZ = spatial.getLocalTranslation().z;
            float newX = ((Vector3f)spatial.getUserData("newPosition")).x;
            float newZ = ((Vector3f)spatial.getUserData("newPosition")).z;
            Boolean left = false;
            Boolean up = false;
            
            //decide movement increment and direction
            if (tempX < newX) {
                tempX += tpf * globals.getGlobalSpeed() * spriteVelocity;
                left = true;
            } else if (tempX > newX) {
                tempX -= tpf * globals.getGlobalSpeed() * spriteVelocity;
            }
            if (tempZ < newZ) {
                up = true;
                tempZ += tpf * globals.getGlobalSpeed() * spriteVelocity;
            } else if (tempZ > newZ) {
                tempZ -= tpf * globals.getGlobalSpeed() * spriteVelocity;
            }

            //set new position
            if(alreadyInZPoint(up, tempZ, newZ) && alreadyInXPoint(left, tempX, newX)){
                spatial.setUserData("moving", false);
            }else{
                spatial.setLocalTranslation(tempX, 0, tempZ);
                if(globals.getSelectedSprite().equals(spatial)){
                    globals.getCircle().setLocalTranslation(tempX, 0, tempZ);
                }
            }
        }
    }
    
    private boolean alreadyInXPoint(Boolean left, float tempX, float newX) {
        if(left && tempX >= newX){
            return true;
        }else return !left && tempX <= newX;
    }

    private boolean alreadyInZPoint(Boolean up, float tempZ, float newZ) {
        if(up && tempZ >= newZ){
            return true;
        }else return !up && tempZ <= newZ;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //TO DO:
    }
}
