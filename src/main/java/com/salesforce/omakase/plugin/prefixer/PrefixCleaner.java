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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.Declarations;
import com.salesforce.omakase.util.Prefixes;

import java.util.Optional;

/**
 * This plugin handles removing unnecessary prefixed units.
 * <p>
 * If an at-rule is prefixed, most likely any prefixed declarations within that at-rule with a differing prefix can be removed.
 * This might especially be the case if you are utilizing the {@link Prefixer} plugin, which might automatically create prefixed
 * versions of at-rules that contain unnecessary prefixed declarations. For example:
 * <pre><code>
 * &#64;keyframes animation {
 *     from { transform: rotate(0deg) }
 *     from { transform: rotate(360deg) }
 * }
 * </code></pre>
 * after prefixing could result in
 * <p>
 * <pre><code>
 * &#64;-webkit-keyframes animation {
 *     from { -webkit-transform: rotate(0deg); -ms-transform: rotate(0deg); transform: rotate(0deg) }
 *     from { -webkit-transform: rotate(360deg); -ms-transform: rotate(360deg); transform: rotate(360deg) }
 * }
 * </code></pre>
 * <p>
 * Notice the {@code -ms-transform} is most likely unnecessary as it is within a {@code -webkit-} prefixed at-rule. The {@link
 * PrefixCleaner} plugin can be utilized to remove such prefixed declarations inside of prefixed at-rules. Use the {@link
 * #prefixedAtRules()} instance method or the {@link #mismatchedPrefixedUnits()} constructor method to remove these unnecessary
 * prefixes.
 * <p>
 * <b>Important:</b> This plugin must be registered <em>after</em> the {@link Prefixer} plugin:
 * <pre><code>
 * Omakase.source(input)
 *      .use(Prefixer.defaultBrowserSupport())
 *      .use(PrefixCleaner.mismatchedPrefixedUnits())
 *      .process()
 * </code></pre>
 * You can also specify the only prefix you want to keep with the {@link #keep(Prefix)} method, and all other prefixed selectors,
 * at-rules, declarations, etc... that don't match will be removed. This can be useful if you are generating browser-specific
 * versions of your CSS. <b>Not yet supported!</b>
 *
 * @author nmcwilliams
 */
public final class PrefixCleaner implements Plugin {
    private boolean prefixedAtRules;

    /**
     * Creates a new {@link PrefixCleaner} instance.
     */
    public PrefixCleaner() {
    }

    /**
     * Creates a new {@link PrefixCleaner} instance that will remove all units with a prefix that doesn't match the given one.
     *
     * @param prefix
     *     Only keep units with this prefix.
     */
    public PrefixCleaner(Prefix prefix) {
        keep(prefix);
    }

    /**
     * NOT IMPLEMENTED: Removes all units with a prefix that doesn't match the given one.
     *
     * @param prefix
     *     Only keep units with this prefix.
     *
     * @return this, for chaining.
     */
    public PrefixCleaner keep(Prefix prefix) {
        // this.keeper = checkNotNull(prefix, "prefix cannot be null");
        //return this;
        throw new UnsupportedOperationException("not yet supported");
    }

    /**
     * For prefixed declarations within prefixed at-rules, this removes the declaration if it has a prefix different from the
     * at-rule's prefix.
     *
     * @return this, for chaining.
     */
    public PrefixCleaner prefixedAtRules() {
        prefixedAtRules = true;
        return this;
    }

    /**
     * Subscription method - do not call directly.
     *
     * @param atRule
     *     Check this at rule.
     */
    @Rework
    public void atRule(AtRule atRule) {
        if (prefixedAtRules && atRule.isRefined() && atRule.name() != null && atRule.block().isPresent()) {
            Optional<Prefix> prefix = Prefixes.parsePrefix(atRule.name());
            if (prefix.isPresent()) {
                for (Declaration declaration : Declarations.within(atRule.block().get())) {
                    Optional<Prefix> declarationPrefix = declaration.propertyName().prefix();
                    if (declarationPrefix.isPresent() && declarationPrefix.get() != prefix.get()) {
                        declaration.destroy();
                    }
                }
            }
        }
    }

    /**
     * Creates a new {@link PrefixCleaner} that will eliminate prefixed declarations within prefixed at-rules, where the
     * declaration's prefix doesn't match the at-rule's prefix.
     *
     * @return The new {@link PrefixCleaner} instance.
     */
    public static PrefixCleaner mismatchedPrefixedUnits() {
        return new PrefixCleaner().prefixedAtRules();
    }

    /**
     * NOT IMPLEMENTED: Creates a new {@link PrefixCleaner} that will eliminate all units with prefixes that do not match the
     * given prefix.
     *
     * @param prefix
     *     Only keep units with this prefix.
     *
     * @return The new {@link PrefixCleaner} instance.
     */
    public static PrefixCleaner onlyKeep(Prefix prefix) {
        return new PrefixCleaner(prefix);
    }
}
