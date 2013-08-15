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
public class FunctionValue implements ExpressionTerm {
    private String functionName;
    private String rawArguments;

    /**
     * TODO
     * 
     * @param functionName
     *            TODO
     * @param rawArguments
     *            TODO
     */
    public FunctionValue(String functionName, String rawArguments) {
        this.functionName = functionName;
        this.rawArguments = rawArguments;
    }

    /**
     * TODO Description
     * 
     * @param functionName
     *            TODO
     * @return TODO
     */
    public FunctionValue functionName(String functionName) {
        this.functionName = checkNotNull(functionName, "functionName cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String functionName() {
        return functionName;
    }

    /**
     * TODO Description
     * 
     * @param rawArguments
     *            TODO
     * @return TODO
     */
    public FunctionValue rawArguments(String rawArguments) {
        this.rawArguments = checkNotNull(rawArguments, "rawArguments cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
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
