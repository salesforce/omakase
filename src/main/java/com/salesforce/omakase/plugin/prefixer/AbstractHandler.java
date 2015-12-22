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
 * Base class for most {@link Handler} implementations.
 *
 * @param <T>
 *     (T)ype of prefixable AST object to handle.
 * @param <G>
 *     Type of the related prefixable equivalents. A Groupable, usually {@link Declaration} or {@link Statement}.
 *
 * @author nmcwilliams
 * @see Prefixer
 * @see Handler
 */
abstract class AbstractHandler<T, G extends Groupable<?, G>> implements Handler<T> {
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
                copy(subject, prefix, support);
            }
        }

        // any left over equivalents are unnecessary. remove or rearrange them if allowed
        if (!equivalents.isEmpty()) {
            if (prune) {
                Actions.destroy().apply(equivalents.values());
            } else if (rearrange) {
                Actions.<G>moveBefore().apply(subject, equivalents.values());
            }
        }

        return !required.isEmpty();
    }

    /** should return false if the instance should be skipped */
    protected abstract boolean applicable(T instance, SupportMatrix support);

    /** should return the main subject (prepend, append, and move stuff around this) */
    protected abstract G subject(T instance);

    /** should return the set of required {@link Prefix}es for the given instance */
    protected abstract Set<Prefix> required(T instance, SupportMatrix support);

    /** should return the set of related objects that may be existing prefixed equivalents for rearrangement or removal */
    protected abstract Multimap<Prefix, ? extends G> equivalents(T instance);

    /** should make (and prepends) a copy of the instance with the given prefix */
    protected abstract void copy(G original, Prefix prefix, SupportMatrix support);
}
