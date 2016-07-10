/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author rafagonz
 */
public class BattleManager extends SimpleApplication{
    private Globals globals;
    Node clickables;
    Node sprites;
    private Spatial ship;
    private SpriteMovementControl shipMovementControl;
    private InputControl inputControl;
    private SkyboxGenerator generator;
    
    private Geometry initMark(){
        Sphere sphere = new Sphere(30, 30, 0.2f);
        Geometry mark = new Geometry("Click", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        
        return mark;
    }
    
    private void initKeys(){
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Command", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(inputControl, "Select", "Command");
    }
    
    private Geometry makeFloor(){
        Box box = new Box(15, .2f, 15);
        Geometry floor = new Geometry("Floor", box);
        floor.setLocalTranslation(0, -0.1f, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.BlackNoAlpha);
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        floor.setMaterial(mat1);
        floor.setQueueBucket(RenderQueue.Bucket.Transparent);
        return floor;
    }
    
    private Spatial loadShip(){
        ship = assetManager.loadModel("Models/shuttle/shuttle.obj");
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
        ship.setUserData("moving", false);
        ship.setUserData("newPosition", ship.getLocalTranslation());
        
        ship.addControl(shipMovementControl);
        
       return ship; 
    }
    
    
    
    private Geometry paintCircle(){
        Box cube2Mesh = new Box( 0.5f,0.001f,0.5f);
        Geometry circle = new Geometry("window frame", cube2Mesh);
        Material cube2Mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        cube2Mat.setTexture("ColorMap", assetManager.loadTexture("Textures/blue-circle.png"));
        cube2Mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        circle.setQueueBucket(RenderQueue.Bucket.Transparent);
        circle.setMaterial(cube2Mat);
        
        return circle;
    }

    @Override
    public void simpleInitApp() {
        globals = new Globals();
        clickables = new Node();
        sprites = new Node();
        generator = new SkyboxGenerator(assetManager, rootNode);
        generator.createSky();
        inputControl = new InputControl(inputManager, cam, clickables, globals, rootNode);
        
        initKeys();
        globals.setMark(initMark());
        shipMovementControl = new SpriteMovementControl(2, globals);
        
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        
        globals.setCircle(paintCircle());
        globals.setGlobalSpeed(1f);
        
        cam.setLocation(new Vector3f(5, 5, 10));
        cam.setRotation(new Quaternion(-0.07f, 0.92f, -0.25f, -0.27f));
        
        sprites.attachChild(loadShip());
        
        clickables.attachChild(makeFloor());
        clickables.attachChild(sprites);
        rootNode.attachChild(clickables);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
}
