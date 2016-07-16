/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Spatial;

/**
 *
 * @author rafagonz
 */
public class InputControl extends AbstractControl implements ActionListener, AnalogListener {

    private static final float MAX_ELAPSED_TIME_PER_CLICK = .2f;
    private final InputManager inputManager;
    private final Camera cam;
    private final Node clickables;
    private final Globals globals;
    private final Node rootNode;
    private final Node sprites;
    private Vector3f initialSelection;
    private Float elapsedTimeHoldingAction;

    public InputControl(InputManager inputmanager, Camera cam,
            Node clickables, Globals globals, Node rootNode, Node sprites) {
        this.inputManager = inputmanager;
        this.cam = cam;
        this.clickables = clickables;
        this.globals = globals;
        this.rootNode = rootNode;
        rootNode.addControl(this);
        this.sprites = sprites;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Select")) {
            if (isPressed) {
                selectActionPressed();
            } else {
                selectActionReleased();
            }
        }

        if (name.equals("Command") && !isPressed) {
            commandActionReleased();
        }

        if (name.equals("Build") && !isPressed) {
            buildActionReleased();
        }
    }

    private void selectActionPressed() {
        initialSelection = floorContactPoint();
        elapsedTimeHoldingAction = 0f;
    }

    private Vector3f floorContactPoint() {
        CollisionResults collisions = new CollisionResults();
        Ray ray = cameraRay();
        globals.getFloor().collideWith(ray, collisions);

        if (collisions.size() > 0) {
            CollisionResult closestCollision = collisions.getClosestCollision();
            Vector3f contactPoint = closestCollision.getContactPoint();
            contactPoint.y = 0;
            return contactPoint;
        }
        return null;
    }

    private Ray cameraRay() {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        return ray;
    }

    private void selectActionReleased() {
        if (elapsedTimeHoldingAction < MAX_ELAPSED_TIME_PER_CLICK) {
            performSelectAction();
        } else {
            determineSelectedObjects();
        }
        elapsedTimeHoldingAction = null;
    }

    private void performSelectAction() {
        CollisionResults results = clickableCollisions();

        if (results.size() > 0) {
            CollisionResult closestCollision = results.getClosestCollision();
            if (closestCollision.getGeometry().getName().equals("Floor")) {
                setFloorMark(closestCollision);
            }
            Spatial currentSprite;
            if (Util.DEVELOPMENT) {
                currentSprite = closestCollision.getGeometry();
            } else {
                currentSprite = closestCollision.getGeometry().getParent();
            }

            if (currentSprite.getName().equals("ship")) {
                selectSprite(currentSprite);
            }
            if (currentSprite.getName().equals("worker")) {
                selectSprite(currentSprite);
            }

            if (currentSprite.getName().equals("station")) {
                selectStation(currentSprite);
            }
        } else {
            cleanSelection();
        }
    }

    private CollisionResults clickableCollisions() {
        CollisionResults collisions = new CollisionResults();
        Ray ray = cameraRay();
        clickables.collideWith(ray, collisions);
        return collisions;
    }

    private void setFloorMark(CollisionResult closestCollision) {
        Vector3f contactPoint = new Vector3f(closestCollision.getContactPoint().x,
                0, closestCollision.getContactPoint().z);
        globals.getMark().setLocalTranslation(contactPoint);
        globals.setSelectedSprite(null);
        globals.setSelectedBuilding(null);

        rootNode.detachChild(globals.getCircle());
        rootNode.attachChild(globals.getMark());
    }

    private void selectSprite(Spatial spatial) {
        Vector3f spriteLocation = new Vector3f(spatial.getLocalTranslation().x,
                0, spatial.getLocalTranslation().z);
        globals.getCircle().setLocalScale(1, 1, 1);
        globals.getCircle().setLocalTranslation(spriteLocation);
        globals.setSelectedSprite(spatial);
        globals.setSelectedBuilding(null);
        rootNode.attachChild(globals.getCircle());
    }

    private void selectStation(Spatial spatial) {
        Vector3f spriteLocation = new Vector3f(spatial.getLocalTranslation().x,
                0, spatial.getLocalTranslation().z);
        globals.getCircle().setLocalScale(3, 1, 3);
        globals.getCircle().setLocalTranslation(spriteLocation);
        globals.setSelectedSprite(null);
        globals.setSelectedBuilding(spatial);
        rootNode.attachChild(globals.getCircle());
    }

    private void cleanSelection() {
        rootNode.detachChild(globals.getMark());
        rootNode.detachChild(globals.getCircle());
    }

    private void determineSelectedObjects() {
        System.out.println("Termino la seleccion");
        initialSelection = null;
    }

    private void commandActionReleased() {
        CollisionResults collisions = clickableCollisions();

        if (collisions.size() > 0) {
            CollisionResult closest = collisions.getClosestCollision();

            Vector3f place = new Vector3f(closest.getContactPoint().x,
                    0, closest.getContactPoint().z);
            if (closest.getGeometry().getName().equals("Floor")) {
                globals.getMark().setLocalTranslation(place);
                if (globals.getSelectedSprite() != null) {
                    globals.getSelectedSprite().getControl(SpriteMovementControl.class).move(place);
                }
                if (globals.getSelectedBuilding() != null) {
                    globals.getSelectedBuilding().getControl(BuildingControl.class).create();
                }
                rootNode.attachChild(globals.getMark());
            }

            Spatial currentSprite;
            if (Util.DEVELOPMENT) {
                currentSprite = closest.getGeometry();
            } else {
                currentSprite = closest.getGeometry().getParent();
            }

            if (sprites.hasChild(currentSprite)) {
                Vector3f spriteLocation = new Vector3f(currentSprite.getLocalTranslation().x,
                        0, currentSprite.getLocalTranslation().z);
                if (globals.getSelectedSprite() != null) {
                    globals.getSelectedSprite().getControl(AttackControl.class)
                            .attack(currentSprite, spriteLocation);
                }
            }
        } else {
            cleanSelection();
        }
    }

    private void buildActionReleased() {
        if (globals.getSelectedSprite() != null
                && globals.getSelectedSprite().getName().equals("worker")) {
            Spatial currentSprite = globals.getSelectedSprite();
            Vector3f place = new Vector3f(currentSprite.getLocalTranslation().x + 1.5f,
                    0, currentSprite.getLocalTranslation().z + 1.5f);
            currentSprite.getControl(WorkerControl.class).build(place);
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("CursorUp") || name.equals("CursorDown") || name.equals("CursorRight") || name.endsWith("CursorLeft")) {
            if (initialSelection != null && elapsedTimeHoldingAction != null) {
                Vector3f contactPoint = floorContactPoint();
                if (contactPoint != null) {
                    Box box = new Box(initialSelection, contactPoint);
                    Geometry selectionRectangle = globals.getSelectionRectangle();
                    selectionRectangle.setMesh(box);
                }
            }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (elapsedTimeHoldingAction != null) {
            elapsedTimeHoldingAction += tpf;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
