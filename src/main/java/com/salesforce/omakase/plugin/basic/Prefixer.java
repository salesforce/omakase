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

import com.google.common.base.Optional;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixInfo;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.Actions;
import com.salesforce.omakase.util.Declarations;

import java.util.Iterator;
import java.util.Set;

import static com.salesforce.omakase.data.Browser.*;

/**
 * TODO description
 * <p/>
 * <b>Important:</b> This is an <em>Experimental</em> plugin. Some rare and uncommon usages of prefixed values, property names,
 * selectors or at-rules may not currently work correctly. Please check the list of what's actually supported in the readme doc ot
 * {@code prefix-info.yaml} file. However please note, even if a prefix is supported, there may still be a few rare cases where a
 * particular prefixable value is not automatically handled. If you are using bleeding-edge syntax or prefixable features in a
 * non-typical way then please double check the CSS output for the proper behavior.
 * <p/>
 * Also note that some very old browser versions utilizing non-standard syntax may not currently be handled correctly. For
 * example, the legacy linear-gradient syntax (which shouldn't matter, because every modern browser has supported modern
 * linear-gradient syntax for a number of releases).
 *
 * @author nmcwilliams
 */
public final class Prefixer implements Plugin {
    private final SupportMatrix support = new SupportMatrix();
    private boolean rearrange;
    private boolean remove;

    /** used a constructor method instead */
    private Prefixer() {}

    /**
     * Gets the {@link SupportMatrix} instance which can be used to indicate which browser versions are supported.
     *
     * @return The {@link SupportMatrix} instance.
     */
    public SupportMatrix support() {
        return support;
    }

    /**
     * Whether we should remove prefixed declarations/at-rules if they are not required for the supported browser versions.
     * Default is false.
     *
     * @param remove
     *     Whether we should remove unnecessary prefixed declarations.
     *
     * @return this, for chaining.
     */
    public Prefixer removeUnnecessary(boolean remove) {
        this.remove = remove;
        return this;
    }

    /**
     * Whether this plugin should rearranged the declarations/at-rules so that the unprefixed version always comes last. Default
     * is false. This only works for at-rules if the at-rules are contiguous.
     *
     * @param rearrange
     *     Whether we should moved the unprefixed version last.
     *
     * @return this, for chaining.
     */
    public Prefixer rearrangeIfPresent(boolean rearrange) {
        this.rearrange = rearrange;
        return this;
    }

    /**
     * Subscription method - do not invoke directly.
     *
     * @param declaration
     *     The declaration instance.
     */
    @Rework
    public void declaration(Declaration declaration) {
        if (declaration.isDetached()) return;

        // check for a known, unprefixed property name
        Optional<Property> property = declaration.propertyName().asProperty();
        if (!property.isPresent()) return;

        // check if we have prefix info on the property
        if (!PrefixInfo.hasProperty(property.get())) return;

        // gather all required prefixes
        Set<Prefix> required = support.prefixesForProperty(property.get());

        // find all prefixed declarations in the rule for the same property
        Iterable<Declaration> equivalents = Declarations.prefixedEquivalents(declaration);

        for (Prefix prefix : required) {
            // check if we already have the prefixed version

            boolean present = false;

            Iterator<Declaration> iterator = equivalents.iterator();
            while (iterator.hasNext()) {
                Declaration equivalent = iterator.next();

                if (equivalent.propertyName().hasPrefix(prefix)) {
                    // prefixed version already present
                    if (rearrange) declaration.group().get().moveBefore(declaration, equivalent);
                    iterator.remove();
                    present = true;
                }
            }

            // if not present the add it
            if (!present) {
                declaration.prepend(declaration.copyWithPrefix(prefix, support));
            }
        }

        // any left over equivalents are unnecessary. remove or rearrange them if allowed
        if (remove) {
            Actions.detach().apply(equivalents);
        } else if (rearrange) {
            Actions.<Declaration>moveBefore().apply(declaration, equivalents);
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
        // check if we have prefix info on the function
        if (!PrefixInfo.hasFunction(function.name())) return;

        // check supported browsers for a required prefix
        Set<Prefix> required = support.prefixesForFunction(function.name());
        if (required.isEmpty()) {
            // TODO
            // no required prefixes, so remove unnecessary prefixed declarations if allowed
            // if (remove) Declarations.apply(Declarations.prefixedEquivalents(declaration), Actions.DETACH);
        } else {
            Declaration unprefixed = function.group().get().parent().parentDeclaration().get();

            // add all required prefixes
            for (Prefix prefix : required) {
                Optional<Declaration> dd = Declarations.prefixedFunctionEquivalent(unprefixed, prefix, function.name());
                if (dd.isPresent()) {
                    // TODO
                } else {
                    // prefixed version wasn't found, so create and add it
                    unprefixed.prepend(unprefixed.copyWithPrefix(prefix, support));
                }
            }
        }
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin with default browser version support levels: IE7+, latest versions of
     * Safari, IE Mobile and Opera Mini, last 2 versions of Firefox and Chrome, last 4 versions of IOS Safari and last 3 versions
     * of Android Browser.
     *
     * @return The new {@link Prefixer} instance.
     *
     * @see #rearrangeIfPresent(boolean)
     * @see #removeUnnecessary(boolean)
     */
    public static Prefixer defaultBrowserSupport() {
        Prefixer prefixer = new Prefixer();
        prefixer.support()
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

        return prefixer;
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin with no default browser version support. Specify which versions to
     * support via the {@link #support()} method.
     *
     * @return The new {@link Prefixer} instance.
     *
     * @see #rearrangeIfPresent(boolean)
     * @see #removeUnnecessary(boolean)
     */
    public static Prefixer customBrowserSupport() {
        return new Prefixer();
    }
}
