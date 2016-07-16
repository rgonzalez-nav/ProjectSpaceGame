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
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class BuildingControl extends CommonControl{
    private float posX;
    private float posZ;
    private boolean creating;
    private final ModelLoader models;
    
    public BuildingControl(Globals globals, AssetManager assetManager, Node rootNode, ModelLoader models){
        super(globals, assetManager, rootNode);
        this.models = models;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        posX=spatial.getLocalTranslation().x+1;
        posZ=spatial.getLocalTranslation().z+1;
        creating = false;
    }
    
    public void create(){
        creating = true;
    }
    
    private Spatial loadShip(){
        Spatial ship = models.loadShip();
        posX += 0.5f;
        posZ += 0.5f;
        ship.setLocalTranslation(posX, 0, posZ);
        ship.setUserData("unitId", globals.addUnit(ship));
        ship.addControl(new SpriteMovementControl(3, globals));
        ship.addControl(new AttackControl(globals, assetManager, rootNode, 1));
            
        return ship;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(creating){
            Spatial ship = loadShip();
            globals.addUnit(ship);
            Node sprites = (Node)rootNode.getChild("sprites");
            sprites.attachChild(ship);
            creating = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
