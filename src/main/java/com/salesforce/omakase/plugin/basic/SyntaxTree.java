/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.basic;

import static com.google.common.base.Preconditions.checkState;

import com.salesforce.omakase.As;
import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Rework;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class SyntaxTree implements DependentPlugin, PostProcessingPlugin {
    private Broadcaster broadcaster;
    private State state;

    private Stylesheet currentStylesheet;
    private Statement currentStatement;
    private Rule currentRule;

    private enum State {
        ROOT_LEVEL,
        INSIDE_SELECTOR_GROUP,
        INSIDE_DECLARATION_BLOCK,
        FROZEN
    }

    @Override
    public void dependencies(Context context) {
        broadcaster = context;
        startStylesheet();
    }

    @Override
    public void after(Context context) {
        endStylesheet();
    }

    /**
     * Gets the {@link Stylesheet} instance.
     * 
     * @return The {@link Stylesheet}.
     */
    public Stylesheet stylesheet() {
        return currentStylesheet;
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param selector
     *            The new selector.
     */
    @Rework
    public void startSelector(Selector selector) {
        checkState(state != State.FROZEN, "syntax tree cannot be modified directly.");

        if (state == State.ROOT_LEVEL) {
            startRule(selector.line(), selector.column());
        } else if (state == State.INSIDE_DECLARATION_BLOCK) {
            endRule();
            startRule(selector.line(), selector.column());
        }

        currentRule.selectors().append(selector);
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param declaration
     *            The new declaration.
     */
    @Rework
    public void startDeclaration(Declaration declaration) {
        checkState(state != State.FROZEN, "syntax tree cannot be modified directly.");
        checkState(currentRule != null, "cannot add a declaration without a rule");

        currentRule.declarations().append(declaration);
        state = State.INSIDE_DECLARATION_BLOCK;
    }

    private void startStylesheet() {
        currentStylesheet = new Stylesheet();
        state = State.ROOT_LEVEL;
    }

    private void endStylesheet() {
        checkState(currentStylesheet != null, "currentStylesheet not set");

        // end the last statement
        if (currentStatement != null) {
            endStatement();
        }

        broadcaster.broadcast(SubscriptionType.CREATED, currentStylesheet);
        state = State.FROZEN;
    }

    private void startRule(int line, int column) {
        checkState(currentRule == null, "previous rule not ended");

        currentRule = new Rule(line, column);
        startStatement(currentRule);
        state = State.INSIDE_SELECTOR_GROUP;
    }

    private void endRule() {
        checkState(currentRule != null, "currentRule not set");

        endStatement();
        currentRule = null;
        state = State.ROOT_LEVEL;
    }

    private void startStatement(Statement statement) {
        currentStatement = statement;
    }

    private void endStatement() {
        checkState(currentStatement != null, "currentStatement not set");
        currentStylesheet.append(currentStatement);
        broadcaster.broadcast(SubscriptionType.CREATED, currentStatement);
        currentStatement = null;
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("stylesheet", currentStylesheet).toString();
    }
}
