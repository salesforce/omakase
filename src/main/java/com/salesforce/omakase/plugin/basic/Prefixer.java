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
import com.google.common.collect.Sets;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixInfo;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.Plugin;

import java.util.Set;

import static com.salesforce.omakase.data.Browser.*;

/**
 * TESTME
 * <p/>
 * TODO description
 * <p/>
 * Experimental.
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
     * Whether we should remove prefixed declarations if they are not required for the supported browser versions. Default is
     * false.
     *
     * @param remove
     *     Whether we should remove unnecessary prefixed declarations.
     *
     * @return this, for chaining.
     */
    public Prefixer removeUnnecessaryDeclarations(boolean remove) {
        this.remove = remove;
        return this;
    }

    /**
     * Whether this plugin should rearranged the declarations so that the unprefixed version always comes last. Default is false.
     *
     * @param rearrange
     *     Whether we should moved the unprefixed version last.
     *
     * @return this, for chaining.
     */
    public Prefixer rearrangeIfAlreadyPresent(boolean rearrange) {
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

        // check supported browsers for a required prefix
        Set<Prefix> required = Sets.newHashSet();
        for (Browser browser : support.supportedBrowsers()) {
            double lowestSupported = support.lowestSupportedVersion(browser);
            double lastPrefixed = PrefixInfo.lastPrefixedVersion(property.get(), browser);
            if (lowestSupported <= lastPrefixed) required.add(browser.prefix());
        }

        System.out.println(required);

        if (required.isEmpty()) {
            // no required prefixes, so remove unnecessary prefixed declarations if allowed
            if (remove) removeAllPrefixed(declaration.group().get(), property.get());
        } else {
            // add all applicable prefixed declarations
            addPrefixes(declaration, property.get(), required);
        }
    }

    /** removes any prefixed declarations in the same rule that are the same property */
    private void removeAllPrefixed(SyntaxCollection<Rule, Declaration> declarations, Property property) {
        for (Declaration d : declarations) {
            if (d.isPrefixed() && d.isPropertyIgnorePrefix(property)) d.detach();
        }
    }

    /** prepend prefixes before the declaration */
    private void addPrefixes(Declaration original, Property property, Iterable<Prefix> prefixes) {
        // create a collection of all sibling prefixed declarations
        Set<Declaration> declarations = Sets.newHashSet();
        for (Declaration declaration : original.group().get()) {
            if (declaration.isPrefixed()) declarations.add(declaration);
        }

        for (Prefix prefix : prefixes) {
            Declaration found = null;
            for (Declaration declaration : declarations) {
                PropertyName pn = declaration.propertyName();
                if (pn.hasPrefix(prefix) && pn.matchesIgnorePrefix(property)) {
                    found = declaration;
                    break;
                }
            }

            if (found == null) {
                Declaration prefixed = PrefixerUtil.createPrefixed(original, prefix, support);
                original.prepend(prefixed);
            } else if (rearrange) {
                original.group().get().moveBefore(original, found);
            }
        }
    }

    /**
     * Creates a new instance of the {@link Prefixer} plugin with default browser version support levels: IE7+, latest versions of
     * Safari, IE Mobile and Opera Mini, last 2 versions of Firefox and Chrome, last 4 versions of IOS Safari and last 3 versions
     * of Android Browser.
     *
     * @return The new {@link Prefixer} instance.
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
     */
    public static Prefixer customBrowserSupport() {
        return new Prefixer();
    }
}
