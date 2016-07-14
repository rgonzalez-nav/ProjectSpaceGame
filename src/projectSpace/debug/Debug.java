/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;

/**
 *
 * @author jhovanni
 */
public class Debug {

    private static final String AXIS_Z_NAME = "axisZ";
    private static final String AXIS_Y_NAME = "axisY";
    private static final String AXIS_X_NAME = "axisX";
    private static final String GRID_NAME = "wireFrameDebugGrid";

    private static final ColorRGBA Z_AXIS_COLOR = ColorRGBA.Blue;
    private static final ColorRGBA Y_AXIS_COLOR = ColorRGBA.Green;
    private static final ColorRGBA X_AXIS_COLOR = ColorRGBA.Red;
    private static final ColorRGBA GRID_COLOR = ColorRGBA.DarkGray;

    private final AssetManager assetManager;

    public Debug(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void showAxes(Node node, float length) {
        length/=2;
        Vector3f dimension = new Vector3f(length, 0, 0);
        createAxis(node, AXIS_X_NAME, X_AXIS_COLOR, dimension);

        dimension = new Vector3f(0, length, 0);
        createAxis(node, AXIS_Y_NAME, Y_AXIS_COLOR, dimension);

        dimension = new Vector3f(0, 0, length);
        createAxis(node, AXIS_Z_NAME, Z_AXIS_COLOR, dimension);
    }

    private void createAxis(Node node, String name, ColorRGBA color, Vector3f dimension) {
        Line line = new Line(Vector3f.ZERO.subtract(dimension), dimension);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        Geometry geometry = new Geometry(node.getName() + name, line);
        geometry.setMaterial(material);
        node.attachChild(geometry);
    }

    public void showAxisArrows(Node node, float length) {
        Vector3f dimension = new Vector3f(length, 0, 0);
        createArrow(node, AXIS_X_NAME, X_AXIS_COLOR, dimension);

        dimension = new Vector3f(0, length, 0);
        createArrow(node, AXIS_Y_NAME, Y_AXIS_COLOR, dimension);

        dimension = new Vector3f(0, 0, length);
        createArrow(node, AXIS_Z_NAME, Z_AXIS_COLOR, dimension);
    }

    private void createArrow(Node node, String name, ColorRGBA color, Vector3f dimension) {
        Arrow arrow = new Arrow(dimension);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        Geometry geom = new Geometry(node.getName() + name, arrow);
        geom.setMaterial(mat);
        node.attachChild(geom);
    }

    public void showGrid(Node node, Integer size) {
        Grid grid = new Grid(size, size, 1f);
        Geometry geometry = new Geometry(node.getName() + GRID_NAME, grid);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", GRID_COLOR);
        geometry.setMaterial(material);
        geometry.center().move(Vector3f.ZERO);
        node.attachChild(geometry);
    }

}
