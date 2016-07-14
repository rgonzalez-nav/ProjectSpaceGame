/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
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
    
    public AttackControl(Globals globals, AssetManager assetManager, Node rootNode, int weaponType){
        super(globals, assetManager, rootNode);
        this.weaponType = weaponType;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setUserData("shooting", false);
        spatial.setUserData("enemy", null);
        spatial.setUserData("target", new Vector3f(0,0,0));
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(spatial.getUserData("shooting")){
            Geometry beam = globals.getWeapons().loadBeam();
            WeaponMovementControl beamMovementControl = new WeaponMovementControl(2, globals);
            beam.addControl(beamMovementControl);
            //beam.rotate(0, 0, 0);
            beam.setLocalTranslation(spatial.getLocalTranslation());
            beam.setUserData("newPosition", spatial.getUserData("target"));
            beam.setUserData("growing", true);
            beam.setUserData("traveling", true);
            beam.setUserData("enemy",spatial.getUserData("enemy"));
            rootNode.attachChild(beam);
            
            spatial.setUserData("shooting", false);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
