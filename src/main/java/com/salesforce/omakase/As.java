/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class As {
    private boolean indent;
    private final String name;
    private final List<Entry> entries = Lists.newArrayList();

    /**
     * TODO
     * 
     * @param object
     *            TODO
     */
    private As(Object object) {
        this(name(object.getClass()));
    }

    /**
     * TODO
     * 
     * @param name
     *            TODO
     */
    private As(String name) {
        this.name = name;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public As indent() {
        indent = true;
        return this;
    }

    /**
     * TODO Description
     * 
     * @param name
     *            TODO
     * @param value
     *            TODO
     * @return TODO
     */
    public As add(String name, Object value) {
        return entry(name, value, false, false);
    }

    /**
     * TODO Description
     * 
     * @param name
     *            TODO
     * @param value
     *            TODO
     * @param forceInline
     *            TODO
     * @return TODO
     */
    public As add(String name, Object value, boolean forceInline) {
        return entry(name, value, forceInline, false);
    }

    /**
     * TODO Description
     * 
     * @param name
     *            TODO
     * @param collection
     *            TODO
     * @return TODO
     */
    public As add(String name, Iterable<?> collection) {
        return entry(name, collection, false, true);
    }

    private As entry(String name, Object value, boolean forceInline, boolean isIterable) {
        Entry e = new Entry();
        e.name = name;
        e.value = value;
        e.forceInline = forceInline;
        e.isIterable = isIterable;
        entries.add(e);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(entries.size() * 24);

        // name of the object being printed
        builder.append(name);
        if (indent) builder.append(" ");
        builder.append("{");

        // entries for the object being printed
        String separator = "";
        for (Entry entry : entries) {
            if (indent && !entry.forceInline) {
                // use a new line separator between entries
                builder.append("\n  ");
            } else if (indent) {
                // forcedIndent uses a comma separator between entries
                builder.append(", ");
            } else {
                // separator for when indent is off
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

    /**
     * TODO Description
     * 
     * @param klass
     * @return
     */
    private static String name(Class<? extends Object> klass) {
        return klass.getSimpleName();
    }

    /** information on an item to include in toString */
    private static final class Entry {
        String name;
        Object value;
        boolean forceInline;
        boolean isIterable;
    }

    /**
     * TODO Description
     * 
     * @param object
     *            TODO
     * @return TODO
     */
    public static As string(Object object) {
        return new As(object);
    }

    /**
     * TODO Description
     * 
     * @param name
     *            TODO
     * @return TODO
     */
    public static As named(String name) {
        return new As(name);
    }
}
