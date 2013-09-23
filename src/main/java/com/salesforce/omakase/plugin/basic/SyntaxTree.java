/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.PreProcess;
import com.salesforce.omakase.notification.NotifyDeclarationBlockEnd;
import com.salesforce.omakase.notification.NotifyDeclarationBlockStart;
import com.salesforce.omakase.notification.NotifyStylesheetEnd;
import com.salesforce.omakase.notification.NotifyStylesheetStart;
import com.salesforce.omakase.plugin.BroadcastingPlugin;

import static com.google.common.base.Preconditions.checkState;

/**
 * Responsible for organizing parsed {@link Selector}s, {@link Declaration}s and {@link AtRule}s into an AST tree.
 * <p/>
 * If you want direct access to the {@link Stylesheet} then this plugin must be registered.
 * <p/>
 * You also must register this plugin if you are subscribing to {@link Stylesheet} or {@link Rule} AST objects, as they will not
 * be created outside of this plugin.
 * <p/>
 * For more information see the readme file.
 *
 * @author nmcwilliams
 */
public final class SyntaxTree implements BroadcastingPlugin {
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

    /**
     * Gets the {@link Stylesheet} instance.
     *
     * @return The {@link Stylesheet}.
     */
    public Stylesheet stylesheet() {
        return currentStylesheet;
    }

    @Override
    public void broadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param event
     *     event.
     */
    @PreProcess
    @SuppressWarnings("UnusedParameters")
    public void onStylesheetStart(NotifyStylesheetStart event) {
        currentStylesheet = new Stylesheet(broadcaster);
        state = State.ROOT_LEVEL;
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param event
     *     event.
     */
    @PreProcess
    @SuppressWarnings("UnusedParameters")
    public void onStylesheetEnd(NotifyStylesheetEnd event) {
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
     *     The new {@link AtRule}.
     */
    @PreProcess
    public void startAtRule(AtRule atRule) {
        checkNotFrozen();
        checkState(state == State.ROOT_LEVEL, "cannot add an at-rule outside of the root level");
        startStatement(atRule);
        endStatement(false);
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param selector
     *     The new {@link Selector}.
     */
    @PreProcess
    public void selector(Selector selector) {
        checkNotFrozen();

        if (state == State.ROOT_LEVEL) {
            startRule(selector.line(), selector.column());
            state = State.INSIDE_SELECTOR_GROUP;
        } else if (state != State.INSIDE_SELECTOR_GROUP) {
            throw new IllegalStateException("expected state to be ROOT_LEVEL or INSIDE_SELECTOR_GROUP");
        }

        currentRule.selectors().append(selector);
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param event
     *     event.
     */
    @PreProcess
    @SuppressWarnings("UnusedParameters")
    public void onDeclarationBlockStart(NotifyDeclarationBlockStart event) {
        state = State.INSIDE_DECLARATION_BLOCK;
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param event
     *     event.
     */
    @PreProcess
    @SuppressWarnings("UnusedParameters")
    public void onDeclarationBlockEnd(NotifyDeclarationBlockEnd event) {
        endRule();
    }

    /**
     * Subscription method. Do not call directly.
     *
     * @param declaration
     *     The new {@link Declaration}.
     */
    @PreProcess
    public void declaration(Declaration declaration) {
        checkNotFrozen();
        checkState(state == State.INSIDE_DECLARATION_BLOCK, "cannot add a declaration outside of a declaration block");
        currentRule.declarations().append(declaration);
    }

    /**
     * Subscription method. Do not call directly.
     * <p/>
     * Handles {@link OrphanedComment}s for rules and stylesheets.
     *
     * @param comment
     *     The orphaned comment.
     */
    @PreProcess
    public void orphanedComment(OrphanedComment comment) {
        checkNotFrozen();
        if (currentRule != null && comment.location() == OrphanedComment.Location.RULE) {
            currentRule.orphanedComment(comment);
        } else if (currentStylesheet != null && comment.location() == OrphanedComment.Location.STYLESHEET) {
            currentStylesheet.orphanedComment(comment);
        }
    }

    private void startRule(int line, int column) {
        checkState(currentRule == null, "previous rule not ended");
        currentRule = new Rule(line, column, broadcaster);
        startStatement(currentRule);
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

    private void checkNotFrozen() {
        if (state == State.FROZEN) throw new IllegalStateException(Message.CANT_MODIFY_SYNTAX_TREE.message());
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("stylesheet", currentStylesheet).toString();
    }
}
