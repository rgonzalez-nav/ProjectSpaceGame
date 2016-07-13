/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResults;
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
    
    public WeaponMovementControl(float spriteVelocity, Globals globals){
        this.spriteVelocity = spriteVelocity;
        this.globals = globals;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setUserData("growing", false);
        spatial.setUserData("traveling", false);
        spatial.setUserData("newPosition", spatial.getLocalTranslation());
        spatial.setUserData("enemy", null);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        float movementSpeed = tpf * globals.getGlobalSpeed() * spriteVelocity;
        if(spatial.getUserData("growing")){
            Quaternion tempRotation = new Quaternion(spatial.getLocalRotation().getX(), spatial.getLocalRotation().getY(),
                    spatial.getLocalRotation().getZ(), spatial.getLocalRotation().getW());
            spatial.setLocalRotation(Quaternion.ZERO);
            float currentScale = spatial.getLocalScale().x;
            spatial.setLocalScale(new Vector3f(currentScale+movementSpeed,1,1));
            spatial.setLocalRotation(tempRotation);
            if(spatial.getLocalScale().x>1){
                spatial.setUserData("growing", false);
            }
        }
        if(spatial.getUserData("traveling")){
            float x, z, dx, dz, p, stepx, stepz;
            x = spatial.getLocalTranslation().x;
            z = spatial.getLocalTranslation().z;
            float newX = ((Vector3f)spatial.getUserData("newPosition")).x;
            float newZ = ((Vector3f)spatial.getUserData("newPosition")).z;
            dx = (newX - x);
            dz = (newZ - z);
            
            Boolean left = false;
            Boolean up = false;

           /* determinar que punto usar para empezar, cual para terminar */
            if (dz < 0) { 
              dz = -dz; 
              stepz = movementSpeed*(-1);
            } 
            else {
              stepz = movementSpeed;
              up = true;
            }

            if (dx < 0) {  
              dx = -dx;
              stepx = movementSpeed*(-1); 
            } 
            else {
              stepx = movementSpeed;
              left = true;
            }
            
           /* se cicla hasta llegar al extremo de la línea */
            if(dx>dz){
              p = 2*dz - dx;
              x = x + stepx;
              if (p > 0){
                  z = z + stepz;
              }
            }
            else{
              p = 2*dx - dz;
              z = z + stepz;
              if (p > 0){
                x = x + stepx;
              }
            }
            
            CollisionResults results = new CollisionResults();
            spatial.getWorldBound().collideWith(((Spatial)spatial.getUserData("enemy")).getWorldBound(), results);
            
            if(results.size()>0){
                spatial.setUserData("traveling", false);
                spatial.getParent().detachChild(spatial);
            }
            
            
            if(alreadyInZPoint(up, z, newZ) && alreadyInXPoint(left, x, newX)){
                spatial.setUserData("traveling", false);
                spatial.getParent().detachChild(spatial);
            }else{
                spatial.setLocalTranslation(x, 0, z);
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
