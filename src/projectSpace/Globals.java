/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class Globals {
    private Spatial selectedSprite;
    private float globalSpeed;
    private Geometry circle;
    
    public Globals(){}
    
    public Globals(Spatial selectedSprite, float globalSpeed, Geometry circle){
        this.selectedSprite = selectedSprite;
        this.globalSpeed = globalSpeed;
        this.circle = circle;
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
}
