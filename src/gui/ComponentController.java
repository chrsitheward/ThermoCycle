/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.Arrays;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import static javafx.scene.input.MouseButton.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public final class ComponentController extends AnchorPane {
    
    // FXML variables
    @FXML private AnchorPane base;
    @FXML private AnchorPane icon;
    @FXML private VBox stack;
    @FXML protected GridPane node_grid;
    @FXML private Label name;
    private ContextMenu menu;
    private Tooltip tip;
    
    // Set master
    protected final MasterSceneController master;
    
    // Properties
    protected ObjectProperty<Point2D> centerInLocal;
    
    // Model variables
    protected Component component;
    
    // Component type
    private ComponentIcon iType;
    
    
    /**
     * Constructor
     */
    public ComponentController(MasterSceneController master, boolean modelComponent) {
        
        // Set master
        this.master = master;
        
        // Create properties
        centerInLocal = new SimpleObjectProperty();
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Component.fxml"));
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        if (modelComponent) {
            // Model component handlers
            buildDragHandlersForCanvas();
            buildClickHandlersForCanvas();
            node_grid.visibleProperty().bind(this.master.nodeVisibility);
            name.visibleProperty().bind(this.master.nameVisibility);
        }
        else {
            // Toolbox component handlers
            buildDragHandlersForToolbox();
        }
        
        // Set cursor type
        cursorProperty().setValue(Cursor.OPEN_HAND);
        
    }
    
    /**
     * Initializer
     */
    @FXML
    private void initialize() {
        // Create bindings
        centerInLocal.bind(new ObjectBinding<Point2D>() {
            {
                bind(ComponentController.this.widthProperty(), ComponentController.this.heightProperty());
            }
            @Override
            protected Point2D computeValue() {
                return new Point2D(icon.widthProperty().getValue()/2.0, icon.heightProperty().getValue()/2.0);
            }
        });
    }
    
    /**
     * Build click handers for this node when it's put on a canvas.
     */
    private void buildClickHandlersForCanvas() {
        
        ComponentController.this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                // Set focus to component if the single primary click
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 1) {
                        if (!event.isControlDown()) {
                            master.canvas.canvasClipboard.clear();
                            master.setFocus(ComponentController.this);
                        }
                        master.canvas.canvasClipboard.add(ComponentController.this);
                    }
                }
                else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    // Create context menu
                    MenuItem mi = new MenuItem("Delete");
                    mi.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            master.canvas.remove(ComponentController.this);
                        }
                    });
                    ContextMenu contextMenu = new ContextMenu();
                    contextMenu.getItems().add(mi);
                    contextMenu.show(ComponentController.this, event.getScreenX(), event.getScreenY());
                }
                
                // Consume event to stop it bubbling
                event.consume();
                
            }
        });
        
        ComponentController.this.name.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               
               // Check for double click on label
               if (event.getButton().equals(PRIMARY)) {
                   if (event.getClickCount() == 2) {
                       TextField input = new TextField();
                       input.prefWidthProperty().bind(name.prefWidthProperty());
                       input.prefHeightProperty().bind(name.prefHeightProperty());
                       input.fontProperty().bind(name.fontProperty());
                       input.setText(name.getText());
                       input.alignmentProperty().bind(name.alignmentProperty());
                       //name.setVisible(false);
                       input.setOnAction(new EventHandler<ActionEvent>() {
                           @Override
                           public void handle(ActionEvent event) {
                               name.setText(input.getText());
                               stack.getChildren().remove(input);
                               stack.getChildren().add(name);
                               //name.setVisible(true);
                               master.getModel().setName(component, name.getText());
                               master.setFocus(null);
                               master.setFocus(ComponentController.this);
                           }
                       });
                       stack.getChildren().remove(name);
                       stack.getChildren().add(input);
                   }
               }
               
               // Don't consume event to allow it to bubble to the 
               // ????
               
           }
            
        });
        
    }
    
    /**
     * Build drag handlers for this node when it's put on a canvas.
     */
    private final void buildDragHandlersForCanvas() {
        
       this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(CanvasController.MOVE_COMPONENT,component);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                //setMouseTransparent(true);
                
                // Consume event to make make sure canvas drag detected event isn't fired.
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                //setMouseTransparent(false);
            }
        });
        
    }
    
    /**
     * Builds the drag handlers for this node when put in the toolbox.
     */
    private final void buildDragHandlersForToolbox() {
        
       this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(CanvasController.CREATE_COMPONENT,iType);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                //setMouseTransparent(true);
                
                // Prepare the canvas component
                master.canvas.dragIcon.setStyle(iType);
                master.canvas.dragIcon.relocateToPointInScene(new Point2D (event.getSceneX(), event.getSceneY()));
                master.canvas.dragIcon.setVisible(true);
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                // Hide canvas drag icon
                master.canvas.dragIcon.setVisible(false);
                
                // Change cursor
                cursorProperty().setValue(Cursor.OPEN_HAND);
                
                // Consume event
                event.consume();
                
            }
        });
        
    }
    
    /**
     * Builds the tool tip for this component
     */
    protected void buildTooltip() {
        tip = new Tooltip();
        Tooltip.install(base, tip);
        tip.setOnShowing(new EventHandler() {
            @Override
            public void handle(Event event) {
                tip.setText(component.toString());
            }
        });
    }
    
    /**
     * Creates the thermodynamic cycle component based on the icon type.
     */
    protected final void createComponent(ComponentIcon iType) {
        // Sets the component style
        setStyle(iType);
        // Create component
        switch (this.iType) {
            case COMBUSTOR:
                component = master.getModel().createCombustor(iType.name);
                break;
            case COMPRESSOR:
                component = master.getModel().createCompressor(iType.name);
                break;
            case HEAT_EXCHANGER:
                component = master.getModel().createHeatExchanger(iType.name);
                break;
            case HEAT_SINK:
                component = master.getModel().createHeatSink(iType.name);
                break;
            case TURBINE:
                component = master.getModel().createTurbine(iType.name);
                break;
        }
        // Add nodes
        createNodes();
    }
    
    /**
     * Creates the thermodynamic cycle component based on the thermocycle component.
     */
    protected void createComponent(Component component) {
        // Assigns the thermocycle component
        this.component = component;
        // Assigns the icon type based on component class
        Arrays.asList(ComponentIcon.values()).stream().filter(c -> component.getClass().equals(c.type)).findFirst().ifPresent(c -> {
            setStyle(c);
        });
        // Add nodes
        createNodes();
    }
    
    /**
     * Adds the nodes to the component.
     */
    private void createNodes() {
        // Adds nodes to the component
        component.flowNodes.keySet().stream().forEach(k -> {
            int[] i = iType.nodes.get(k);
            node_grid.add(new NodeController(master, ComponentController.this, component.flowNodes.get(k)), i[0], i[1]);
        });
        component.workNodes.keySet().stream().forEach(k -> {
            int[] i = iType.nodes.get(k);
            node_grid.add(new NodeController(master, ComponentController.this, component.workNodes.get(k)), i[0], i[1]);
        });

        component.heatNodes.keySet().stream().forEach(k -> {
            int[] i = iType.nodes.get(k);
            node_grid.add(new NodeController(master, ComponentController.this, component.heatNodes.get(k)), i[0], i[1]);
        });
    }
    
    /**
     * Relocates the toolbox icon to the specified point in scene co-ordinates.
     * @param scenePoint the point to relocate to in the scene co-ordinates.
     */
    protected final void relocateToPointInScene(Point2D scenePoint) {
        Point2D parentPoint = getParent().sceneToLocal(scenePoint);
        this.relocate((int) (parentPoint.getX() - (centerInLocal.getValue().getX())), (int) (parentPoint.getY() - centerInLocal.getValue().getY()));
    }
    
    /**
     * Rotates the icon 90 degrees counter-clockwise;
     */
    public void rotateCCW() {
        icon.rotateProperty().setValue(icon.rotateProperty().getValue() - 90.0);
    }
    
    /**
     * Rotates the icon 90 degrees clockwise.
     */
    public void rotateCW() {
        icon.rotateProperty().setValue(icon.rotateProperty().getValue() + 90.0);
    }
    
    /**
     * Sets the icon style. Must only be called after type is defined.
     * @param iType the icon type to set.
     */
    protected final void setStyle(ComponentIcon iType) {
        this.iType = iType;
        icon.getStyleClass().clear();
        icon.getStyleClass().add(iType.css);
        icon.getStyleClass().add("icon");
        name.setText(iType.name);
    }
    
}
