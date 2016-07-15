/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.HashMap;

/**
 *
 * @author rafagonz
 */
public class Globals {
    private Spatial selectedSprite;
    private Spatial selectedBuilding;
    private float globalSpeed;
    private Geometry circle;
    private Geometry mark;
    private AssetManager assetManager;
    private HashMap<Integer, Spatial> units;
    private int unitId = 0;
    private Weapons weapons;
    private Util util;
    
    public Globals(){
        units = new HashMap<>();
        selectedBuilding = null;
        util = new Util();
    }
    
    public Globals(Spatial selectedSprite, float globalSpeed, Geometry circle, Geometry mark, AssetManager assetManager){
        this();
        this.selectedSprite = selectedSprite;
        this.globalSpeed = globalSpeed;
        this.circle = circle;
        this.mark = mark;
        weapons = new Weapons(assetManager);
    }
    
    public void setSelectedSprite(Spatial selectedSprite){
        this.selectedSprite = selectedSprite;
    }
    
    public Spatial getSelectedSprite(){
        return selectedSprite;
    }
    
    public void setGlobalSpeed(float globalSpeed){
        this.globalSpeed = globalSpeed;
    }
    
    public float getGlobalSpeed(){
        return globalSpeed;
    }
    
    public void setCircle(Geometry circle){
        this.circle = circle;
    }
    
    public Geometry getCircle(){
        return circle;
    }
    
    public void setMark(Geometry mark){
        this.mark = mark;
    }
    
    public Geometry getMark(){
        return mark;
    }
    
    public int addUnit(Spatial unit){
        units.put(++unitId, unit);
        return unitId;
    }
    
    public void removeUnit(Spatial unit){
        units.remove((Integer)unit.getUserData("unitId"));
    }
    
    public HashMap getUnits(){
        return units;
    }
    
    public void setAssetManager(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    public AssetManager assetManager(){
        return assetManager;
    }
    
    public void setWeapons(Weapons weapons){
        this.weapons = weapons;
    }
    
    public Weapons getWeapons(){
        return weapons;
    }
    
    public void setSelectedBuilding(Spatial selectedBuilding){
        this.selectedBuilding = selectedBuilding;
    }
    
    public Spatial getSelectedBuilding(){
        return selectedBuilding;
    }
    
    public void setUtil(Util util){
        this.util = util;
    }
    
    public Util getUtil(){
        return util;
    }
}
