package aima.core.search.csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * An assignment assigns values to some or all variables of a CSP.
 *
 * @author Ruediger Lunde
 */
public class Assignment {
    /**
     * Contains all assigned variables. Positions reflect the the order in which
     * the variables were assigned to values.
     */
    private List<Variable> variables;
    /**
     * Maps variables to their assigned values.
     */
    private Hashtable<Variable, Object> variableToValue;

    public Assignment() {
        variables = new ArrayList<>();
        variableToValue = new Hashtable<>();
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public Object getValue(Variable var) {
        return variableToValue.get(var);
    }

    public void add(Variable var, Object value) {
        if (variableToValue.put(var, value) == null)
            variables.add(var);
    }

    public void remove(Variable var) {
        if (contains(var)) {
            variableToValue.remove(var);
            variables.remove(var);
        }
    }

    public boolean contains(Variable var) {
        return variableToValue.get(var) != null;
    }

    /**
     * Returns true if this assignment does not violate any constraints of
     * <code>constraints</code>.
     */
    public boolean isConsistent(List<Constraint> constraints) {
        for (Constraint cons : constraints)
            if (!cons.isSatisfiedWith(this))
                return false;
        return true;
    }

    /**
     * Returns true if this assignment assigns values to every variable of
     * <code>vars</code>.
     */
    public boolean isComplete(List<Variable> vars) {
        for (Variable var : vars) {
            if (!contains(var))
                return false;
        }
        return true;
    }

    /**
     * Returns true if this assignment is consistent as well as complete with
     * respect to the given CSP.
     */
    public boolean isSolution(CSP csp) {
        return isConsistent(csp.getConstraints())
                && isComplete(csp.getVariables());
    }

    public Assignment copy() {
        Assignment copy = new Assignment();
        for (Variable var : variables) {
            copy.add(var, variableToValue.get(var));
        }
        return copy;
    }

    @Override
    public String toString() {
        boolean comma = false;
        StringBuilder result = new StringBuilder("{");
        for (Variable var : variables) {
            if (comma)
                result.append(", ");
            result.append(var).append("=").append(variableToValue.get(var));
            comma = true;
        }
        result.append("}");
        return result.toString();
    }
}