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
import com.jme3.scene.Geometry;
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
    private final Animations animations;
    
    public InputControl(InputManager inputmanager, Camera cam, 
            Node clickables, Globals globals, Node rootNode, Animations animations){
        this.inputManager = inputmanager;
        this.cam = cam;
        this.clickables = clickables;
        this.globals = globals;
        this.rootNode = rootNode;
        this.animations = animations;
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
                   
                if(closest.getGeometry().getName().equals("Floor")){
                    closest.getContactPoint().y=0;
                    globals.getMark().setLocalTranslation(closest.getContactPoint());
                    globals.setSelectedSprite(null);
                    System.out.println("Selected position: "+closest.getContactPoint());
                    rootNode.detachChild(globals.getCircle());
                    rootNode.attachChild(globals.getMark());
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
                    }
                    rootNode.attachChild(globals.getMark());
                }
            }else{
                rootNode.detachChild(globals.getMark());
                rootNode.detachChild(globals.getCircle());
            }
        }
        if(name.equals("Shoot") && !isPressed){
            Geometry beam = animations.loadBeamAnimation();
            WeaponMovementControl beamMovementControl = new WeaponMovementControl(2, globals);
            beam.addControl(beamMovementControl);
            beam.rotate(0, 0, 0);
            beam.setUserData("newPosition", new Vector3f(-7.235211f,0,0f));
            beam.setUserData("growing", true);
            beam.setUserData("traveling", true);
            
            rootNode.attachChild(beam);
        }   
    }
    
}
