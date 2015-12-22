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

package com.salesforce.omakase.writer;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

import java.io.IOException;

/**
 * Customizes the writing of a particular {@link Syntax} unit.
 * <p/>
 * This allows you to override (or augment) the writing of any {@link Syntax} unit.
 * <p/>
 * <b>Important</b>: Some syntax units will not have their overrides kick in unless the parent unit is refined. For example, a
 * {@link ClassSelector} override will not be utilized unless {@link Selector#refine()} is called on the parent {@link Selector}.
 * An easy way to handle this is with an {@link AutoRefiner}. See the notes on that class for more information.
 * <p/>
 * <b>Also Important:</b> The default {@link StyleWriter} (in {@link StyleWriter#writeInner(Writable, StyleAppendable)}) usually
 * checks that the unit should actually be written out via {@link Writable#isWritable()}. Generally custom writers should check
 * this as well before writing.
 * <p/>
 * Note that custom writers must handle the writing (or not writing) of CSS comments on their own. For assistance with this see
 * {@link StyleWriter#appendComments(Iterable, StyleAppendable)}. Also see {@link Syntax#writesOwnComments()} and
 * {@link Syntax#writesOwnOrphanedComments()}.
 *
 * @param <T>
 *     The Type of object being overridden.
 *
 * @author nmcwilliams
 */
public interface CustomWriter<T extends Writable> {
    /**
     * Writes the given unit to the given {@link StyleAppendable}.
     * <p/>
     * <b>Notes for implementation:</b>
     * <p/>
     * You can completely bypass the default writing behavior of the unit by simply writing out content to the {@link
     * StyleAppendable}.
     * <p/>
     * If you are augmenting the write process instead, you can output the default representation of the unit by calling {@link
     * StyleWriter#writeInner(Writable, StyleAppendable)} before or after your augmentations, as appropriate.
     * <p/>
     * Do not use the {@link StyleWriter} in an attempt to write direct content (Strings, chars, etc...). Use the {@link
     * StyleAppendable}.
     * <p/>
     * The {@link StyleWriter} should be used to make decisions based on writer settings (e.g., compressed vs. verbose output
     * mode), as well as for writing inner or child {@link Writable}s.
     *
     * @param unit
     *     The unit to write.
     * @param writer
     *     Writer to use for output settings and for writing inner {@link Writable}s.
     * @param appendable
     *     Append direct content to this {@link StyleAppendable}.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    @SuppressWarnings("RedundantThrows")
    void write(T unit, StyleWriter writer, StyleAppendable appendable) throws IOException;
}
