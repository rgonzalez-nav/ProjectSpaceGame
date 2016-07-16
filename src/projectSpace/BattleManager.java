/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import projectSpace.camera.BattleCameraControl;
import projectSpace.debug.Debug;
import projectSpace.input.BattleInput;

/**
 *
 * @author rafagonz
 */
public class BattleManager extends SimpleApplication{
    private static final Vector3f CAMERA_INITIAL_LOCATION = new Vector3f(0f, 10f, 20f);
    private static final String SELECTION_RECTANGLE_NAME = "SelectionRectangle";
    
    private Globals globals;
    Node clickables;
    Node sprites;
    private InputControl inputControl;
    private SkyboxGenerator generator;
    private Models models;
    
    private Geometry initMark(){
        Sphere sphere = new Sphere(30, 30, 0.2f);
        Geometry mark = new Geometry("Click", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        
        return mark;
    }
    
    private void initKeys(){
        inputManager.addMapping(BattleInput.SELECT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(BattleInput.COMMAND, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(BattleInput.BUILD, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(BattleInput.SHOOT, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(BattleInput.CURSOR_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(BattleInput.CURSOR_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(BattleInput.CURSOR_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(BattleInput.CURSOR_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addListener(inputControl, BattleInput.INPUTS);
    }
    
    private Geometry makeFloor(){
        Box box = new Box(15, .2f, 15);
        Geometry floor = new Geometry("Floor", box);
        floor.setLocalTranslation(0,-box.yExtent, 0);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.BlackNoAlpha);
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        floor.setMaterial(mat1);
        floor.setQueueBucket(RenderQueue.Bucket.Transparent);
        globals.setFloor(floor);
        return floor;
    }
    
    private Geometry paintCircle(){
        Box cubeMesh = new Box( 0.5f,0.001f,0.5f);
        Geometry circle = new Geometry("selection circle", cubeMesh);
        Material cubeMat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        cubeMat.setTexture("ColorMap", assetManager.loadTexture("Textures/blue-circle.png"));
        cubeMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        circle.setQueueBucket(RenderQueue.Bucket.Transparent);
        circle.setMaterial(cubeMat);
        
        return circle;
    }
    
    private Spatial loadBuilding(){
        Spatial station = models.loadStation();
        station.addControl(new BuildingControl(globals, assetManager, rootNode, models));
        return station;
    }
    
    private Spatial loadWorker(){
        Spatial worker = models.loadWorker();
        worker.addControl(new WorkerControl(globals, assetManager, rootNode, sprites, models));
        worker.addControl(new AttackControl(globals, assetManager, rootNode, 2));
        worker.addControl(new SpriteMovementControl(2, globals));
        return worker;
    }

    @Override
    public void simpleInitApp() {
        globals = new Globals();
        clickables = new Node();
        clickables.setName("clickables");
        sprites = new Node();
        sprites.setName("sprites");
        generator = new SkyboxGenerator(assetManager, rootNode);
        //generator.createSky();
        inputControl = new InputControl(inputManager, cam, 
                clickables, globals, rootNode, sprites);
        models = new Models(assetManager);
        
        initKeys();
        globals.setMark(initMark());
        
        initBattleCamera();
        
        globals.setCircle(paintCircle());
        globals.setGlobalSpeed(1f);
        globals.setWeapons(new Weapons(assetManager));
        clickables.attachChild(loadBuilding());
        sprites.attachChild(loadWorker());
        
        clickables.attachChild(makeFloor());
        clickables.attachChild(sprites);
        rootNode.attachChild(clickables);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        initSelectionRectangle();
        
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom= new BloomFilter(BloomFilter.GlowMode.Objects);        
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        
        enableDebug();
    }
    
    private Geometry initSelectionRectangle() {
  
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setColor("Color", new ColorRGBA(255, 255, 255, 0.01f));
        
        Box box = new Box(Vector3f.ZERO.clone(), Vector3f.ZERO.clone());
        Geometry geometry = new Geometry(SELECTION_RECTANGLE_NAME, box);
        geometry.setMaterial(material);
        geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(geometry);
        globals.setSelectionRectangle(geometry);
        return geometry;
    }

    private void initBattleCamera() {
        flyCam.setEnabled(false);
        BattleCameraControl battleCamera = new BattleCameraControl(rootNode, stateManager);
        battleCamera.setLimits(new Vector3f(-15f, 2, -15),new Vector3f(15f, 30, 15));
    }

    private void enableDebug() {
        Debug debug = new Debug(assetManager);
        debug.showGrid(rootNode, 30);
        debug.showAxes(rootNode, 30);
        debug.showAxisArrows(rootNode, 5);
    }
}
