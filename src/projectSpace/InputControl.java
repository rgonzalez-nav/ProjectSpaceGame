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
import com.jme3.scene.Spatial;

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
    private final Node sprites;
    
    public InputControl(InputManager inputmanager, Camera cam, 
            Node clickables, Globals globals, Node rootNode, Node sprites){
        this.inputManager = inputmanager;
        this.cam = cam;
        this.clickables = clickables;
        this.globals = globals;
        this.rootNode = rootNode;
        this.sprites = sprites;
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
                System.out.println("Contact with: "+closest.getGeometry().getName());
                Vector3f place = new Vector3f(closest.getContactPoint().x,
                0, closest.getContactPoint().z);
                if(closest.getGeometry().getName().equals("Floor")){
                    globals.getMark().setLocalTranslation(place);
                    globals.setSelectedSprite(null);
                    globals.setSelectedBuilding(null);
                    
                    rootNode.detachChild(globals.getCircle());
                    rootNode.attachChild(globals.getMark());
                }
                Spatial currentSprite;
                if(Util.DEVELOPMENT){
                    currentSprite = closest.getGeometry();
                }else{
                    currentSprite = closest.getGeometry().getParent();
                }
                if(currentSprite.getName().equals("ship")){
                    Vector3f spriteLocation = new Vector3f(currentSprite.getLocalTranslation().x, 
                            0, currentSprite.getLocalTranslation().z);
                    globals.getCircle().setLocalScale(1, 1, 1);
                    globals.getCircle().setLocalTranslation(spriteLocation);
                    globals.setSelectedSprite(currentSprite);
                    globals.setSelectedBuilding(null);
                    rootNode.attachChild(globals.getCircle());
                    /*System.out.println("Circle moved: geometry pos: x-"+closest.getGeometry().getLocalTranslation().x+" z-"+
                            closest.getGeometry().getLocalTranslation().z+", geometry world pos: x-"+place.x+" z-"+place.z);*/
                }
                if(currentSprite.getName().equals("station")){
                    Vector3f spriteLocation = new Vector3f(currentSprite.getLocalTranslation().x, 
                            0, currentSprite.getLocalTranslation().z);
                    globals.getCircle().setLocalScale(3, 1, 3);
                    globals.getCircle().setLocalTranslation(spriteLocation);
                    globals.setSelectedSprite(null);
                    globals.setSelectedBuilding(currentSprite);
                    rootNode.attachChild(globals.getCircle());
                }
                if(currentSprite.getName().equals("worker")){
                    Vector3f spriteLocation = new Vector3f(currentSprite.getLocalTranslation().x, 
                            0, currentSprite.getLocalTranslation().z);
                    globals.getCircle().setLocalScale(1, 1, 1);
                    globals.getCircle().setLocalTranslation(spriteLocation);
                    globals.setSelectedSprite(currentSprite);
                    globals.setSelectedBuilding(null);
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
                Vector3f place = new Vector3f(closest.getContactPoint().x,
                    0, closest.getContactPoint().z);
                if(closest.getGeometry().getName().equals("Floor")){
                    globals.getMark().setLocalTranslation(place);
                    if(globals.getSelectedSprite()!=null){
                        globals.getSelectedSprite().getControl(SpriteMovementControl.class).move(place);
                    }
                    if(globals.getSelectedBuilding()!=null){
                        globals.getSelectedBuilding().getControl(BuildingControl.class).create();
                    }
                    rootNode.attachChild(globals.getMark());
                }
                
                Spatial currentSprite;
                if(Util.DEVELOPMENT){
                    currentSprite = closest.getGeometry();
                }else{
                    currentSprite = closest.getGeometry().getParent();
                }
                
                if(sprites.hasChild(currentSprite)){
                    Vector3f spriteLocation = new Vector3f(currentSprite.getLocalTranslation().x, 
                            0, currentSprite.getLocalTranslation().z);
                    if(globals.getSelectedSprite()!=null){
                        globals.getSelectedSprite().getControl(AttackControl.class)
                                .attack(currentSprite, spriteLocation);
                    }
                }
            }else{
                rootNode.detachChild(globals.getMark());
                rootNode.detachChild(globals.getCircle());
            }
        }
        if(name.equals("Build") && !isPressed){
            if(globals.getSelectedSprite()!= null && 
                    globals.getSelectedSprite().getName().equals("worker")){
                Spatial currentSprite = globals.getSelectedSprite();
                Vector3f place = new Vector3f(currentSprite.getLocalTranslation().x+1.5f,
                        0, currentSprite.getLocalTranslation().z+1.5f);
                currentSprite.getControl(WorkerControl.class).build(place);
            }
        }
    }
    
}
