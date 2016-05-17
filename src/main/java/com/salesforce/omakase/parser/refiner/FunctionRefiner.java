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

import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.util.Args;

/**
 * Represents a strategy for refining an {@link RawFunction} object.
 * <p>
 * This feature allows you to add custom functions. The refiner should read the data in the given {@link RawFunction} and create
 * and broadcast the applicable custom function object.
 * <p>
 * Generally your custom function should implement {@link Term} or extend from {@link AbstractTerm}.
 * <p>
 * This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface FunctionRefiner extends Refiner {
    /**
     * Refines a {@link RawFunction}.
     * <p>
     * The information in the given {@link RawFunction} can be used to determine if the function value is applicable to your
     * custom syntax. Most often you determine this based on the value from {@link RawFunction#name()}. Utilize the {@link
     * RawFunction#args()} method to get the raw, unrefined arguments. Note that there are utilities in the {@link Args} helper to
     * assist with common argument string operations.
     * <p>
     * <b>Important:</b> There are two main ways to handle custom functions:
     * <p>
     * 1) The most common way is to immediately convert it to a list of {@link PropertyValueMember}s (e.g., {@link Term}s and
     * {@link Operator}s). To do this, process (and validate) the args, create a new {@link Source} object and then parse the
     * source object using {@link ParserFactory#termSequenceParser()}. Be sure to use the same {@link Broadcaster} instance given
     * as a parameter of this method. This will automatically result in the parsed terms and operators being added in place of
     * where the custom function was found.
     * <p>
     * 2) Alternatively, you can create a custom AST object (that extends {@link AbstractTerm} or implements {@link Term}). Once
     * created, you must broadcast this custom AST object using the same {@link Broadcaster} instance given as a parameter of this
     * method. The main benefit of creating a custom AST object is that you have more control over how it is written out, and you
     * can also subscribe to your custom AST objects in plugins (if you intend to also subscribe to your custom AST objects they
     * must be annotated with the {@link Subscribable} annotation). While you can still utilize parsers from {@link ParserFactory}
     * (e .g., using {@link ParserFactory#termSequenceParser()} to parse terms and add them to your custom AST object) it is
     * important that you give your own {@link Broadcaster} instance to these parsers. For example, creating a new {@link
     * QueryableBroadcaster}. If you reuse the same {@link Broadcaster} given as a parameter to this method it will result in any
     * {@link PropertyValueMember}s parsed being added along with your custom AST object.
     *
     * @param raw
     *     The {@link RawFunction} that may potentially match your custom function.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return One of the {@link Refinement} values.
     */
    Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner);
}
