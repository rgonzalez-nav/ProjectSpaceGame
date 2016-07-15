/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author rafagonz
 */
public class Models {
    private final boolean simpleGeometries = Util.DEVELOPMENT;
    private final String ATTACK_SHIP_URL = "Models/shuttle/shuttle.obj";
    private final String DOCK_STATION_URL = "Models/station/TARDIS-FIGR_mkIII_station.obj";
    private final String SIMPLE_CONE_URL = "Models/simpleCone/cone.obj";
    private final String SIMPLE_CUBE_URL = "Models/simpleCube/cube.obj";
    private final AssetManager assetManager;
    
    public Models(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    public Spatial loadShip(){
        Spatial ship;
        if(simpleGeometries){
            ship = assetManager.loadModel(SIMPLE_CONE_URL);
            ship.setName("ship");
            ship.setLocalScale(0.4f, 0.4f, 0.4f);
            Material shipMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            shipMat.setColor("Color", ColorRGBA.Blue);
            ship.setMaterial(shipMat);
        }else{
            ship = assetManager.loadModel(ATTACK_SHIP_URL);
            ship.setName("ship");
            ship.scale(0.005f, 0.005f, 0.005f);

            Node shipNode = (Node)ship;
            Geometry geo = (Geometry)shipNode.getChild(0);
            Material shipMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            shipMat.setBoolean("UseMaterialColors",true);
            shipMat.setColor("Diffuse",ColorRGBA.White);
            shipMat.setColor("Specular",ColorRGBA.White);
            shipMat.setFloat("Shininess", 64f);
            geo.setMaterial(shipMat);
        }
        
        return ship;
    }
    
    public Spatial loadStation(){
        Spatial station;
        if(simpleGeometries){
            station = assetManager.loadModel(SIMPLE_CUBE_URL);
            station.setName("station");
        }else{
            station = assetManager.loadModel(DOCK_STATION_URL);
            station.setName("station");
            station.setLocalScale(0.0004f, 0.0004f, 0.0004f);
            station.setLocalTranslation(0, 1.2f, 0);
        }
        
        return station;
    }
}
