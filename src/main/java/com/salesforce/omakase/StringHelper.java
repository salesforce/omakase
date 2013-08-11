/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class StringHelper {
    private static final String indentString = "                ";

    private final String name;
    private final List<Entry> entries;

    private static int level = 0;

    private boolean indent;

    public StringHelper(Object object) {
        this(object.getClass());

    }

    public StringHelper(Class<? extends Object> klass) {
        this(name(klass));
    }

    public StringHelper(String name) {
        this.name = name;
        this.entries = Lists.newArrayList();
    }

    public StringHelper indent() {
        indent = true;
        return this;
    }

    public StringHelper add(String name, @Nullable Object value) {
        return entry(name, value);
    }

    public StringHelper add(String name, @Nullable Object value, boolean newline) {
        return entry(name, value, newline);
    }

    public StringHelper add(String name, boolean value) {
        return entry(name, String.valueOf(value));
    }

    public StringHelper add(String name, char value) {
        return entry(name, String.valueOf(value));
    }

    public StringHelper add(String name, double value) {
        return entry(name, String.valueOf(value));
    }

    public StringHelper add(String name, float value) {
        return entry(name, String.valueOf(value));
    }

    public StringHelper add(String name, int value) {
        return entry(name, String.valueOf(value));
    }

    public StringHelper add(String name, long value) {
        return entry(name, String.valueOf(value));
    }

    private StringHelper entry(String name, Object value) {
        return entry(name, value, true);
    }

    private StringHelper entry(String name, Object value, boolean newline) {
        entries.add(new Entry(name, value, newline));
        return this;
    }

    @Override
    public String toString() {
        if (indent) {
            level++;
        }

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
                if (entry.newline) {
                    builder.append("\n").append(indentString(level));
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

            builder.append(entry.value);
            if (!indent && i != last) {
                builder.append(", ");
            }

        }

        if (indent) {
            level--;
            builder.append("\n").append(indentString(level));
        }
        builder.append("}");

        return builder.toString();
    }

    private static final class Entry {
        final String name;
        final Object value;
        final boolean newline;

        public Entry(String name, Object value, boolean newline) {
            this.name = name;
            this.value = value;
            this.newline = newline;
        }
    }

    /**
     * TODO Description
     * 
     * @param level
     * @return
     */
    private Object indentString(int level) {
        return indentString.substring(0, level * 2);
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
}
