/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class WorkerControl extends CommonControl{
    private boolean constructing;
    private Vector3f place;
    private final Node sprites;
    private final Models models;
    
    public WorkerControl(Globals globals, AssetManager assetManager, Node rootNode, 
            Node sprites, Models models) {
        super(globals, assetManager, rootNode);
        this.sprites = sprites;
        this.models = models;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        constructing = false;
        place = spatial.getLocalTranslation();
    }
    
    public void build(Vector3f place){
        constructing = true;
        this.place = place;
    }
    
    public Spatial loadBuilding(){
        Spatial station = models.loadStation();
        station.setLocalTranslation(place);
        station.addControl(new BuildingControl(globals, assetManager, rootNode, models));
        return station;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(constructing){
            System.out.println("constrcuting at: "+place);
            Spatial building = loadBuilding();
            Node clickables = (Node)rootNode.getChild("clickables");
            clickables.attachChild(building);
            constructing = false;
        }
    }
}
