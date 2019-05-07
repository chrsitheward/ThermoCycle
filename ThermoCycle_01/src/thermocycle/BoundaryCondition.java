/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Chris
 */
public abstract class BoundaryCondition implements Serializable {
    
    /**
     * The logger instance.
     */
    private static final Logger logger = LogManager.getLogger("DebugLog");
    
    /**
     * The current index of all boundary conditions.
     */
    private static int idx = 1;
    
    /**
     * An array list of the boundary condition values.
     */
    private final ArrayList<Double> values;
    
    /**
     * Constructor for parametric boundary condition
     * @param values The array of boundary condition values.
     */
    public BoundaryCondition(List<Double> values) {
        this.values = new ArrayList(values);
        logger.debug("Boundary condition values are: ");
        this.values.stream().forEach(v -> {
            logger.debug(" - " + v.toString());
        });
    }
    
    /**
     * Constructor for constant boundary condition
     * @param values The boundary condition value.
     */
    public BoundaryCondition(double value) {
        this.values = new ArrayList();
        this.values.add(value);
    }
    
    /**
     * Gets the current value of the boundary condition. IF the
     * boundary condition is a single value this always returns
     * that value. If the boundary condition is parametric then
     * it return the value of the current index. This is called
     * during the execute command.
     * @return the current value of the boundary condition.
     */
    public final double getValue() {
        if (values.size() == 1) {
            return values.get(0);
        }
        return this.values.get(idx);
    }
    
    /**
     * Gets the current size of the boundary condition.
     * @return the number of values in the boundary condition.
     */
    public final int getSize() {
        return values.size();
    }
    
    /**
     * Sets the current boundary condition index.
     * @param i The new index value.
     */
    public static void setIndx(int i) {
        if (i<1) {
            logger.error("Boundary conditon index cannot be less than 1.");
        }
        idx = i;
    }
    
    /**
     * Gets the current boundary condition index.
     */
    public static int getIdx() {
        return idx;
    }
    
    /**
     * Increment the boundary condition index by 1.
     * @return the new boundary condition index.
     */
    public static int incrementIndex() {
        return idx++;
    }
    
    /**
     * Executes the boundary condition by applying the current value
     * to the model.
     */
    protected abstract void execute();
    
    /**
     * Checks to see if this boundary condition matches another. Boundary
     * conditions match if they are applied to the same object.
     * @param cnd The boundary condition to check against for a match.
     * @return true if the boundary conditions match, otherwise false.
     */
    protected abstract boolean match(BoundaryCondition cnd);
    
}
