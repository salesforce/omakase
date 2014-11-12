/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.ast;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

/**
 * Represents a CSS annotation comment.
 * <p/>
 * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
 * example "@noparse", "@browser ie7", "@something arg1 arg2 arg3", etc...
 * <p/>
 * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block. CSS
 * comment annotations can have optional arguments, separated by spaces.
 * <p/>
 * Units will be associated with the CSS annotation comments that directly precede them. More than one CSS annotation comment is
 * allowed.
 *
 * @author nmcwilliams
 */
public final class CssAnnotation {
    private final String name;
    private final ImmutableList<String> arguments;

    private Comment cached;

    /**
     * Creates a new {@link CssAnnotation} instance with the given arguments (or no arguments).
     *
     * @param name
     *     The name of the annotation.
     * @param arguments
     *     Optional list of arguments.
     */
    public CssAnnotation(String name, String... arguments) {
        this(name, ImmutableList.copyOf(arguments));
    }

    /**
     * Creates a new {@link CssAnnotation} instance with the given arguments.
     *
     * @param name
     *     The name of the annotation.
     * @param arguments
     *     List of arguments.
     */
    public CssAnnotation(String name, Iterable<String> arguments) {
        this.name = name;
        this.arguments = arguments == null ? ImmutableList.<String>of() : ImmutableList.copyOf(arguments);
    }

    /**
     * Gets the name of the annotation.
     *
     * @return The name of the annotation.
     */
    public String name() {
        return name;
    }

    /**
     * Gets the first (or only) argument of the annotation.
     *
     * @return The first argument, or {@link Optional#absent()} if the annotation does not have any arguments.
     */
    public Optional<String> argument() {
        return argument(0);
    }

    /**
     * Gets the argument at the given index.
     *
     * @param index
     *     Retrieve the argument at this index, starting at 0.
     *
     * @return The argument at the given index, or {@link Optional#absent()} if annotation does not contain an argument at the
     * given index.
     */
    public Optional<String> argument(int index) {
        return (index >= arguments.size()) ? Optional.<String>absent() : Optional.fromNullable(arguments.get(index));
    }

    /**
     * Gets the list of all arguments.
     *
     * @return The list of all arguments.
     */
    public ImmutableList<String> arguments() {
        return arguments;
    }

    /**
     * Gets whether the given argument is present.
     *
     * @param argument
     *     Check for this argument.
     *
     * @return True if the given argument is present.
     */
    public boolean hasArgument(String argument) {
        for (String arg : arguments) {
            if (arg.equals(argument)) return true;
        }
        return false;
    }

    /**
     * Creates a new {@link Comment} instance using this annotation as the content.
     *
     * @param useCached
     *     If true, this method will reuse the same {@link Comment} instance previously created from this method instead of
     *     creating a new one. As {@link Comment} objects are essentially immutable, you usually want to pass true here.
     *
     * @return The new {@link Comment} instance.
     */
    public Comment toComment(boolean useCached) {
        if (useCached) {
            if (cached == null) {
                cached = new Comment(this);
            }
            return cached;
        }

        return new Comment(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("@").append(name);
        if (!arguments.isEmpty()) {
            builder.append(' ').append(Joiner.on(" ").skipNulls().join(arguments));
        }
        return builder.toString();
    }

    /**
     * Tests equality using the name and arguments fields. That is, if the name is equal and the arguments (or lack of arguments)
     * are equal (based on contents of the list) then this will return true.
     *
     * @param object
     *     Compare to this object.
     *
     * @return True if both the name is equal and arguments are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof CssAnnotation) {
            CssAnnotation other = (CssAnnotation)object;
            return Objects.equals(name, other.name) && Objects.equals(arguments, other.arguments);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }
}
