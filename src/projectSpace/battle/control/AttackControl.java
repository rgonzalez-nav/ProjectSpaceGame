/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import projectSpace.battle.BattleState;

/**
 *
 * @author Administrator
 */
public class AttackControl extends AbstractControl {

    private final BattleState state;
    private Vector3f target;
    private final Type type;

    public AttackControl(BattleState state, Type type) {
        this.state = state;
        this.type = type;
    }

    public void attack(Vector3f target) {
        this.target = target;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (target != null) {
            Geometry weapon = null;
            WeaponMovementControl weaponMovementControl;
            if (type.equals(Type.BEAM)) {
                weapon = state.getWeapons().loadBeam();
                weaponMovementControl = new WeaponMovementControl(2);
            } else {
                weapon = state.getWeapons().loadBullet();
                weaponMovementControl = new WeaponMovementControl(3);
            }
            weapon.setLocalTranslation(spatial.getLocalTranslation().clone());
            weapon.addControl(weaponMovementControl);
            weaponMovementControl.fire(target);
            state.getRootNode().attachChild(weapon);
            target = null;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public enum Type {
        BULLET, BEAM
    }
}
