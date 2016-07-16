/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.battle.control;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import projectSpace.Util;
import projectSpace.battle.BattleInput;
import projectSpace.battle.BattleState;

/**
 *
 * @author Administrator
 */
public class BattleControl extends AbstractControl implements ActionListener, AnalogListener {

    private static float MAX_ELAPSED_TIME_PER_CLICK = .2f;
    private final BattleState state;

    private Float elapsedTimeHoldingAction;
    private Vector3f initialSelection;

    public BattleControl(BattleState battleState) {
        this.state = battleState;
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

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(BattleInput.SELECT)) {
            if (isPressed) {
                selectActionPressed();
            } else {
                selectActionReleased();
            }
        }

        if (name.equals(BattleInput.COMMAND) && !isPressed) {
            commandActionReleased();
        }

        if (name.equals(BattleInput.BUILD) && !isPressed) {
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
        state.getFloor().collideWith(ray, collisions);

        if (collisions.size() > 0) {
            CollisionResult closestCollision = collisions.getClosestCollision();
            Vector3f contactPoint = closestCollision.getContactPoint();
            contactPoint.y = 0;
            return contactPoint;
        }
        return null;
    }

    private Ray cameraRay() {
        Vector2f click2d = state.getInputManager().getCursorPosition();
        Vector3f click3d = state.getCamera().getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = state.getCamera().getWorldCoordinates(
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
        CollisionResults clickableCollisions = clickableCollisions();

        if (clickableCollisions.size() > 0) {
            CollisionResult closestCollision = clickableCollisions.getClosestCollision();
            if (closestCollision.getGeometry().getName().equals("Floor")) {
                setFloorMark(closestCollision);
            }
            Spatial currentSprite;
            if (Util.DEVELOPMENT) {
                currentSprite = closestCollision.getGeometry();
            } else {
                currentSprite = closestCollision.getGeometry().getParent();
            }
            if (currentSprite.getName().equals("station")) {
                selectStation(currentSprite);
            } else {
                selectSprite(currentSprite);
            }
        } else {
            cleanSelection();
        }
    }

    private CollisionResults clickableCollisions() {
        CollisionResults collisions = new CollisionResults();
        Ray ray = cameraRay();
        state.getClickables().collideWith(ray, collisions);
        return collisions;
    }

    private void setFloorMark(CollisionResult closestCollision) {
        Vector3f contactPoint = new Vector3f(closestCollision.getContactPoint().x,
                0, closestCollision.getContactPoint().z);
        state.getMark().setLocalTranslation(contactPoint);
        state.clearSelectedSprite();
        state.clearSelectedBuilding();
        state.getRootNode().detachChild(state.getCircle());
        state.getRootNode().detachChild(state.getMark());
    }

    private void selectSprite(Spatial spatial) {
        Vector3f spriteLocation = new Vector3f(spatial.getLocalTranslation().x,
                0, spatial.getLocalTranslation().z);
        state.getCircle().setLocalScale(1, 1, 1);
        state.getCircle().setLocalTranslation(spriteLocation);
        state.getRootNode().attachChild(state.getCircle());

        state.setSelectedSpring(spatial);
        state.clearSelectedBuilding();
    }

    private void selectStation(Spatial spatial) {
        Vector3f spriteLocation = new Vector3f(spatial.getLocalTranslation().x,
                0, spatial.getLocalTranslation().z);
        state.getCircle().setLocalScale(3, 1, 3);
        state.getCircle().setLocalTranslation(spriteLocation);
        state.getRootNode().attachChild(state.getCircle());

        state.clearSelectedSprite();
        state.setSelectedBuilding(spatial);
    }

    private void cleanSelection() {
        state.getRootNode().detachChild(state.getMark());
        state.getRootNode().detachChild(state.getCircle());
    }

    private void determineSelectedObjects() {
        System.out.println("Calcular seleccion ahora");

        Box box = new Box(Vector3f.ZERO, Vector3f.ZERO);
        state.getRectangleSelection().setMesh(box);
        initialSelection = null;
    }

    private void commandActionReleased() {
        CollisionResults clickableCollisions = clickableCollisions();
        if (clickableCollisions.size() > 0) {
            if (state.getSelectedSprite() != null) {
                CollisionResult closestCollision = clickableCollisions.getClosestCollision();

                Spatial currentSprite;
                if (Util.DEVELOPMENT) {
                    currentSprite = closestCollision.getGeometry();
                } else {
                    currentSprite = closestCollision.getGeometry().getParent();
                }
                if (!currentSprite.getName().equals("station")) {
                    Vector3f contactPoint = new Vector3f(closestCollision.getContactPoint().x, 0, closestCollision.getContactPoint().z);
                    state.getSelectedSprite().getControl(AttackControl.class).attack(contactPoint);
                }
            }
        } else {
            Vector3f floorContactPoint = floorContactPoint();
            if (floorContactPoint != null) {
                if (state.getSelectedSprite() != null) {
                    state.getSelectedSprite().getControl(SpriteMovementControl.class).move(floorContactPoint);
                }
                if (state.getSelecteBuilding() != null) {
                    state.getSelecteBuilding().getControl(StationControl.class).createShip();
                }
                state.getMark().setLocalTranslation(floorContactPoint);
                state.getRootNode().attachChild(state.getMark());
            }
        }
    }

    private void buildActionReleased() {
        if (state.getSelectedSprite() != null && state.getSelectedSprite().getName().equals("worker")) {
            Spatial worker = state.getSelectedSprite();
            Vector3f buildLocation = new Vector3f(worker.getLocalTranslation().x + 1.5f,
                    0, worker.getLocalTranslation().z + 1.5f);
            worker.getControl(WorkerControl.class).build(buildLocation);
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        //falta
    }

}
