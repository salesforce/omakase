/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.writer.StyleWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper for constructing toString() methods...cuz guava's helper just doesn't get the job done.
 * <p>
 * Example: <code><pre>As.string(this).indent().add("abc", abc).toString();</pre></code>
 *
 * @author nmcwilliams
 */
public final class As {
    private final List<Entry> entries = new ArrayList<>();
    private final Object instance;

    private String name;
    private boolean indent;

    /** use a constructor method instead */
    private As(Object object) {
        named(object.getClass().getSimpleName());
        this.instance = object;
    }

    /**
     * Creates a new string representation helper for the given object. Usually used inside of toString methods.
     *
     * @param object
     *     Create a string representation of this object.
     *
     * @return The helper instance.
     */
    public static As string(Object object) {
        return new As(object);
    }

    /**
     * Creates a simple toString representation of the given {@link Syntax} unit, based on how it would be output in CSS.
     *
     * @param syntax
     *     The unit.
     * @param includeUnitType
     *     Specify true to append in parenthesis the syntax type (e.g., 'pseudo-element-selector').
     *
     * @return The toString representation.
     */
    public static String simpleString(Syntax syntax, boolean includeUnitType) {
        StringBuilder builder = new StringBuilder(64);
        builder.append(StyleWriter.inline().writeSingle(syntax));
        if (includeUnitType) {
            builder.append(" (");
            String name = syntax.getClass().getSimpleName();
            name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
            builder.append(name);
            builder.append(")");
        }
        return builder.toString();
    }

    /**
     * Creates a new string representation helper described by the given name. Usually used inside of toString methods.
     *
     * @param name
     *     Name of the object being represented.
     *
     * @return The helper instance.
     */
    public As named(String name) {
        this.name = name;
        return this;
    }

    /**
     * Specifies that this toString representation should indent and write each member on a separate line.
     *
     * @return this, for chaining.
     */
    public As indent() {
        indent = true;
        return this;
    }

    /**
     * Adds a member to this toString representation.
     *
     * @param name
     *     Name of the member.
     * @param value
     *     The member.
     *
     * @return this, for chaining.
     */
    public As add(String name, Object value) {
        return entry(name, value, false);
    }

    /**
     * Same as {@link #add(String, Object)}, except it will only add the member if the given condition is true.
     *
     * @param condition
     *     Only add if this condition is true.
     * @param name
     *     Name of the member.
     * @param value
     *     The member.
     *
     * @return this, for chaining.
     */
    public As addIf(boolean condition, String name, Object value) {
        if (condition) add(name, value);
        return this;
    }

    /**
     * Adds a member to this toString representation. This is for iterables, which will automatically have their indentation level
     * increased (if indent is turned on).
     *
     * @param name
     *     Name of the member.
     * @param iterable
     *     The member.
     *
     * @return this, for chaining.
     */
    public As add(String name, Iterable<?> iterable) {
        return entry(name, iterable, true);
    }

    /**
     * Same as {@link #add(String, Iterable)}, except it will only add the member if the iterable is not empty.
     *
     * @param name
     *     Name of the member.
     * @param iterable
     *     The member.
     *
     * @return this, for chaining.
     */
    public As addUnlessEmpty(String name, Iterable<?> iterable) {
        if (!Iterables.isEmpty(iterable)) {
            add(name, iterable);
        }
        return this;
    }

    /**
     * Specifies that all fields should be added.
     * <p>
     * Fields with a value of null will not be output. Iterables will be added with {@link #add(String, Iterable)} and {@link
     * #indent()} will automatically be turned on upon the first addition of an iterable. If the object being printed is a {@link
     * Syntax} unit, line, column, and comments information will automatically be added.
     *
     * @return this, for chaining.
     */
    @SuppressWarnings("AutoBoxing")
    public As fields() {
        if (instance instanceof Syntax) {
            Syntax syntax = (Syntax)instance;
            addIf(syntax.line() > -1, "line", syntax.line());
            addIf(syntax.column() > -1, "col", syntax.line());
            addUnlessEmpty("comments", syntax.comments());
            addUnlessEmpty("orphaned comments", syntax.orphanedComments());
        }

        List<Field> fields = new ArrayList<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        try {
            for (Field field : fields) {
                Object value = null;
                value = field.get(instance);
                if (value instanceof Iterable) {
                    indent();
                    Iterable<?> iterable = (Iterable<?>)value;
                    if (!Iterables.isEmpty(iterable)) {
                        add(field.getName(), iterable);
                    }
                } else if (value != null) {
                    add(field.getName(), value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(entries.size() * 32);

        // name of the object being printed
        builder.append(name);
        if (indent) builder.append(" ");
        builder.append("{");

        // entries for the object being printed
        String separator = "";
        for (Entry entry : entries) {
            if (indent) {
                // use a new line separator between entries
                builder.append("\n  ");
            } else {
                // use a comma separator between entries
                builder.append(separator);
                separator = ", ";
            }

            // entry name
            builder.append(entry.name);
            builder.append(indent ? ": " : "=");

            // entry value. For indentation, increase the space before each newline for iterables
            String value = String.valueOf(entry.value);
            if (entry.isIterable) {
                value = value.replaceAll("\n", "\n  ");
            }
            builder.append(value);
        }

        // closing bracket
        if (indent) builder.append("\n");
        builder.append("}");

        return builder.toString();
    }

    /** utility method to create an {@link Entry} */
    private As entry(String name, Object value, boolean isIterable) {
        Entry entry = new Entry();
        entry.name = name;
        entry.value = value;
        entry.isIterable = isIterable;
        entries.add(entry);
        return this;
    }

    /** information on an item to include in toString */
    private static final class Entry {
        String name;
        Object value;
        boolean isIterable;
    }
}
