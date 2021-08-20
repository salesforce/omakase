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

package com.salesforce.omakase.plugin.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;
import com.salesforce.omakase.util.CssAnnotations;
import com.salesforce.omakase.util.Values;

/**
 * DirectionFlipPlugin changes the direction of CSS property names, keywords, and term lists from left to right, or from right to
 * left.
 *
 * @author david.brady
 */
public final class DirectionFlipPlugin implements DependentPlugin {
    private static final Map<Property, Property> PROPERTIES_TO_FLIP = new ImmutableMap.Builder<Property, Property>()
        .put(Property.LEFT, Property.RIGHT)
        .put(Property.RIGHT, Property.LEFT)

        .put(Property.BORDER_LEFT, Property.BORDER_RIGHT)
        .put(Property.BORDER_LEFT_COLOR, Property.BORDER_RIGHT_COLOR)
        .put(Property.BORDER_LEFT_STYLE, Property.BORDER_RIGHT_STYLE)
        .put(Property.BORDER_LEFT_WIDTH, Property.BORDER_RIGHT_WIDTH)

        .put(Property.BORDER_RIGHT, Property.BORDER_LEFT)
        .put(Property.BORDER_RIGHT_COLOR, Property.BORDER_LEFT_COLOR)
        .put(Property.BORDER_RIGHT_STYLE, Property.BORDER_LEFT_STYLE)
        .put(Property.BORDER_RIGHT_WIDTH, Property.BORDER_LEFT_WIDTH)

        .put(Property.BORDER_TOP_LEFT_RADIUS, Property.BORDER_TOP_RIGHT_RADIUS)
        .put(Property.BORDER_TOP_RIGHT_RADIUS, Property.BORDER_TOP_LEFT_RADIUS)
        .put(Property.BORDER_BOTTOM_LEFT_RADIUS, Property.BORDER_BOTTOM_RIGHT_RADIUS)
        .put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Property.BORDER_BOTTOM_LEFT_RADIUS)

        .put(Property.PADDING_LEFT, Property.PADDING_RIGHT)
        .put(Property.PADDING_RIGHT, Property.PADDING_LEFT)
        .put(Property.MARGIN_LEFT, Property.MARGIN_RIGHT)
        .put(Property.MARGIN_RIGHT, Property.MARGIN_LEFT)
        .put(Property.NAV_LEFT, Property.NAV_RIGHT)
        .put(Property.NAV_RIGHT, Property.NAV_LEFT)

        .build();

    private static final Map<Keyword, Keyword> KEYWORDS_TO_FLIP = new ImmutableMap.Builder<Keyword, Keyword>()
        .put(Keyword.LTR, Keyword.RTL)
        .put(Keyword.RTL, Keyword.LTR)
        .put(Keyword.LEFT, Keyword.RIGHT)
        .put(Keyword.RIGHT, Keyword.LEFT)

        .put(Keyword.E_RESIZE, Keyword.W_RESIZE)
        .put(Keyword.W_RESIZE, Keyword.E_RESIZE)
        .put(Keyword.NE_RESIZE, Keyword.NW_RESIZE)
        .put(Keyword.NW_RESIZE, Keyword.NE_RESIZE)
        .put(Keyword.NESW_RESIZE, Keyword.NWSE_RESIZE)
        .put(Keyword.NWSE_RESIZE, Keyword.NESW_RESIZE)
        .put(Keyword.SE_RESIZE, Keyword.SW_RESIZE)
        .put(Keyword.SW_RESIZE, Keyword.SE_RESIZE)

        .build();

    private static final Set<Property> FLIP_PERCENTAGE = ImmutableSet.of(
        Property.BACKGROUND,
        Property.BACKGROUND_POSITION,
        Property.BACKGROUND_POSITION_X);

    private static final Set<Property> FOUR_TERM_PROPERTIES = ImmutableSet.of(
        Property.PADDING,
        Property.MARGIN,
        Property.BORDER_COLOR,
        Property.BORDER_STYLE,
        Property.BORDER_WIDTH);

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(DeclarationPlugin.class);
    }

    /**
     * Checks for a {@link CssAnnotation} indicating not to flip anything.
     */
    private boolean hasNoFlip(Declaration declaration) {
        return declaration.hasAnnotation(CssAnnotations.NOFLIP);
    }

    /**
     * Flips keywords.
     *
     * @param value
     *     keywordValue to be flipped.
     */
    @Rework
    public void flipKeyword(KeywordValue value) {
        if (!hasNoFlip(value.declaration())) {
            value.asKeyword().map(KEYWORDS_TO_FLIP::get).ifPresent(value::keyword);
        }
    }

    /**
     * Flips property names and/or property values.
     *
     * @param declaration
     *     Declaration to be flipped.
     */
    @Rework
    public void flipDeclaration(Declaration declaration) {
        if (hasNoFlip(declaration)) return;

        Optional<Property> property = declaration.propertyName().asPropertyIgnorePrefix();
        if (!property.isPresent()) return; // must be a known property

        // flip the property name if applicable
        property.map(PROPERTIES_TO_FLIP::get).ifPresent(declaration::propertyName);

        // flip property values
        // Careful!  If a handler depends on the changes a previous handler made,
        // it won't be able to use the property or property value we've grabbed above.
        if (handleFourTerms(declaration, property.get())) return;
        if (handlePercentages(declaration, property.get())) return;
        handleBorderRadius(declaration, property.get());
    }

    private boolean handleFourTerms(Declaration declaration, Property property) {
        // for patterns such as 1 2 3 4, swap 2 and 4
        if (FOUR_TERM_PROPERTIES.contains(property) && declaration.propertyValue().countTerms() == 4) {
            ImmutableList<Term> terms = declaration.propertyValue().terms();
            declaration.propertyValue(PropertyValue.of(terms.get(0), terms.get(3), terms.get(2), terms.get(1)));
            return true;
        }
        return false;
    }

    private boolean handlePercentages(Declaration declaration, Property property) {
        if (FLIP_PERCENTAGE.contains(property)) {
            for (Term term : declaration.propertyValue().terms()) {
                // can't handle left, right, or center yet
                if (term instanceof KeywordValue) {
                    Keyword keyword = ((KeywordValue)term).asKeyword().orElse(null);
                    if (keyword == Keyword.LEFT || keyword == Keyword.CENTER || keyword == Keyword.RIGHT) return false;
                }

                // flip the first percentage
                if (term instanceof NumericalValue) {
                    NumericalValue numerical = (NumericalValue)term;
                    if (numerical.unit().isPresent() && numerical.unit().get().equals("%")) {
                        numerical.value(100 - numerical.doubleValue());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean handleBorderRadius(Declaration declaration, Property property) {
        if (Property.BORDER_RADIUS == property) {
            List<PropertyValue> split = Values.split(OperatorType.SLASH, declaration.propertyValue());
            List<PropertyValue> join = new ArrayList<>();
            if (split.size() == 1 || split.size() == 2) {
                for (PropertyValue val : split) {
                    join.add(flipBorderRadiusSet(val));
                }
                declaration.propertyValue(Values.join(OperatorType.SLASH, join));
                return true;
            }
        }
        return false;
    }

    /*
     * If a border radius has 2, 3, or 4 terms, they'll be flipped using these patterns:
     *
     * <ul>
     *     <li>a b => b a</li>
     *     <li>a b c => b a b c</li>
     *     <li>a b c d => b a d c</li>
     * </ul>
     */
    private PropertyValue flipBorderRadiusSet(PropertyValue value) {
        List<Term> terms = value.terms();
        switch (terms.size()) {
        case 2:
            return PropertyValue.of(terms.get(1), terms.get(0));
        case 3:
            // the 2nd term, when flipped, is used in both the first and 3rd positions.  Use a copy of the term for the 3rd.
            return PropertyValue.of(terms.get(1), terms.get(0), terms.get(1).copy(),
                terms.get(2));
        case 4:
            return PropertyValue.of(terms.get(1), terms.get(0), terms.get(3), terms.get(2));
        default:
            return value;
        }
    }
}
