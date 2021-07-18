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
package com.salesforce.omakase.parser;

import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * Parses a segment of CSS source code.
 * <p>
 * {@link Parser}s must <em>not</em> maintain any state or persistence from one parse operation to another. They should be
 * immutable objects.
 * <p>
 * <b>Important:</b> for implementations-- parsing sub-units that may contain a {@link Refinable}, then querying for the results,
 * may require that you use {@link Broadcaster#chain(Broadcaster)} or {@link Broadcaster#chainBroadcast(Broadcastable,
 * Broadcaster, Broadcaster...)} in order to find units broadcasted by {@link Refine} subscription methods. See how other
 * parsers use those methods for examples.
 *
 * @author nmcwilliams
 */
public interface Parser {
    /**
     * Parse from the current position of the given source, notifying the given {@link Broadcaster} of any applicable events and
     * data.
     * <p>
     * Necessary grammar tokens and other parsers should be retrieved from the provided {@link Grammar} instance.
     *
     * @param source
     *     The source to parse.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     * of true does not indicate that the parsed content was completely valid syntax (unknown for some units until refinement).
     * @see #parse(Source, Grammar, Broadcaster, Boolean) 
     */
    boolean parse(Source source, Grammar grammar, Broadcaster broadcaster);
    
    /**
     * Parse from the current position of the given source, notifying the given {@link Broadcaster} of any applicable events and
     * data.
     * <p>
     * Necessary grammar tokens and other parsers should be retrieved from the provided {@link Grammar} instance.
     *
     * @param source
     *     The source to parse.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     * @param parentIsConditional
     *     Indicates that the parent rule is a conditional rule.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     * of true does not indicate that the parsed content was completely valid syntax (unknown for some units until refinement).
     * @see #parse(Source, Grammar, Broadcaster)
     */
    default boolean parse(Source source, Grammar grammar, Broadcaster broadcaster, boolean parentIsConditional) {
        return parse(source, grammar, broadcaster);
    }
}
