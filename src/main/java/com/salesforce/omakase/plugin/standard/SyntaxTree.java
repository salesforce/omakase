/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.standard;

import static com.google.common.base.Preconditions.checkState;

import com.salesforce.omakase.As;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorList;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.plugin.DependentPlugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SyntaxTree implements DependentPlugin {
    private Broadcaster broadcaster;
    private State state;

    private Stylesheet currentStylesheet;
    private Statement currentStatement;
    private Rule currentRule;
    private SelectorList currentSelectorList;
    private Selector currentSelector;
    private Declaration currentDeclaration;

    private enum State {
        ROOT_LEVEL,
        INSIDE_RULE,
        INSIDE_SELECTOR_GROUP
    }

    @Override
    public void before(Context context) {
        broadcaster = context;
        state = State.ROOT_LEVEL;
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
    @Subscribe
    public void startSelector(Selector selector) {
        switch (state) {
        case ROOT_LEVEL:
            startSelectorList(selector);
            break;
        case INSIDE_RULE:
            endRule();
            startSelectorList(selector);
            break;
        case INSIDE_SELECTOR_GROUP:
            checkState(currentSelector != null, "currentSelector not set");
            currentSelector.append(selector);
            break;
        }

        currentSelector = selector;
    }

    /**
     * Subscription method. Do not call directly.
     * 
     * @param declaration
     *            The new declaration.
     */
    @Subscribe
    public void startDeclaration(Declaration declaration) {
        switch (state) {
        case ROOT_LEVEL:
            throw new IllegalStateException("cannot start a declaration at the root level");
        case INSIDE_RULE:
            currentDeclaration.append(declaration);
            break;
        case INSIDE_SELECTOR_GROUP:
            startRule(declaration);
            break;
        }

        currentDeclaration = declaration;
    }

    private void startStylesheet(Statement firstStatement) {
        checkState(currentStylesheet == null, "previous stylesheet not ended");

        currentStylesheet = new Stylesheet(firstStatement);
        state = State.ROOT_LEVEL;
    }

    private void endStylesheet() {
        checkState(currentStylesheet != null, "currentStylesheet not set");

        // end the last statement
        if (currentStatement != null) {
            endStatement();
        }

        broadcaster.broadcast(SubscriptionType.CREATED, currentStylesheet);
        state = State.ROOT_LEVEL;
    }

    private void startRule(Declaration firstDeclaration) {
        checkState(currentRule == null, "previous rule not ended");
        checkState(currentSelectorList != null, "cannot start a rule without selectors");

        currentRule = new Rule(currentSelectorList, firstDeclaration);
        endSelectorList();
        startStatement(currentRule);
        state = State.INSIDE_RULE;
    }

    private void endRule() {
        checkState(currentRule != null, "currentRule not set");

        endStatement();
        currentRule = null;
        state = State.ROOT_LEVEL;
    }

    private void startStatement(Statement statement) {
        if (currentStylesheet == null) {
            startStylesheet(statement);
        }

        if (currentStatement != null) {
            currentStatement.append(statement);
        }

        currentStatement = statement;
    }

    private void endStatement() {
        checkState(currentStatement != null, "currentStatement not set");

        broadcaster.broadcast(SubscriptionType.CREATED, currentStatement);
    }

    private void startSelectorList(Selector firstSelector) {
        checkState(currentSelectorList == null, "previous selectorList not ended");
        checkState(state == State.ROOT_LEVEL, "cannot start selector group unless in root state");

        currentSelectorList = new SelectorList(firstSelector);
        state = State.INSIDE_SELECTOR_GROUP;
    }

    private void endSelectorList() {
        checkState(currentSelectorList != null, "currentSelectorList not set");
        broadcaster.broadcast(SubscriptionType.CREATED, currentSelectorList);
        currentSelectorList = null;
        state = State.ROOT_LEVEL;
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("stylesheet", currentStylesheet).toString();
    }
}
