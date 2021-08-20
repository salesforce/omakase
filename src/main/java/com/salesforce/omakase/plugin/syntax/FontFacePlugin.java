/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.plugin.syntax;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.FontDescriptor;
import com.salesforce.omakase.ast.atrule.FontFaceBlock;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.ConsumingBroadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Refines font-face at-rules (@font-face).
 * <p>
 * For the grammar rules see <a href='http://dev.w3.org/csswg/css-fonts/#font-face-rule'>the spec</a>.
 * <p>
 * Inside of the font-face block are <em>font-descriptors</em>. Grammar-wise these are pretty much exactly just like {@link
 * Declaration}s, however in the interpreted CSS they have different meanings. For example, a {@code font-family} font-descriptor
 * means something different from a {@code font-family} declaration. We <em>could</em> just treat and broadcast these as
 * declarations, and while there are reasons that would make that beneficial, there are also reasons that would make that
 * undesirable.
 * <p>
 * Whether a plugin author listening for a declaration wants to receive font-descriptors as well depends on what they are doing.
 * An example of where you wouldn't want to would be a plugin that counts the number of font-family declarations, or a plugin that
 * automatically changes all font-family declarations to something different for, say, a Japan-localised version of the site.
 * Also, quite honestly due to the nature of the AST objects (type-safe bi-traversal), putting {@link Declaration}s directly
 * inside of an {@link AtRuleBlock} would require more work.
 *
 * @author nmcwilliams
 * @see FontDescriptor
 * @see FontFaceBlock
 */
public final class FontFacePlugin implements Plugin {
    /**
     * Refines the font-face at-rule.
     * <p>
     * If refinement is successful the new {@link FontFaceBlock} will be broadcasted via the given {@link Broadcaster}.
     *
     * @param atRule
     *     The atRule to refine.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine("font-face")
    public void refine(AtRule atRule, Grammar grammar, Broadcaster broadcaster) {
        // shouldn't have an expression
        if (atRule.rawExpression().isPresent()) {
            throw new ParserException(atRule.rawExpression().get(), Message.UNEXPECTED_EXPRESSION_FONT_FACE);
        }

        // must have a block
        if (!atRule.rawBlock().isPresent()) throw new ParserException(atRule, Message.FONT_FACE);

        Source source = new Source(atRule.rawBlock().get());
        FontFaceBlock block = new FontFaceBlock(source.line(), source.column());

        // font descriptors are basically the same as declarations, so we're going to cheat and reuse the declaration parsers.
        DeclarationPlugin refiner = new DeclarationPlugin();
        // XXX can probably now broadcast declarations with no harm

        // however we don't want to broadcast declarations to subscription methods, so make a new broadcaster to go at the end
        // of the chain. we also need to refine the declarations because our FontDescriptors will need the property values.
        QueryableBroadcaster queryable = broadcaster.chain(new QueryableBroadcaster());
        queryable.chain(new ConsumingBroadcaster<>(Declaration.class, d -> refiner.refine(d, grammar, broadcaster)));

        grammar.parser().rawDeclarationSequenceParser().parse(source, grammar, queryable); // parse using the queryable

        // pull the property name and property values into font descriptors and add them to the block
        for (Declaration declaration : queryable.filter(Declaration.class)) {
            block.fontDescriptors().append(new FontDescriptor(declaration));
        }

        // add orphaned comments
        block.orphanedComments(source.collectComments().flushComments());

        // nothing should be left in the source
        if (!source.eof()) {
            throw new ParserException(source, Message.UNPARSABLE_FONT_FACE, source.remaining());
        }

        // we didn't give the original broadcaster to the parser, so now ensure the block and child elements are broadcasted
        block.propagateBroadcast(broadcaster, Status.PARSED);
    }
}
