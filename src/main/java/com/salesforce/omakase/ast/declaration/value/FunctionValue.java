/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.EmittableRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * A generic function value with non-validated arguments.
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "individual function value", broadcasted = REFINED_DECLARATION)
public class FunctionValue extends AbstractSyntax implements Term {
    private String name;
    private String args;

    /**
     * Constructs a new {@link FunctionValue} instance with the given function name and arguments.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            The name of the function.
     * @param args
     *            The raw, non-validated function arguments.
     */
    public FunctionValue(int line, int column, String name, String args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    /**
     * Sets the function name.
     * 
     * @param functionName
     *            The function name.
     * @return this, for chaining.
     */
    public FunctionValue name(String functionName) {
        this.name = checkNotNull(functionName, "name cannot be null");
        return this;
    }

    /**
     * Gets the function name.
     * 
     * @return The function name.
     */
    public String name() {
        return name;
    }

    /**
     * Sets the raw arguments.
     * 
     * @param args
     *            The arguments.
     * @return this, for chaining.
     */
    public FunctionValue args(String args) {
        this.args = checkNotNull(args, "args cannot be null");
        return this;
    }

    /**
     * Gets the raw arguments.
     * 
     * @return The raw arguments.
     */
    public String args() {
        return args;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("name", name)
            .add("args", args)
            .toString();
    }
}
