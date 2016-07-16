/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

/**
 *
 * @author Administrator
 */
public class WeaponMovementControl extends MovementControl {

    private boolean growing;

    public WeaponMovementControl(int velocity) {
        this.velocity = velocity;
    }

    public void fire(Vector3f target) {
        this.destination = target;
        growing = true;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (destination != null) {
            float movement = velocity * tpf;
            if (growing) {
                float scaleX = spatial.getLocalScale().x + movement;
                spatial.setLocalScale(new Vector3f(scaleX, 1, 1));
                lookAt(destination);
                if (scaleX > 1) {
                    growing = false;
                }
            }
            Vector3f nextPosition = nextPosition(movement);
            moveTo(nextPosition);
        }
    }

    @Override
    protected void moveTo(Vector3f movePosition) {
        if (isMoveInDestination(movePosition)) {
            destination = null;
            spatial.getParent().detachChild(spatial);
        } else {
            spatial.setLocalTranslation(movePosition);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
