/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author rafagonz
 */
public class CommonControl extends AbstractControl{
    protected Globals globals;
    protected AssetManager assetManager;
    protected Node rootNode;
    
    public CommonControl(Globals globals, AssetManager assetManager, Node rootNode){
        this.assetManager = assetManager;
        this.globals = globals;
        this.rootNode = rootNode;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
