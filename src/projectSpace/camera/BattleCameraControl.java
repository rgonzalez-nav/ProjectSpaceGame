/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.camera;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Administrator
 */
public class BattleCameraControl extends AbstractControl implements ActionListener {

    private static final String PLAYER_NAME = "PlayerSpatial";
    private static final String DOWN = "Down";
    private static final String UP = "Up";
    private static final String RIGHT = "Right";
    private static final String LEFT = "Left";

    private ChaseCamera chaseCamera;
    private final Node node;
    private final AssetManager assetManager;
    private final Camera camera;
    private final InputManager inputManager;

    private final Vector3f movement = new Vector3f();
    private final Vector3f dirCamera = new Vector3f();
    private final Vector3f leftCamera = new Vector3f();

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private float cameraVelocity = .2f;
    private Vector3f inferiorLimit = Vector3f.NEGATIVE_INFINITY.clone();
    private Vector3f superiorLimit = Vector3f.POSITIVE_INFINITY.clone();

    public BattleCameraControl(Node node, AppStateManager stateManager) {
        this.node = node;
        this.camera = stateManager.getApplication().getCamera();
        this.assetManager = stateManager.getApplication().getAssetManager();
        this.inputManager = stateManager.getApplication().getInputManager();

        setUpSpatial(node);
        setUpChaseCamera(node);
        setUpKeys();
    }

    private void setUpSpatial(Node node) {
        Box box = new Box(.1f, .1f, .1f);
        spatial = new Geometry(node.getName() + PLAYER_NAME, box);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Yellow);
        spatial.setMaterial(material);
        spatial.setCullHint(Spatial.CullHint.Always);
    }

    private void setUpChaseCamera(Node node) {
        chaseCamera = new ChaseCamera(camera, spatial, inputManager);
        chaseCamera.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        spatial.addControl(this);
        node.attachChild(spatial);
    }

    private void setUpKeys() {
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, LEFT);
        inputManager.addListener(this, RIGHT);
        inputManager.addListener(this, UP);
        inputManager.addListener(this, DOWN);
    }

    public void setCameraVelocity(float cameraVelocity) {
        this.cameraVelocity = cameraVelocity;
    }

    public void setLimits(Vector3f inferior, Vector3f superior) {
        this.inferiorLimit = inferior;
        this.superiorLimit = superior;
        chaseCamera.setMinDistance(inferiorLimit.y);
        chaseCamera.setMaxDistance(superiorLimit.y);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (left || right || up || down) {
            setCameras();
            setMovePosition();
        }
    }

    private void setCameras() {
        Vector3f frontCamera = camera.getDirection().clone();
        frontCamera.y = 0;
        dirCamera.set(frontCamera).multLocal(cameraVelocity);
        leftCamera.set(camera.getLeft()).multLocal(cameraVelocity);
    }

    protected void setMovePosition() {
        getMovement();
        Vector3f nextPosition = spatial.getLocalTranslation().add(movement);
        if (nextPosition.x < superiorLimit.x && nextPosition.x > inferiorLimit.x) {
            spatial.move(movement.x, 0, 0);
        }
        if (nextPosition.z < superiorLimit.z && nextPosition.z > inferiorLimit.z) {
            spatial.move(0, 0, movement.z);
        }

    }

    private void getMovement() {
        //TODO:dirCamera needs to be normalized to transpose y value into x and z
        movement.set(0, 0, 0);
        if (left) {
            movement.addLocal(leftCamera);
        }
        if (right) {
            movement.addLocal(leftCamera.negate());
        }
        if (up) {
            movement.addLocal(dirCamera);
        }
        if (down) {
            movement.addLocal(dirCamera.negate());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case LEFT:
                left = isPressed;
                break;
            case RIGHT:
                right = isPressed;
                break;
            case UP:
                up = isPressed;
                break;
            case DOWN:
                down = isPressed;
                break;
            default:
                break;
        }
    }

}
