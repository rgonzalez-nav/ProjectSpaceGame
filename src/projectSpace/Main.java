/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
//import com.jme3.texture.Texture;
//import com.jme3.util.SkyFactory;

/**
 *
 * @author rafagonz
 */
public class Main extends SimpleApplication{
    Node clickables;
    Node sprites;
    private Spatial ship;
    Geometry mark;
    private Globals globals;
    private SpriteMovementControl shipMovementControl;
    
    //float time;
    
    @Override
    public void simpleInitApp() {
        initKeys();
        initMark();
        
        globals = new Globals();
        shipMovementControl = new SpriteMovementControl(2, globals);
        
        /*Texture west = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeX.png");
        Texture east = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveX.png");
        Texture north = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveZ.png");
        Texture south = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeZ.png");
        Texture up = assetManager.loadTexture("Textures/space/GalaxyTex_PositiveY.png");
        Texture down = assetManager.loadTexture("Textures/space/GalaxyTex_NegativeY.png");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);*/
        
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        
        globals.setCircle(paintCircle());
        globals.setGlobalSpeed(1f);
        
        cam.setLocation(new Vector3f(5, 5, 10));
        cam.setRotation(new Quaternion(-0.07f, 0.92f, -0.25f, -0.27f));
        
        clickables = new Node();
        sprites = new Node();
        clickables.attachChild(makeFloor());
        sprites.attachChild(loadShip());
        clickables.attachChild(sprites);
        rootNode.attachChild(clickables);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
    
    private Geometry makeFloor(){
        Box box = new Box(15, .2f, 15);
        Geometry floor = new Geometry("Floor", box);
        floor.setLocalTranslation(0, -0.1f, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.BlackNoAlpha);
        mat1.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        floor.setMaterial(mat1);
        floor.setQueueBucket(Bucket.Transparent);
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
    
    private void initMark(){
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("Click", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }
    
    private Geometry paintCircle(){
        Box cube2Mesh = new Box( 0.5f,0.001f,0.5f);
        Geometry circle = new Geometry("window frame", cube2Mesh);
        Material cube2Mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        cube2Mat.setTexture("ColorMap", assetManager.loadTexture("Textures/blue-circle.png"));
        cube2Mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        circle.setQueueBucket(Bucket.Transparent);
        circle.setMaterial(cube2Mat);
        
        return circle;
    }
    
    private void initKeys(){
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Command", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "Select", "Command");
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Select") && !isPressed){
                CollisionResults results = new CollisionResults();
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(
                    new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(
                    new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                Ray ray = new Ray(click3d, dir);
                
                clickables.collideWith(ray, results);
                if(results.size()>0){
                    CollisionResult closest = results.getClosestCollision();
                    
                    if(closest.getGeometry().getName().equals("Floor")){
                        closest.getContactPoint().y=0;
                        mark.setLocalTranslation(closest.getContactPoint());
                        globals.setSelectedSprite(null);
                        rootNode.detachChild(globals.getCircle());
                        rootNode.attachChild(mark);
                    }
                    if(closest.getGeometry().getName().startsWith("shuttle")){
                        Vector3f place = closest.getGeometry().getWorldTranslation();
                        place.y=0;
                        globals.getCircle().setLocalTranslation(place);
                        globals.setSelectedSprite(closest.getGeometry().getParent());
                        rootNode.attachChild(globals.getCircle());
                        /*System.out.println("Circle moved: geometry pos: x-"+closest.getGeometry().getLocalTranslation().x+" z-"+
                                closest.getGeometry().getLocalTranslation().z+", geometry world pos: x-"+place.x+" z-"+place.z);*/
                    }
                }else{
                    rootNode.detachChild(mark);
                    rootNode.detachChild(globals.getCircle());
                }
            }
            
            if(name.equals("Command") && !isPressed){
                CollisionResults results = new CollisionResults();
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(
                    new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(
                    new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                Ray ray = new Ray(click3d, dir);
                
                clickables.collideWith(ray, results);
                if(results.size()>0){
                    CollisionResult closest = results.getClosestCollision();
                    
                    if(closest.getGeometry().getName().equals("Floor")){
                        closest.getContactPoint().y=0;
                        mark.setLocalTranslation(closest.getContactPoint());
                        if(globals.getSelectedSprite()!=null){
                            globals.getSelectedSprite().setUserData("moving", true);
                            globals.getSelectedSprite().setUserData("newPosition", closest.getContactPoint());
                        }
                        rootNode.attachChild(mark);
                    }
                }else{
                    rootNode.detachChild(mark);
                    rootNode.detachChild(globals.getCircle());
                }
            }
        }
    };
    
    @Override
    public void simpleUpdate(float tpf){
        //time += 2*tpf;
    }
    
    
    public static void main(String[] args){
        Main app = new Main();
        app.start();
    }
}
