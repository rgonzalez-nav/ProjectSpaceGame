/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class BuildingControl extends CommonControl{
    private final Node sprites;
    private float posCont = 1.5f;
    private boolean creating;
    private final Models models;
    
    public BuildingControl(Globals globals, AssetManager assetManager, Node rootNode, Node sprites, Models models){
        super(globals, assetManager, rootNode);
        this.sprites = sprites;
        this.models = models;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        creating = false;
    }
    
    public void create(){
        creating = true;
    }
    
    private Spatial loadShip(){
        Spatial ship = models.loadShip();
        posCont += 0.5f;
        ship.setLocalTranslation(posCont, 0, posCont);
        ship.setUserData("unitId", globals.addUnit(ship));
        ship.addControl(new SpriteMovementControl(3, globals));
        ship.addControl(new AttackControl(globals, assetManager, rootNode, 0));
            
        return ship;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(creating){
            Spatial ship = loadShip();
            globals.addUnit(ship);
            sprites.attachChild(ship);
            creating = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
