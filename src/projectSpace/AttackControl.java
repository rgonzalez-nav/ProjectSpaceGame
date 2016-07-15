/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafa
 */
public class AttackControl extends CommonControl{
    private final int weaponType;
    private boolean shooting;
    private Spatial enemy;
    private Vector3f target;
    
    public AttackControl(Globals globals, AssetManager assetManager, Node rootNode, int weaponType){
        super(globals, assetManager, rootNode);
        this.weaponType = weaponType;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        shooting = false;
        enemy = null;
        target = spatial.getLocalTranslation();
    }
    
    public void attack(Spatial enemy, Vector3f target){
        shooting = true;
        this.enemy = enemy;
        this.target = target;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(shooting){
            Geometry beam = globals.getWeapons().loadBeam();
            WeaponMovementControl beamMovementControl = new WeaponMovementControl(2, globals);
            beam.addControl(beamMovementControl);
            Util.lookAt(target, spatial);
            Util.lookAt(target, beam);
            beam.setLocalTranslation(spatial.getLocalTranslation());
            beamMovementControl.fire(enemy, target);
            rootNode.attachChild(beam);
            
            shooting = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
