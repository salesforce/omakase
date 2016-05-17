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

package com.salesforce.omakase.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Prefix;

/**
 * Utilities for finding prefixed equivalents.
 *
 * @author nmcwilliams
 */
public final class Equivalents {
    private Equivalents() {}

    /**
     * Finds all peers of the given unit that match the same name, but with a prefix.
     * <p>
     * What constitutes a "peer", and how matches are determined is based on the given {@link EquivalentWalker}.
     *
     * @param peer
     *     The peer that is unprefixed. Usually this is either the same unit as unprefixed, or a parent of unprefixed.
     * @param unprefixed
     *     The unprefixed unit.
     * @param walker
     *     Handles the specifics of finding the peers.
     * @param <P>
     *     (P)eer. The type of units that are considered peers and also the type of unit that will be returned in the map.
     * @param <N>
     *     (N)amed unit. The unit that has the unprefixed name. This could be the same type as {@link P}, or it could be a unit
     *     that's a child of {@link P}.
     *
     * @return All found prefixed equivalents, or an empty immutable multimap if none are found.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static <P, N extends Named> Multimap<Prefix, P> prefixes(P peer, N unprefixed, EquivalentWalker<P, N> walker) {
        Multimap<Prefix, P> multimap = null;

        P previous = walker.previous(peer);
        while (previous != null) {
            N located = walker.locate(previous, unprefixed);
            if (located != null) {
                if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                multimap.put(Prefixes.parsePrefix(located.name()).get(), previous);
            }
            previous = (located != null || walker.walkAll()) ? walker.previous(previous) : null;

        }

        // look for unprefixed versions appearing after the unprefixed one
        P next = walker.next(peer);
        while (next != null) {
            N located = walker.locate(next, unprefixed);
            if (located != null) {
                if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                multimap.put(Prefixes.parsePrefix(located.name()).get(), next);
            }
            next = (located != null || walker.walkAll()) ? walker.next(next) : null;
        }

        return multimap == null ? ImmutableMultimap.<Prefix, P>of() : multimap;
    }

    /**
     * Responsible for finding the next and previous "peers", and also for determining whether a "peer" is an prefixed
     * equivalent.
     *
     * @param <P>
     *     (P)eer. See {@link #prefixes(Object, Named, EquivalentWalker)} for more details.
     * @param <N>
     *     (N)amed unit. See {@link #prefixes(Object, Named, EquivalentWalker)} for more details.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public interface EquivalentWalker<P, N extends Named> {
        /**
         * Returns the named unit that is prefixed-equivalent within/of the given peer, or null if the given peer is not
         * prefixed-equivalent.
         *
         * @param peer
         *     Locate the prefixed unit within/of this peer.
         * @param unprefixed
         *     The original unprefixed unit.
         *
         * @return The prefixed-equivalent, or null.
         */
        N locate(P peer, N unprefixed);

        /**
         * Gets the previous unit, or null if none.
         *
         * @param peer
         *     Find the unit previous to this one.
         *
         * @return The previous unit, or null if none.
         */
        P previous(P peer);

        /**
         * Gets the next unit, or null if none.
         *
         * @param peer
         *     Find the unit after this one.
         *
         * @return The next unit, or null if none.
         */
        P next(P peer);

        /**
         * Specifies whether we should walk all peers, or stop when a peer does not match.
         * <p>
         * In other words, return true to evaluate all peers, even when null is returned from {@link #locate(Object, Named)}, and
         * return false to stop walking (in that direction) once {@link #locate(Object, Named)} returns null.
         *
         * @return Whether all peers should be walked, or if we should stop walking a direction once a peer does not match.
         */
        boolean walkAll();
    }

    /** base for walkers that group any Groupable together */
    public abstract static class Base<G extends Groupable<?, G>, N extends Named> implements EquivalentWalker<G, N> {
        @Override
        public G previous(G peer) {
            return peer.previous().orNull();
        }

        @Override
        public G next(G peer) {
            return peer.next().orNull();
        }

        @Override
        public boolean walkAll() {
            return true;
        }
    }

    /** base for walkers that group {@link Rule}s together */
    public abstract static class RuleBase<N extends Named> implements EquivalentWalker<Rule, N> {
        @Override
        public Rule previous(Rule peer) {
            Optional<Statement> previous = peer.previous();
            if (previous.isPresent() && previous.get() instanceof Rule) {
                return (Rule)previous.get();
            }
            return null;
        }

        @Override
        public Rule next(Rule peer) {
            Optional<Statement> next = peer.next();
            if (next.isPresent() && next.get() instanceof Rule) {
                return (Rule)next.get();
            }
            return null;
        }

        @Override
        public boolean walkAll() {
            return false;
        }
    }

    /** base for walkers that group {@link AtRule}s together */
    public abstract static class AtRuleBase<N extends Named> implements EquivalentWalker<AtRule, N> {
        @Override
        public AtRule previous(AtRule peer) {
            Optional<Statement> previous = peer.previous();
            if (previous.isPresent() && previous.get() instanceof AtRule) {
                return (AtRule)previous.get();
            }
            return null;
        }

        @Override
        public AtRule next(AtRule peer) {
            Optional<Statement> next = peer.next();
            if (next.isPresent() && next.get() instanceof AtRule) {
                return (AtRule)next.get();
            }
            return null;
        }

        @Override
        public boolean walkAll() {
            return false;
        }
    }

    /** private utility */
    private static boolean isPrefixed(Named named) {
        return named.name().charAt(0) == '-';
    }

    /**
     * Finds declarations with prefixed-equivalent property names.
     * <p>
     * For example, given the following css:
     * <pre><code>
     *  .example {
     *      color: red;
     *      -webkit-border-radius: 3px;
     *      -moz-border-radius: 3px;
     *      border-radius: 3px;
     *  }
     * </code></pre>
     * <p>
     * When given the last declaration in the rule, this will locate both the {@code -webkit-border-radius} and the {@code
     * -moz-border-radius} declarations.
     */
    public static final EquivalentWalker<Declaration, Declaration> PROPERTIES = new Base<Declaration, Declaration>() {
        @Override
        public Declaration locate(Declaration peer, Declaration unprefixed) {
            // check if the declaration has the same property name, but prefixed
            return peer.isPrefixed() && peer.isPropertyIgnorePrefix(unprefixed.propertyName()) ? peer : null;
        }
    };

    /**
     * Finds declarations with prefixed-equivalent function values.
     * <p>
     * For example, given the following css:
     * <pre><code>
     *  .example {
     *      width: -webkit-calc(2px - 1px);
     *      color: red;
     *      width: calc(2px - 1px);
     *  }
     * </code></pre>
     * When given the last declaration in the rule and a functionName of "calc", this will locate the first declaration, which
     * contains the {@code -webkit-calc} function.
     */
    public static final EquivalentWalker<Declaration, FunctionValue> FUNCTION_VALUES = new Base<Declaration, FunctionValue>() {
        @Override
        public FunctionValue locate(Declaration peer, FunctionValue unprefixed) {
            // check if the declaration has the same property name as the prefixed one
            if (peer.isProperty(unprefixed.declaration().propertyName())) {
                // try to find a function value with the same name but prefixed
                for (FunctionValue function : Values.filter(FunctionValue.class, peer.propertyValue())) {
                    if (isPrefixed(function) && function.name().endsWith(unprefixed.name())) return function;
                }
            }
            return null;
        }
    };

    /**
     * Finds at-rules with prefixed-equivalent names.
     * <p>
     * For example, given the following css:
     * <pre><code>
     * &#64;-webkit-keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     *
     * &#64;keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     *
     * &#64;-moz-keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     * </code></pre>
     * When given the middle, unprefixed at-rule, the at-rules before and after it will be returned. Only immediately adjacent
     * at-rules will be considered.
     * <p>
     * Note that this only compares immediately adjacent peers, for as long as a match is found. The first peer that does not
     * match prefix and raw expression content will halt looking in that direction (before and after).
     */
    public static final EquivalentWalker<AtRule, AtRule> AT_RULES = new AtRuleBase<AtRule>() {
        @Override
        public AtRule locate(AtRule peer, AtRule unprefixed) {
            if (isPrefixed(peer)) {
                Prefixes.PrefixPair pair = Prefixes.splitPrefix(peer.name());
                if (pair.prefix().isPresent() && pair.unprefixed().equals(unprefixed.name())) {
                    // compare raw expressions
                    String unprefixedExpr = unprefixed.rawExpression().isPresent() ? unprefixed.rawExpression().get().content() : "";
                    String peerExpr = peer.rawExpression().isPresent() ? peer.rawExpression().get().content() : "";
                    if (unprefixedExpr.equals(peerExpr)) {
                        return peer;
                    }
                }
            }
            return null;
        }
    };

    /**
     * Finds rules with prefixed-equivalent pseudo element selectors.
     * <p>
     * For example, given the following css:
     * <pre><code>
     * &#64;::-moz-selection {
     *   color: red;
     * }
     * &#64;::selection {
     *   color: red;
     * }
     * </code></pre>
     * When given the second, unprefixed rule, the one before it will be returned. Only immediately adjacent rules will be
     * considered.
     * <p>
     * Note that this only compares immediately adjacent peers, for as long as a match is found. The first peer that does not
     * match will halt looking in that direction (before and after).
     */
    public static final EquivalentWalker<Rule, PseudoElementSelector> PSEUDO_ELEMENTS = new RuleBase<PseudoElementSelector>() {
        @Override
        public PseudoElementSelector locate(Rule peer, PseudoElementSelector unprefixed) {
            for (Selector selector : peer.selectors()) {
                Optional<PseudoElementSelector> s = Selectors.findPseudoElementSelector(selector, unprefixed.name(), false);
                if (s.isPresent() && isPrefixed(s.get())) {
                    return s.get();
                }
            }
            return null;
        }
    };

    /**
     * Finds rules with prefixed-equivalent pseudo class selectors.
     * <p>
     * For example, given the following css:
     * <pre><code>
     * &#64;:blah {
     *   color: red;
     * }
     * &#64;:blah {
     *   color: red;
     * }
     * </code></pre>
     * When given the second, unprefixed rule, the one before it will be returned. Only immediately adjacent rules will be
     * considered.
     * <p>
     * Note that this only compares immediately adjacent peers, for as long as a match is found. The first peer that does not
     * match will halt looking in that direction (before and after).
     */
    public static final EquivalentWalker<Rule, PseudoClassSelector> PSEUDO_CLASSES = new RuleBase<PseudoClassSelector>() {
        @Override
        public PseudoClassSelector locate(Rule peer, PseudoClassSelector unprefixed) {
            for (Selector selector : peer.selectors()) {
                Optional<PseudoClassSelector> s = Selectors.findPseudoClassSelector(selector, unprefixed.name(), false);
                if (s.isPresent() && isPrefixed(s.get())) {
                    return s.get();
                }
            }
            return null;
        }
    };
}
