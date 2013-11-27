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

import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.Actions;

import java.util.Collection;
import java.util.Set;

/**
 * Base class for most {@link PrefixerHandler} implementations.
 *
 * @param <T>
 *     (T)ype of prefixable AST object to handle.
 * @param <G>
 *     Type of the related prefixable equivalents. A (G)roupable, usually {@link Declaration} or {@link Statement}.
 *
 * @author nmcwilliams
 * @see Prefixer
 * @see PrefixerHandler
 * @see PrefixerHandlers
 */
abstract class PrefixerHandlerStandard<T, G extends Groupable<?, G>> implements PrefixerHandler<T> {
    @Override
    public boolean handle(T instance, boolean rearrange, boolean prune, SupportMatrix support) {
        if (!applicable(instance, support)) return false;

        // gather all required prefixes
        Set<Prefix> required = required(instance, support);

        // find all prefixed equivalents
        Multimap<Prefix, ? extends G> equivalents = equivalents(instance);

        // get the main unprefixed object of question
        G subject = subject(instance);

        // look through each required prefix to see whether we need to rearrange, remove or prepend
        for (Prefix prefix : required) {
            Collection<? extends G> matches = equivalents.get(prefix);
            if (!matches.isEmpty()) {
                if (rearrange) {
                    Actions.<G>moveBefore().apply(subject, matches);
                }
                equivalents.removeAll(prefix);
            } else {
                subject.prepend(subject.copy(prefix, support));
            }
        }

        // any left over equivalents are unnecessary. remove or rearrange them if allowed
        if (!equivalents.isEmpty()) {
            if (prune) {
                Actions.detach().apply(equivalents.values());
            } else if (rearrange) {
                Actions.<G>moveBefore().apply(subject, equivalents.values());
            }
        }

        return !required.isEmpty();
    }

    /** should return false if the instance should be skipped */
    abstract boolean applicable(T instance, SupportMatrix support);

    /** should return the main subject (prepend, append, and move stuff around this) */
    abstract G subject(T instance);

    /** should return the set of required {@link Prefix}es for the given instance */
    abstract Set<Prefix> required(T instance, SupportMatrix support);

    /** should return the set of related objects that may be existing prefixed equivalents for rearrangement or removal */
    abstract Multimap<Prefix, ? extends G> equivalents(T instance);
}
