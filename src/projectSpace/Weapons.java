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
import com.jme3.scene.shape.Box;

/**
 *
 * @author rafagonz
 */
public class Weapons {
    private final AssetManager assetManager;
    public Weapons(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    public Geometry loadBeam(){
        Box innerCube = new Box(0.6f, 0.03f, 0.03f);
        Geometry innerBeam = new Geometry("inner beam", innerCube);
        Material innerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        innerMat.setColor("Color", ColorRGBA.Cyan);
        innerMat.setColor("GlowColor", ColorRGBA.Cyan);
        innerBeam.setMaterial(innerMat);
        
        innerBeam.setUserData("growing", true);
        innerBeam.setLocalScale(0.001f, 1, 1);
        
        return innerBeam;
    }
    
    public Geometry loadBullet(){
        Box bulletCube = new Box(0.25f, 0.02f, 0.02f);
        Geometry bullet = new Geometry("bullet", bulletCube);
        Material bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMat.setColor("Color", ColorRGBA.Yellow);
        bulletMat.setColor("GlowColor", ColorRGBA.Yellow);
        bullet.setMaterial(bulletMat);
        
        bullet.setLocalScale(0.001f, 1, 1);
        
        return bullet;
    }
}
