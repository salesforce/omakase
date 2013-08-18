/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class FunctionValue implements Term {
    private String functionName;
    private String rawArguments;

    /**
     * Constructs a new {@link FunctionValue} instance with the given function name and arguments.
     * 
     * @param functionName
     *            The name of the function.
     * @param rawArguments
     *            The raw, non-validated function arguments.
     */
    public FunctionValue(String functionName, String rawArguments) {
        this.functionName = functionName;
        this.rawArguments = rawArguments;
    }

    /**
     * Sets the function name.
     * 
     * @param functionName
     *            The function name.
     * @return this, for chaining.
     */
    public FunctionValue functionName(String functionName) {
        this.functionName = checkNotNull(functionName, "functionName cannot be null");
        return this;
    }

    /**
     * Gets the function name.
     * 
     * @return The function name.
     */
    public String functionName() {
        return functionName;
    }

    /**
     * Sets the raw arguments.
     * 
     * @param rawArguments
     *            The arguments.
     * @return this, for chaining.
     */
    public FunctionValue rawArguments(String rawArguments) {
        this.rawArguments = checkNotNull(rawArguments, "rawArguments cannot be null");
        return this;
    }

    /**
     * Gets the raw arguments.
     * 
     * @return The raw arguments.
     */
    public String rawArguments() {
        return rawArguments;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("name", functionName)
            .add("args", rawArguments)
            .toString();
    }
}
