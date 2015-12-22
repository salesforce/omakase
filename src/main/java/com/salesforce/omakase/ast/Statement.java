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

package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * A top-level {@link Syntax} unit, for example a {@link Rule} or {@link AtRule}.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "rule or at-rule", broadcasted = AUTOMATIC)
public interface Statement extends Groupable<StatementIterable, Statement> {
    /**
     * Gets this statement as an {@link Rule}, if possible. This is an alternative to using an <pre>instanceof</pre> check.
     *
     * @return An {@link Optional} containing this object if it is an instance of an {@link Rule}, or {@link Optional#absent()}
     *         otherwise.
     */
    Optional<Rule> asRule();

    /**
     * Gets this statement as an {@link AtRule}, if possible. This is an alternative to using an <pre>instanceof</pre> check.
     *
     * @return An {@link Optional} containing this object if it is an instance of an {@link AtRule}, or {@link Optional#absent()}
     *         otherwise.
     */
    Optional<AtRule> asAtRule();

    @Override
    Statement copy();
}
