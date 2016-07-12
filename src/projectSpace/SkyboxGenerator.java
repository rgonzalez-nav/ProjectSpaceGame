/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 *
 * @author rafagonz
 */
public class SkyboxGenerator {
    private final AssetManager assetManager;
    private final Node rootNode;
    
    public SkyboxGenerator(AssetManager assetManager, Node rootNode){
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    public void createSky(){
        Texture west = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeX.png");
        Texture east = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveX.png");
        Texture north = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveZ.png");
        Texture south = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeZ.png");
        Texture up = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveY.png");
        Texture down = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeY.png");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
    }
}
