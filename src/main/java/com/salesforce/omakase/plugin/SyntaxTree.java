/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.emitter.SubscriptionType;

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
    private SelectorGroup currentSelectorGroup;
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
     * TODO Description
     * 
     * @return TODO
     */
    public Stylesheet stylesheet() {
        return currentStylesheet;
    }

    /**
     * TODO Description
     * 
     * @param selector
     *            TODO
     */
    @Subscribe
    public void startSelector(Selector selector) {
        switch (state) {
        case ROOT_LEVEL:
            startSelectorGroup(selector);
            break;
        case INSIDE_RULE:
            endRule();
            startSelectorGroup(selector);
            break;
        case INSIDE_SELECTOR_GROUP:
            checkState(currentSelector != null, "currentSelector not set");
            currentSelector.append(selector);
            break;
        }

        currentSelector = selector;
    }

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
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

    /**
     * TODO Description
     * 
     * @param firstStatement
     *            TODO
     */
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
        checkState(currentSelectorGroup != null, "cannot start a rule without selectors");

        currentRule = new Rule(currentSelectorGroup, firstDeclaration);
        endSelectorGroup();
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

    private void startSelectorGroup(Selector firstSelector) {
        checkState(currentSelectorGroup == null, "previous selectorGroup not ended");
        checkState(state == State.ROOT_LEVEL, "cannot start selector group unless in root state");

        currentSelectorGroup = new SelectorGroup(firstSelector);
        state = State.INSIDE_SELECTOR_GROUP;
    }

    private void endSelectorGroup() {
        checkState(currentSelectorGroup != null, "currentSelectorGroup not set");
        broadcaster.broadcast(SubscriptionType.CREATED, currentSelectorGroup);
        currentSelectorGroup = null;
        state = State.ROOT_LEVEL;
    }

    @Override
    public String toString() {
        if (currentStylesheet == null) return Objects.toStringHelper(this).toString();

        StringBuilder builder = new StringBuilder(512);
        for (Statement statement : currentStylesheet.statements()) {
            System.out.println(statement);
        }

        return builder.toString();
    }
}
