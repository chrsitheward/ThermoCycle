/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 *
 * @author Chris
 */
public class ToolboxPath extends Path{
    
    /**
     * Constructor
     */
    public void ToolboxPath() {
    }
    
    /**
     * Starts the drag operation by setting the style and the start location of the path
     * @param canvasNode Te canvas node to start the drag opperation from.
     */
    protected void startDrag(CanvasNode canvasNode) {
        // Set up drag path form
        getElements().clear();
        getElements().add(new MoveTo());
        getElements().add(new LineTo());
        // set Style
        setStyle(canvasNode);
        // Move to start
        Bounds bounds = canvasNode.getBoundsInLocal();
        Point2D point = canvasNode.localToScene(bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight()/2);
        dragFrom(point.getX(),point.getY());
    }
    
    /**
     * Sets the style for the path
     * @param canvasNode 
     */
    protected void setStyle(CanvasNode canvasNode) {
        getStyleClass().clear();
        if (canvasNode.node instanceof thermocycle.FlowNode) {
            getStyleClass().add("path-flow");
        }
        else if (canvasNode.node instanceof thermocycle.HeatNode) {
            getStyleClass().add("path-heat");
        }
        else if (canvasNode.node instanceof thermocycle.WorkNode) {
            getStyleClass().add("path-work");
        }
        else {
            // Something's gone wrong
        }
    }
    
    /**
     * Drag line from new scene location
     * @param x X co-ordinate to drag from
     * @param y Y co-ordinate to drag from
     */
    private void dragFrom(double x, double y) {
        Point2D point = sceneToLocal(x, y);
        ((MoveTo)first()).setX(point.getX());
        ((MoveTo)first()).setY(point.getY());
    }
    
    /**
     * Drag line to new scene location
     * @param x X co-ordinate to drag to
     * @param y Y co-ordinate to drag to
     */
    protected void dragTo(double x, double y) {
        Point2D point = this.sceneToLocal(x, y);
        ((LineTo)last()).setX(point.getX());
        ((LineTo)last()).setY(point.getY());
    }
    
    /**
     * Get the first element in the ToolboxPath
     * @return Returns the first element in the Toolbox
     */
    protected PathElement first() {
        return getElements().get(0);
    }
    
    /**
     * Get the last element in ToolboxPath
     * @return Returns the last element in the ToolboxPath
     */
    protected PathElement last() {
        return getElements().get(getElements().size()-1);
    }
    
}