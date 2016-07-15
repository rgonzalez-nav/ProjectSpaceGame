/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafa
 */
public class WeaponMovementControl extends AbstractControl{
    private final float spriteVelocity;
    private final Globals globals;
    private boolean growing;
    private boolean traveling;
    private Vector3f newPosition;
    private Spatial enemy;
    
    public WeaponMovementControl(float spriteVelocity, Globals globals){
        this.spriteVelocity = spriteVelocity;
        this.globals = globals;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        growing = false;
        traveling = false;
        newPosition = spatial.getLocalTranslation();
        enemy = null;
    }
    
    public void fire(Spatial enemy, Vector3f newPosition){
        growing = true;
        traveling = true;
        this.enemy = enemy;
        this.newPosition = newPosition;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        float movementSpeed = tpf * globals.getGlobalSpeed() * spriteVelocity;
        if(growing){
            Quaternion tempRotation = new Quaternion(spatial.getLocalRotation().getX(), spatial.getLocalRotation().getY(),
                    spatial.getLocalRotation().getZ(), spatial.getLocalRotation().getW());
            spatial.setLocalRotation(Quaternion.ZERO);
            float currentScale = spatial.getLocalScale().x;
            spatial.setLocalScale(new Vector3f(currentScale+movementSpeed,1,1));
            globals.getUtil().lookAt(newPosition, spatial);
            if(spatial.getLocalScale().x>1){
                growing = false;
            }
        }
        if(traveling){
            Vector3f position = spatial.getLocalTranslation();
            float opposite = newPosition.z - position.z;
            float adyacent = newPosition.x - position.x;
            float hypotenuse = FastMath.sqrt(FastMath.pow(opposite, 2) + FastMath.pow(adyacent, 2));

            float movement = tpf * globals.getGlobalSpeed() * spriteVelocity;
            float dx = (adyacent * movement) / hypotenuse;
            float dz = (opposite * movement) / hypotenuse;

            Vector3f nextPosition = position.clone();
            nextPosition.x += dx;
            nextPosition.z += dz;
            
            spatial.setLocalTranslation(nextPosition);
            
            CollisionResults results = new CollisionResults();
            spatial.getWorldBound().collideWith(enemy.getWorldBound(), results);
            
            if(results.size()>0){
                traveling = false;
                spatial.getParent().detachChild(spatial);
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
    protected void controlRender(RenderManager rm, ViewPort vp) {    }
    
}
