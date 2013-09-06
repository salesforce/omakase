/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class PropertyName extends AbstractSyntax {
    private static final Pattern pattern = Pattern.compile("^-[a-zA-Z]+-");

    private final String name;
    private Optional<String> prefix;

    private PropertyName(int line, int column, String name) {
        super(line, column);

        // split into prefix and name
        String[] split = pattern.split(name);
        if (split.length == 1) {
            prefix = Optional.absent();
            this.name = split[0];
        } else {
            prefix = Optional.of(split[0]);
            this.name = split[1];
        }

    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String name() {
        return prefix.isPresent() ? prefix.get() + name : name;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String unprefixedName() {
        return name;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public boolean isPrefixed() {
        return prefix.isPresent();
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<String> prefix() {
        return prefix;
    }

    /**
     * TODO Description
     * 
     * @param prefix
     *            TODO
     * @return TODO
     */
    public PropertyName prefix(Prefix prefix) {
        return prefix(prefix.toString());
    }

    /**
     * TODO Description
     * 
     * @param prefix
     *            TODO
     * @return TODO
     */
    public PropertyName prefix(String prefix) {
        checkNotNull(prefix, "prefix cannot be null (use #removePrefix instead)");
        checkArgument(prefix.startsWith("-"), "prefixes must start with a dash");
        checkArgument(prefix.endsWith("-"), "prefixes must end with a dash");
        this.prefix = Optional.of(prefix);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public PropertyName removePrefix() {
        prefix = Optional.absent();
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name());
    }

    @Override
    public String toString() {
        return As.string(this).add("name", name()).toString();
    }

    /**
     * TODO Description
     * 
     * @param name
     *            TODO
     * @return TODO
     */
    public static PropertyName using(String name) {
        return using(-1, -1, name);
    }

    /**
     * Gets the {@link PropertyName} associated with the given String name.
     * 
     * @param name
     *            The name of the property.
     * @param line
     *            The line number of the start of the property name.
     * @param column
     *            The column number of the start of the property.
     * @return TODO
     */
    public static PropertyName using(int line, int column, String name) {
        Property recognized = Property.map.get(name.toLowerCase());
        String nameToUse = recognized != null ? recognized.getName() : name.toLowerCase();
        return new PropertyName(line, column, nameToUse);
    }

    /**
     * TODO Description
     * 
     * @param property
     *            TODO
     * @return TODO
     */
    public static PropertyName using(Property property) {
        return using(-1, -1, property);
    }

    /**
     * TODO Description
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param property
     *            TODO
     * @return TODO
     */
    public static PropertyName using(int line, int column, Property property) {
        checkNotNull(property, "property cannot be null");
        return new PropertyName(line, column, property.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof String) {
            // match string content
            return name().equals(other);
        } else if (other instanceof Property) {
            // match Property value
            return name().equals(((Property)other).getName());
        }
        return super.equals(other);
    }
}
