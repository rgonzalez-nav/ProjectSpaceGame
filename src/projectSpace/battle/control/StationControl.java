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
import com.jme3.scene.control.AbstractControl;
import projectSpace.battle.BattleState;

/**
 *
 * @author Administrator
 */
public class StationControl extends AbstractControl {

    private final BattleState state;
    boolean creating = false;
    public Vector3f createPosition;

    public StationControl(BattleState state) {
        this.state = state;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        createPosition = new Vector3f(spatial.getLocalTranslation().x + 1, 0, spatial.getLocalTranslation().z + 1);
    }

    void createShip() {
        creating = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (creating) {
            Spatial ship = state.getModelLoader().loadShip();
            createPosition = new Vector3f(createPosition.x + .5f, 0, createPosition.z + .5f);
            ship.setLocalTranslation(createPosition);
            ship.addControl(new SpriteMovementControl(3));
            ship.addControl(new AttackControl(state, AttackControl.Type.BEAM));
            state.getClickables().attachChild(ship);
            creating = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
