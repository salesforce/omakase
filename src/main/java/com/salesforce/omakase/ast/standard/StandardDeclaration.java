/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.google.common.base.Objects;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.RefinedDeclaration;

/**
 * Standard implementation of a {@link Declaration}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardDeclaration extends AbstractSyntax implements RefinedDeclaration {
    private final String content;
    private String property;
    private String value;

    StandardDeclaration(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public RefinedDeclaration refine() {
        if (property == null) {
            // TODO assign property and value
        }
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("raw", content)
            .add("property", property)
            .add("value", value)
            .toString();
    }

}
