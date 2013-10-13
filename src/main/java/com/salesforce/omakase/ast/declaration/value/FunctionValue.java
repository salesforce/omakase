/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.ast.declaration.value;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.declaration.FunctionValueParser;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A generic function value with non-validated arguments. By not validating arguments here, we allow for new CSS specifications as
 * well as custom functions with any arbitrary content.
 *
 * @author nmcwilliams
 * @see FunctionValueParser
 */
@Subscribable
@Description(value = "individual function value", broadcasted = REFINED_DECLARATION)
public class FunctionValue extends AbstractSyntax implements Term, Refinable {
    private final Refiner refiner;

    private String name;
    private String args;
    private Optional<RefinedFunctionValue> refined = Optional.absent();

    /**
     * Constructs a new {@link FunctionValue} instance with the given function name and arguments.
     * <p/>
     * If dynamically creating a new instance then use {@link #FunctionValue(String, String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The name of the function.
     * @param args
     *     The raw, non-validated function arguments.
     * @param refiner
     *     The {@link Refiner} to be used later during refinement of this object.
     */
    public FunctionValue(int line, int column, String name, String args, Refiner refiner) {
        super(line, column);
        this.name = name;
        this.args = args;
        this.refiner = refiner;
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
    public FunctionValue(String name, String args) {
        name(name);
        args(args);
        refiner = null;
    }

    /**
     * Sets the function name. Note that changing this value will invalidate any previous refinement.
     *
     * @param name
     *     The function name.
     *
     * @return this, for chaining.
     */
    public FunctionValue name(String name) {
        checkState(!isRefined(), "Cannot change the name after being refined. Work with the refined object instead.");
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
     * Sets the raw arguments. Note that changing this value will invalidate any previous refinement.
     *
     * @param args
     *     The arguments.
     *
     * @return this, for chaining.
     */
    public FunctionValue args(String args) {
        checkState(!isRefined(), "Cannot change the args after being refined. Work with the refined object instead.");
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

    /**
     * Sets the refined function value delegate object. If this is specified then the {@link #write(StyleWriter, StyleAppendable)}
     * method will delegate completely to the specified object.
     *
     * @param refined
     *     The refined function value delegate.
     *
     * @return this, for chaining.
     */
    public FunctionValue refinedValue(RefinedFunctionValue refined) {
        this.refined = Optional.of(refined);
        return this;
    }

    /**
     * Gets the refined function value, if present.
     *
     * @return The refined function value, or {@link Optional#absent()} if not present.
     */
    public Optional<RefinedFunctionValue> refinedValue() {
        return refined;
    }

    @Override
    public boolean isRefined() {
        return refined.isPresent();
    }

    @Override
    public boolean refine() {
        if (!isRefined() && refiner != null) {
            return refiner.refine(this);
        }
        return false;
    }

    @Override
    public boolean isWritable() {
        return !isRefined() || refined.get().isWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isRefined()) {
            refined.get().write(writer, appendable);
        } else {
            // TODO compression for args (compression here is tricky, probably sufficient to reduce repeating whitespace)
            appendable.append(name).append('(').append(args).append(')');
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("line", line())
            .add("column", column())
            .add("name", name)
            .add("args", args)
            .addIf(isRefined(), "refined", refined)
            .addUnlessEmpty("comments", comments())
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
