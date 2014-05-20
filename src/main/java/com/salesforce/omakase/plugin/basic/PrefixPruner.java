/*
 * Copyright (C) 2014 salesforce.com, inc.
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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.Declarations;
import com.salesforce.omakase.util.Prefixes;

/**
 * TODO rename this
 * This plugin handles removing unnecessary prefixed units.
 * <p/>
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
 * <p/>
 * <pre><code>
 * &#64;-webkit-keyframes animation {
 *     from { -webkit-transform: rotate(0deg); -ms-transform: rotate(0deg); transform: rotate(0deg) }
 *     from { -webkit-transform: rotate(360deg); -ms-transform: rotate(360deg); transform: rotate(360deg) }
 * }
 * </code></pre>
 * <p/>
 * Notice the {@code -ms-transform} is most likely unnecessary as it is within a {@code -webkit-} prefixed at-rule. The {@link
 * PrefixPruner} plugin can be utilized to remove such prefixed declarations inside of prefixed at-rules. Use the {@link
 * #prefixedAtRules()} instance method or the {@link #prunePrefixedAtRules()} constructor method to remove these unnecessary
 * prefixes.
 * <p/>
 * <b>Important:</b> This plugin must be registered <em>after</em> the {@link Prefixer} plugin:
 * <pre><code>
 * Omakase.source(input)
 *      .add(Prefixer.defaultBrowserSupport())
 *      .add(PrefixPruner.prunePrefixedAtRules())
 *      .process()
 * </code></pre>
 * You can also specify the only prefix you want to keep with the {@link #keep(Prefix)} method, and all other prefixed selectors,
 * at-rules, declarations, etc... that don't match will be removed. This can be useful if you are generating browser-specific
 * versions of your CSS. <b>Not yet supported!</b>
 *
 * @author nmcwilliams
 */
public final class PrefixPruner implements Plugin {
    private boolean prefixedAtRules;

    /**
     * Creates a new {@link PrefixPruner} instance.
     */
    public PrefixPruner() {
    }

    /**
     * Creates a new {@link PrefixPruner} instance that will remove all units with a prefix that doesn't match the given one.
     *
     * @param prefix
     *     Only keep units with this prefix.
     */
    public PrefixPruner(Prefix prefix) {
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
    @SuppressWarnings("UnusedParameters")
    public PrefixPruner keep(Prefix prefix) {
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
    public PrefixPruner prefixedAtRules() {
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
        if (prefixedAtRules && atRule.name() != null && atRule.block().isPresent()) {
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
     * Creates a new {@link PrefixPruner} that will eliminate prefixed declarations within prefixed at-rules, where the
     * declaration's prefix doesn't match the at-rule's prefix.
     *
     * @return The new {@link PrefixPruner} instance.
     */
    public static PrefixPruner prunePrefixedAtRules() {
        return new PrefixPruner().prefixedAtRules();
    }

    /**
     * NOT IMPLEMENTED: Creates a new {@link PrefixPruner} that will eliminate all units with prefixes that do not match the given
     * prefix.
     *
     * @param prefix
     *     Only keep units with this prefix.
     *
     * @return The new {@link PrefixPruner} instance.
     */
    public static PrefixPruner onlyKeep(Prefix prefix) {
        return new PrefixPruner(prefix);
    }
}
