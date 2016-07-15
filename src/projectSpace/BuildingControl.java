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
    
    public BuildingControl(Globals globals, AssetManager assetManager, Node rootNode,Node sprites){
        super(globals, assetManager, rootNode);
        this.sprites = sprites;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setUserData("creating", false);
    }
    
    private Spatial loadShip(){
        Spatial ship = assetManager.loadModel("Models/shuttle/shuttle.obj");
        ship.setName("ship");
        ship.scale(0.005f, 0.005f, 0.005f);
        posCont += 0.5f;
        ship.setLocalTranslation(posCont, 0, posCont);

        Node shipNode = (Node)ship;
        Geometry geo = (Geometry)shipNode.getChild(0);
        Material shipMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        shipMat.setBoolean("UseMaterialColors",true);
        shipMat.setColor("Diffuse",ColorRGBA.White);
        shipMat.setColor("Specular",ColorRGBA.White);
        shipMat.setFloat("Shininess", 64f);
        geo.setMaterial(shipMat);
        ship.setUserData("moving", false);
        ship.setUserData("newPosition", ship.getLocalTranslation());
        ship.setUserData("unitId", globals.addUnit(ship));

        ship.addControl(new SpriteMovementControl(3, globals));

        ship.addControl(new AttackControl(globals, assetManager, rootNode, 0));
            
        return ship;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(spatial.getUserData("creating")){
            Spatial ship = loadShip();
            globals.addUnit(ship);
            sprites.attachChild(ship);
            spatial.setUserData("creating", false);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
