/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.basic;

import static com.google.common.base.Preconditions.checkState;

import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.PreProcess;
import com.salesforce.omakase.plugin.BroadcastingPlugin;
import com.salesforce.omakase.plugin.PreProcessingPlugin;

/**
 * TESTME TODO Description
 * 
 * @author nmcwilliams
 */
public final class SyntaxTree implements BroadcastingPlugin, PreProcessingPlugin {
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
    public void broadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void beforePreProcess() {
        startStylesheet();
    }

    @Override
    public void afterPreProcess() {
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

    private void startStylesheet() {
        currentStylesheet = new Stylesheet();
        state = State.ROOT_LEVEL;
    }

    private void endStylesheet() {
        checkState(currentStylesheet != null, "currentStylesheet not set");

        // end the last statement
        if (currentStatement != null) {
            endStatement(true);
        }

        broadcaster.broadcast(currentStylesheet);
        state = State.FROZEN;
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param atRule
     *            The new {@link AtRule}.
     */
    @PreProcess
    public void startAtRule(AtRule atRule) {
        switch (state) {
        case ROOT_LEVEL:
            startStatement(atRule);
            endStatement(false);
            break;
        case INSIDE_DECLARATION_BLOCK:
            endRule();
            startStatement(atRule);
            endStatement(false);
            break;
        case INSIDE_SELECTOR_GROUP:
            throw new IllegalStateException("at-rules currently not allowed here");
        case FROZEN:
            throw new IllegalStateException(Message.CANT_MODIFY_SYNTAX_TREE.message());
        }
    }

    private void startRule(int line, int column) {
        checkState(currentRule == null, "previous rule not ended");

        currentRule = new Rule(line, column);
        startStatement(currentRule);
        state = State.INSIDE_SELECTOR_GROUP;
    }

    private void endRule() {
        checkState(currentRule != null, "currentRule not set");

        endStatement(true);
        currentRule = null;
        state = State.ROOT_LEVEL;
    }

    private void startStatement(Statement statement) {
        checkState(currentStatement == null, "previous statement not ended");
        currentStatement = statement;
    }

    private void endStatement(boolean broadcast) {
        checkState(currentStatement != null, "currentStatement not set");

        currentStylesheet.append(currentStatement);

        if (broadcast) {
            broadcaster.broadcast(currentStatement);
        }

        currentStatement = null;
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param selector
     *            The new {@link Selector}.
     */
    @PreProcess
    public void startSelector(Selector selector) {
        switch (state) {
        case ROOT_LEVEL:
            startRule(selector.line(), selector.column());
            currentRule.selectors().append(selector);
            break;
        case INSIDE_DECLARATION_BLOCK:
            endRule();
            startRule(selector.line(), selector.column());
            currentRule.selectors().append(selector);
            break;
        case INSIDE_SELECTOR_GROUP:
            throw new IllegalStateException("at-rules currently not allowed here");
        case FROZEN:
            throw new IllegalStateException(Message.CANT_MODIFY_SYNTAX_TREE.message());
        }
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param declaration
     *            The new {@link Declaration}.
     */
    @PreProcess
    public void startDeclaration(Declaration declaration) {
        switch (state) {
        case ROOT_LEVEL:
            throw new IllegalStateException("declarations are not allowed at the root level");
        case INSIDE_DECLARATION_BLOCK:
        case INSIDE_SELECTOR_GROUP:
            currentRule.declarations().append(declaration);
            break;
        case FROZEN:
            throw new IllegalStateException(Message.CANT_MODIFY_SYNTAX_TREE.message());
        }

        state = State.INSIDE_DECLARATION_BLOCK;
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("stylesheet", currentStylesheet).toString();
    }
}
