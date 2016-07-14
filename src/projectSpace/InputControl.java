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
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author rafagonz
 */
public class InputControl implements ActionListener{
    private final InputManager inputManager;
    private final Camera cam;
    private final Node clickables;
    private final Globals globals;
    private final Node rootNode;
    
    public InputControl(InputManager inputmanager, Camera cam, 
            Node clickables, Globals globals, Node rootNode){
        this.inputManager = inputmanager;
        this.cam = cam;
        this.clickables = clickables;
        this.globals = globals;
        this.rootNode = rootNode;
    }

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
                Vector3f place = new Vector3f(closest.getGeometry().getWorldTranslation().x,
                    0, closest.getGeometry().getWorldTranslation().z);
                
                if(closest.getGeometry().getName().equals("Floor")){
                    closest.getContactPoint().y=0;
                    globals.getMark().setLocalTranslation(closest.getContactPoint());
                    globals.setSelectedSprite(null);
                    globals.setSelectedBuilding(null);
                    rootNode.detachChild(globals.getCircle());
                    rootNode.attachChild(globals.getMark());
                }
                if(closest.getGeometry().getParent().getName().equals("ship")){
                    globals.getCircle().setLocalScale(1, 1, 1);
                    globals.getCircle().setLocalTranslation(place);
                    globals.setSelectedSprite(closest.getGeometry().getParent());
                    globals.setSelectedBuilding(null);
                    rootNode.attachChild(globals.getCircle());
                    /*System.out.println("Circle moved: geometry pos: x-"+closest.getGeometry().getLocalTranslation().x+" z-"+
                            closest.getGeometry().getLocalTranslation().z+", geometry world pos: x-"+place.x+" z-"+place.z);*/
                }
                if(closest.getGeometry().getParent().getName().equals("station")){
                    globals.getCircle().setLocalScale(3,1,3);
                    globals.getCircle().setLocalTranslation(place);
                    globals.setSelectedBuilding(closest.getGeometry().getParent());
                    globals.setSelectedSprite(null);
                    rootNode.attachChild(globals.getCircle());
                }
            }else{
                rootNode.detachChild(globals.getMark());
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
                    globals.getMark().setLocalTranslation(closest.getContactPoint());
                    if(globals.getSelectedSprite()!=null){
                        globals.getSelectedSprite().setUserData("moving", true);
                        globals.getSelectedSprite().setUserData("newPosition", closest.getContactPoint());
                    }else if(globals.getSelectedBuilding()!=null){
                        globals.getSelectedBuilding().setUserData("creating", true);
                    }
                    rootNode.attachChild(globals.getMark());
                }
                if(closest.getGeometry().getName().startsWith("shuttle")){
                    closest.getContactPoint().y=0;
                    if(globals.getSelectedSprite()!=null){
                        globals.getSelectedSprite().setUserData("shooting", true);
                        globals.getSelectedSprite().setUserData("enemy", closest.getGeometry().getParent());
                        globals.getSelectedSprite().setUserData("target", closest.getContactPoint());
                    }
                }
            }else{
                rootNode.detachChild(globals.getMark());
                rootNode.detachChild(globals.getCircle());
            }
        }
    }
    
}
