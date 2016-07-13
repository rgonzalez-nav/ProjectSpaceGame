/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafa
 */
public class AttackControl extends AbstractControl{
    private final Animations animations;
    private final int weaponType;
    private final Globals globals;
    private final Node rootNode;
    
    public AttackControl(Animations animations, int weaponType, Globals globals, Node rootNode){
        this.animations = animations;
        this.weaponType = weaponType;
        this.globals = globals;
        this.rootNode = rootNode;
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
            Geometry beam = animations.loadBeamAnimation();
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
