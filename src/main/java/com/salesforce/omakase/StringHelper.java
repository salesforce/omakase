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
public final class StringHelper {
    private boolean indent;
    private final String name;
    private final List<Entry> entries;

    /**
     * TODO
     * 
     * @param object
     *            TODO
     */
    public StringHelper(Object object) {
        this(name(object.getClass()));
    }

    /**
     * TODO
     * 
     * @param name
     *            TODO
     */
    public StringHelper(String name) {
        this.name = name;
        this.entries = Lists.newArrayList();
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public StringHelper indent() {
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
    public StringHelper add(String name, Object value) {
        return entry(name, value, false, false);
    }

    public StringHelper inline(String name, Object value) {
        return entry(name, value, true, false);
    }

    public StringHelper add(String name, Iterable<?> collection) {
        return entry(name, collection, false, true);
    }

    private StringHelper entry(String name, Object value, boolean forceInline, boolean isCollection) {
        Entry e = new Entry();
        e.name = name;
        e.value = value;
        e.forceInline = forceInline;
        e.isCollection = isCollection;
        entries.add(e);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(32);

        builder.append(name);

        if (indent) {
            builder.append(" ");
        }

        builder.append("{");

        int size = entries.size();
        int last = size - 1;
        for (int i = 0; i < size; i++) {
            Entry entry = entries.get(i);

            if (indent) {
                if (!entry.forceInline) {
                    builder.append("\n  ");
                } else {
                    builder.append(", ");
                }
            }

            builder.append(entry.name);

            if (indent) {
                builder.append(": ");
            } else {
                builder.append("=");
            }

            String value = String.valueOf(entry.value);
            if (entry.isCollection) {
                value = value.replaceAll("\n", "\n  ");
            }
            builder.append(value);

            if (!indent && i != last) {
                builder.append(", ");
            }
        }

        if (indent) {
            builder.append("\n");
        }
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

    private static final class Entry {
        String name;
        Object value;
        boolean forceInline;
        boolean isCollection;
    }
}
