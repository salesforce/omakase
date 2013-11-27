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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;

import static com.salesforce.omakase.data.Browser.*;
import static com.salesforce.omakase.plugin.basic.PrefixerHandlers.*;

/**
 * This experimental plugin automagically handles vendor prefixing of css property names, function values, at-rules and
 * selectors.
 * <p/>
 * Which vendor prefixes are actually used depends of the browser versions that you want to support. You can manually specify
 * specific browser versions, last N versions, or just the latest browser version by utilizing the {@link SupportMatrix} returned
 * by the {@link #support()} method.
 * <p/>
 * In most cases however the default set of browser versions to support is adequate, as achieved by using the {@link
 * #defaultBrowserSupport()} constructor method.
 * <p/>
 * Browser and prefix data is seamlessly handled via updates from the caniuse.com data. To update to the latest data, see the
 * readme file titled "Scripts".
 * <p/>
 * This plugin integrates well with existing CSS. If all required prefixes are already present then nothing will be changed by
 * default. You can optionally have unnecessary prefixes removed via the {@link #prune(boolean)} method. You can also optionally
 * have all existing vendor-prefixed declarations rearranged to be <em>before</em> the unprefixed version via the {@link
 * #rearrange(boolean)} method.
 * <p/>
 * This doesn't automatically refine declarations or other refinables to check if they might need prefixes. This will only handle
 * prefixes if declarations, at-rules, and selectors have already been refined. If you want to ensure that every thing gets
 * checked then register an {@link AutoRefiner}, and call {@link AutoRefiner#all()}, or ensure that a {@link StandardValidation}
 * plugin instance is registered. Both of these must be registered before this plugin. See the main readme doc for more
 * information.
 * <p/>
 * <b>Important:</b> This is an <em>Experimental</em> plugin. Some rare and uncommon usages of prefixed values, property names,
 * selectors or at-rules may not currently work correctly. Please check the list of what's actually supported in the readme or
 * {@code prefix-info.yaml} file.
 * <p/>
 * Even if a prefix is supported, there may still be a few edge cases where a particular prefixable value is not automatically
 * handled. If you are using bleeding-edge syntax or prefixable features in a non-typical way then please double check the CSS
 * output for the proper behavior.
 * <p/>
 * Also note that some very old browser versions utilizing non-standard syntax may not currently be handled. For example, the
 * legacy linear-gradient syntax is not currently handled because several browser versions have passed since it was last used.
 * <p/>
 * Example usage:
 * <p/>
 * <pre><code>
 *     Prefixer prefixing = Prefixer.customBrowserSupport();
 *     prefixing.support().last(Browser.FIREFOX, 3);
 *     prefixing.support().last(Browser.CHROME, 2);
 *     prefixing.support().latest(Browser.SAFARI);
 *     prefixing.support().browser(Browser.IE, 9);
 *     prefixing.support().browser(Browser.IE, 10);
 *     prefixing.prune(true);
 *     prefixing.rearrange(true);
 * <p/>
 *     AutoRefiner refinement = new AutoRefiner().all();
 * <p/>
 *     Omakase.source(cssSource).request(refinement).request(prefixing).process();
 * </code></pre>
 *
 * @author nmcwilliams
 */
public final class Prefixer implements Plugin {
    private final SupportMatrix support;
    private boolean rearrange;
    private boolean prune;

    /** use a constructor method instead of this */
    private Prefixer(SupportMatrix support) {
        this.support = support == null ? new SupportMatrix() : support;
    }

    /**
     * Gets the {@link SupportMatrix} instance which can be used to indicate which browser versions are supported.
     *
     * @return The {@link SupportMatrix} instance.
     */
    public SupportMatrix support() {
        return support;
    }

    /**
     * Whether this plugin should rearranged the declarations/at-rules so that the unprefixed version always comes last. Default
     * is false.
     * <p/>
     * This only works for at-rules if the at-rules are contiguous.
     *
     * @param rearrange
     *     Whether we should moved the unprefixed version last.
     *
     * @return this, for chaining.
     */
    public Prefixer rearrange(boolean rearrange) {
        this.rearrange = rearrange;
        return this;
    }

    /**
     * Gets whether prefixed declarations can be rearranged.
     *
     * @return True if prefixed declarations can be rearrange.
     */
    public boolean rearrange() {
        return rearrange;
    }

    /**
     * Whether we should remove prefixed declarations/at-rules if they are not required for the supported browser versions.
     * Default is false.
     * <p/>
     * This only works for at-rules if the at-rules are contiguous.
     *
     * @param prune
     *     Whether we should remove unnecessary prefixed declarations.
     *
     * @return this, for chaining.
     */
    public Prefixer prune(boolean prune) {
        this.prune = prune;
        return this;
    }

    /**
     * Gets whether unnecessary prefixed declarations can be pruned.
     *
     * @return If unnecessary prefixed declarations can be pruned.
     */
    public boolean prune() {
        return prune;
    }

    /**
     * Subscription method - do not invoke directly.
     *
     * @param declaration
     *     The declaration instance.
     */
    @Rework
    public void declaration(Declaration declaration) {
        if (!PROPERTY.handle(declaration, rearrange, prune, support)) {
            TRANSITION.handle(declaration, rearrange, prune, support);
        }
    }

    /**
     * Subscription method - do not invoke directly.
     *
     * @param function
     *     The function instance.
     */
    @Rework
    public void function(FunctionValue function) {
        FUNCTION.handle(function, rearrange, prune(), support);
    }

    /**
     * Subscription method - do not invoke directly.
     *
     * @param atRule
     *     The atRule instance.
     */
    @Rework
    public void atRule(AtRule atRule) {
        AT_RULE.handle(atRule, rearrange, prune, support);
    }

    /**
     * Subscription method - do not invoke directly.
     *
     * @param selector
     *     The selector instance.
     */
    @Rework
    public void pseudoElementSelector(PseudoElementSelector selector) {
        PSEUDO.handle(selector, rearrange, prune, support);
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin with default browser version support levels: IE7+, latest versions of
     * Safari, IE Mobile and Opera Mini, last 2 versions of Firefox and Chrome, last 4 versions of IOS Safari and last 3 versions
     * of Android Browser.
     *
     * @return The new {@link Prefixer} instance.
     *
     * @see #rearrange(boolean)
     * @see #prune(boolean)
     */
    public static Prefixer defaultBrowserSupport() {
        SupportMatrix support = new SupportMatrix()
            .last(IOS_SAFARI, 4)
            .last(FIREFOX, 2)
            .last(ANDROID, 3)
            .last(CHROME, 2)
            .browser(IE, 7)
            .browser(IE, 8)
            .browser(IE, 9)
            .browser(IE, 10)
            .browser(IE, 11)
            .latest(SAFARI)
            .latest(IE_MOBILE)
            .latest(OPERA_MINI);

        return new Prefixer(support);
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin with no default browser version support. Specify which versions to
     * support via the {@link #support()} method.
     *
     * @return The new {@link Prefixer} instance.
     *
     * @see #rearrange(boolean)
     * @see #prune(boolean)
     */
    public static Prefixer customBrowserSupport() {
        return new Prefixer(null);
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin using the given {@link SupportMatrix}.
     *
     * @param support
     *     Use this {@link SupportMatrix} instance.
     *
     * @return The new {@link Prefixer} instance.
     *
     * @see #rearrange(boolean)
     * @see #prune(boolean)
     */
    public static Prefixer customBrowserSupport(SupportMatrix support) {
        return new Prefixer(support);
    }
}
