/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link AtRule} object. This allows you to add custom syntax with a structure similar to
 * standard at-rules. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface AtRuleRefiner extends Refiner {
    /**
     * Refines an {@link AtRule}.
     * <p/>
     * The information in the given {@link AtRule} can be used to determine if the at-rule is applicable to your custom syntax.
     * Most often you determine this based on the value from {@link AtRule#name()}.
     * <p/>
     * Utilize the {@link AtRule#rawExpression()} and {@link AtRule#rawBlock()} methods to get the raw, unrefined syntax. Parse
     * this information into your own {@link AtRuleExpression} and {@link AtRuleBlock} objects and then optionally broadcast them
     * using the given {@link Broadcaster} (if you intend to broadcast your custom AST objects they must be annotated with the
     * {@link Subscribable} annotation and implement {@link Syntax}).
     * <p/>
     * Be sure to actually add the objects to the {@link AtRule} using the {@link AtRule#expression(AtRuleExpression)} and {@link
     * AtRule#block(AtRuleBlock)} methods. One or both of these methods should be called (i.e., it's fine if your customized
     * object does not have both).
     * <p/>
     * If the actual at-rule name (e.g., "@media") should be discarded then call {@link AtRule#shouldWriteName(boolean)} with
     * false. If the at-rule itself is just for metadata purposes and does not have any associated content to write out then use
     * {@link AtRule#markAsMetadataRule()}.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return One of the {@link Refinement} values.
     */
    Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner);
}
