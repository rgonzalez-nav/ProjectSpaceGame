/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Administrator
 */
public class SpriteMovementControl extends MovementControl {

    public SpriteMovementControl(float velocity) {
        this.velocity = velocity;
    }

    public void move(Vector3f destination) {
        this.destination = destination;
        lookAt(destination);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (destination != null) {
            Vector3f nextPosition = nextPosition(velocity * tpf);
            moveTo(nextPosition);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
