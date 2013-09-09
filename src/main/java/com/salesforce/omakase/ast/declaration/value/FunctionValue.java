/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.declaration.FunctionValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

/**
 * TESTME
 * <p/>
 * A generic function value with non-validated arguments. By not validating arguments here, we allow for new CSS specifications as
 * well as custom functions with any arbitrary content.
 *
 * @author nmcwilliams
 * @see FunctionValueParser
 */
@Subscribable
@Description(value = "individual function value", broadcasted = REFINED_DECLARATION)
public class FunctionValue extends AbstractSyntax implements Term {
    private String name;
    private String args;

    /**
     * Constructs a new {@link FunctionValue} instance with the given function name and arguments.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The name of the function.
     * @param args
     *     The raw, non-validated function arguments.
     */
    public FunctionValue(int line, int column, String name, String args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    /**
     * Constructs a new {@link FunctionValue} instance with the given function name and arguments (used for dynamically created
     * {@link com.salesforce.omakase.ast.Syntax} units).
     *
     * @param name
     *     The name of the function.
     * @param args
     *     The function arguments.
     */
    @SuppressWarnings("UnusedDeclaration")
    public FunctionValue(String name, String args) {
        name(name);
        args(args);
    }

    /**
     * Sets the function name.
     *
     * @param name
     *     The function name.
     *
     * @return this, for chaining.
     */
    public FunctionValue name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
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
     *     The arguments.
     *
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // TODO Util.compress args (compression here is tricky, probably sufficient to reduce repeating whitespace)
        appendable.append(name).append('(').append(args).append(')');
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("name", name)
            .add("args", args)
            .toString();
    }

    /**
     * Creates a new {@link FunctionValue} instance with the given function name and args.
     *
     * @param name
     *     The name of the function.
     * @param args
     *     The function arguments.
     *
     * @return The new {@link FunctionValue} instance.
     */
    public static FunctionValue of(String name, String args) {
        return new FunctionValue(name, args);
    }
}
