/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Helper for constructing toString methods...cuz guava's helper just doesn't get the job done.
 * 
 * @example <code><pre>As.string(this).indent().add("abc", abc).toString();</pre></code>
 * @author nmcwilliams
 */
public final class As {
    private boolean indent;
    private final String name;
    private final List<Entry> entries = Lists.newArrayList();

    /** use construction method instead */
    private As(Object object) {
        this(object.getClass().getSimpleName());
    }

    /** use construction method instead */
    private As(String name) {
        this.name = name;
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
     *            Name of the member.
     * @param value
     *            the member.
     * @return this, for chaining.
     */
    public As add(String name, Object value) {
        return entry(name, value, false);
    }

    /**
     * Adds a member to this toString representation. This is for iterables, which will automatically have their
     * indentation level increased (if indent is turned on).
     * 
     * @param name
     *            Name of the member.
     * @param collection
     *            The member.
     * @return this, for chaining.
     */
    public As add(String name, Iterable<?> collection) {
        return entry(name, collection, true);
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

    /** information on an item to include in toString */
    private static final class Entry {
        String name;
        Object value;
        boolean isIterable;
    }

    /**
     * Creates a new string representation helper for the given object. Usually used inside of toString methods.
     * 
     * @param object
     *            Create a string representation of this object.
     * @return The helper instance.
     */
    public static As string(Object object) {
        return new As(object);
    }

    /**
     * Creates a new string representation helper described by the given name. Usually used inside of toString methods.
     * 
     * @param name
     *            Name of the object being represented.
     * @return The helper instance.
     */
    public static As stringNamed(String name) {
        return new As(name);
    }
}
