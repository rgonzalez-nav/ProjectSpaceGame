/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafa
 */
public class AttackControl extends AbstractControl{
    
    
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setUserData("shooting", false);
        spatial.setUserData("spatial", new Vector3f(0, 0, 0));
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
