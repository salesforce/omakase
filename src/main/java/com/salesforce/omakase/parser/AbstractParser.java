/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.ast.standard.StandardSyntaxFactory;
import com.salesforce.omakase.consumer.Consumer;

/**
 * Base class for {@link Parser}s.
 * 
 * @author nmcwilliams
 */
@Immutable
public abstract class AbstractParser implements Parser {
    private final SyntaxFactory factory;

    /**
     * Creates a new {@link AbstractParser} instance with using a standard {@link SyntaxFactory}.
     */
    public AbstractParser() {
        this(StandardSyntaxFactory.instance());
    }

    /**
     * Creates a new {@link AbstractParser} instance using the given {@link SyntaxFactory}.
     * 
     * @param factory
     *            Use this factory for creating {@link Syntax} objects.
     */
    public AbstractParser(SyntaxFactory factory) {
        this.factory = factory;
    }

    /**
     * Utility method to create a {@link CombinationParser} comprised of this and the given {@link Parser}.
     * 
     * @param other
     *            The {@link Parser} to use.
     * @return A new {@link CombinationParser} instance.
     */
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }

    /**
     * Gets the {@link SyntaxFactory} to use for creating {@link Syntax} objects.
     * 
     * @return The factory.
     */
    protected SyntaxFactory factory() {
        return factory;
    }

    /**
     * Notify the given {@link Consumer}s of a parsed {@link Declaration}.
     * 
     * @param consumers
     *            The {@link Consumer}s to notify.
     * @param declaration
     *            The new {@link Declaration}.
     */
    protected void notify(Iterable<Consumer> consumers, Declaration declaration) {
        for (Consumer consumer : consumers) {
            consumer.declaration(declaration);
        }
    }

    /**
     * Notify the given {@link Consumer}s of a parsed {@link SelectorGroup}.
     * 
     * @param consumers
     *            The consumers to notify.
     * @param selectorGroup
     *            The new {@link SelectorGroup}.
     */
    protected void notify(Iterable<Consumer> consumers, SelectorGroup selectorGroup) {
        for (Consumer consumer : consumers) {
            consumer.selectorGroup(selectorGroup);
        }
    }
}
