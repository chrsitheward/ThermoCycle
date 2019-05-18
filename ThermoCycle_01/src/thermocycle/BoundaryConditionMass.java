/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionMass extends BoundaryCondition {
    
    public final FlowNode node;
    
    public BoundaryConditionMass(FlowNode node, List<Double> values) {
        super(values);
        this.node = node;
    }
    
    @Override
    protected void execute() {
        node.setMass(getValue());
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionMass) {
            if (this.node == ((BoundaryConditionMass)cnd).node) {
                return true;
            }
        }
        return false;
    }
    
}