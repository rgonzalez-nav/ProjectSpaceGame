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
public class WorkerControl extends AbstractControl {

    private Vector3f buildLocation;
    private final BattleState state;

    public WorkerControl(BattleState battleState) {
        this.state = battleState;
    }

    public void build(Vector3f buildLocation) {
        this.buildLocation = buildLocation;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (buildLocation != null) {
            Spatial station = state.getModelLoader().loadStation();
            station.setLocalTranslation(buildLocation);
            station.addControl(new StationControl(state));
            state.getClickables().attachChild(station);
            buildLocation = null;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
