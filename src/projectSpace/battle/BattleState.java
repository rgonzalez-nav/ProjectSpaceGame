/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle;

import projectSpace.battle.control.AttackControl;
import projectSpace.battle.control.WorkerControl;
import projectSpace.battle.control.BattleControl;
import projectSpace.battle.control.SpriteMovementControl;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import projectSpace.ModelLoader;
import projectSpace.Weapons;
import projectSpace.camera.BattleCameraControl;
import projectSpace.debug.Debug;

/**
 *
 * @author Administrator
 */
public class BattleState extends AbstractAppState {

    private AppStateManager stateManager;
    private AssetManager assetManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private final Node rootNode;
    private Camera camera;
    private ModelLoader modelLoader;
    private Weapons weapons;

    private Geometry floor;
    private Node clickables;
    private Geometry mark;
    private Geometry circle;
    private Geometry rectangleSelection;
    private Spatial selectedSprite;
    private Spatial selectedBuilding;

    private BattleControl battleControl;

    public BattleState() {
        this.rootNode = new Node("Battle root node");
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.viewPort = app.getViewPort();
        this.camera = app.getCamera();
        this.modelLoader = new ModelLoader(assetManager);

        camera.setLocation(new Vector3f(0, 20, 20));
        camera.lookAt(Vector3f.ZERO, camera.getUp());
        inputManager.setCursorVisible(true);

        weapons = new Weapons(assetManager);
        clickables = new Node("Clickables");
        rootNode.attachChild(clickables);
        initLight();
        initMark();
        initFloor();
        initSelectionCircle();
        initSelectionRectangle();

        initKeys();
        //initFilterPostProcessor(); baja los fps, por eso comentado por ahora

        loadWorker();
    }

    private Spatial loadWorker() {
        Spatial worker = modelLoader.loadWorker();
        worker.addControl(new WorkerControl(this));
        worker.addControl(new AttackControl(this, AttackControl.Type.BULLET));
        worker.addControl(new SpriteMovementControl(2));
        clickables.attachChild(worker);
        return worker;
    }

    private void initLight() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }

    private Geometry initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("Click", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);

        return mark;
    }

    private Geometry initFloor() {
        Box box = new Box(15, .2f, 15);
        floor = new Geometry("Floor", box);
        floor.setLocalTranslation(0, -box.yExtent, 0);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.BlackNoAlpha);
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        floor.setMaterial(mat1);
        floor.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(floor);
        return floor;
    }

    private Geometry initSelectionCircle() {
        Box cubeMesh = new Box(0.5f, 0.001f, 0.5f);
        circle = new Geometry("selection circle", cubeMesh);
        Material cubeMat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        cubeMat.setTexture("ColorMap", assetManager.loadTexture("Textures/blue-circle.png"));
        cubeMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        circle.setQueueBucket(RenderQueue.Bucket.Transparent);
        circle.setMaterial(cubeMat);

        return circle;
    }

    private Geometry initSelectionRectangle() {

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setColor("Color", new ColorRGBA(255, 255, 255, 0.01f));

        Box box = new Box(Vector3f.ZERO.clone(), Vector3f.ZERO.clone());
        rectangleSelection = new Geometry("Selection rectangle", box);
        rectangleSelection.setMaterial(material);
        rectangleSelection.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(rectangleSelection);
        return rectangleSelection;
    }
    
    private Geometry initCameraChaser(){
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Yellow);
        Box box = new Box(.1f, .1f, .1f);
        
        Geometry target = new Geometry(rootNode + "cameraChaser", box);
        target.setMaterial(material);
        target.setCullHint(Spatial.CullHint.Always);
        target.setLocalTranslation(Vector3f.ZERO);
        return target;
    }

    private void initKeys() {
        battleControl = new BattleControl(this);

        inputManager.addMapping(BattleInput.SELECT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(BattleInput.COMMAND, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(BattleInput.BUILD, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(BattleInput.SHOOT, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(BattleInput.CURSOR_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(BattleInput.CURSOR_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(BattleInput.CURSOR_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(BattleInput.CURSOR_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addListener(battleControl, BattleInput.INPUTS);
        rootNode.addControl(battleControl);
    }

    private void initFilterPostProcessor() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    }

    public void enableDebug() {
        Debug debug = new Debug(assetManager);
        debug.showGrid(rootNode, 30);
        debug.showAxes(rootNode, 30);
        debug.showAxisArrows(rootNode, 5);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (selectedSprite != null) {
            circle.setLocalTranslation(selectedSprite.getLocalTranslation());
        }

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    public AppStateManager getStateManager() {
        return stateManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public Camera getCamera() {
        return camera;
    }

    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    public Weapons getWeapons() {
        return weapons;
    }

    public Geometry getFloor() {
        return floor;
    }

    public Node getClickables() {
        return clickables;
    }

    public Geometry getMark() {
        return mark;
    }

    public Geometry getCircle() {
        return circle;
    }

    public Geometry getRectangleSelection() {
        return rectangleSelection;
    }

    public Spatial getSelectedSprite() {
        return selectedSprite;
    }

    public Spatial getSelecteBuilding() {
        return selectedBuilding;
    }

    public BattleControl getBattleControl() {
        return battleControl;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void clearSelectedSprite() {
        this.selectedSprite = null;
    }

    public void clearSelectedBuilding() {
        this.selectedBuilding = null;
    }

    public void setSelectedSpring(Spatial spatial) {
        this.selectedSprite = spatial;
    }

    public void setSelectedBuilding(Spatial spatial) {
        this.selectedBuilding = spatial;
    }

}
