package com.salesforce.omakase.plugin.other;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.*;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DirectionFlipPlugin changes the direction of CSS property names, keywords, and term lists from left to right, or from right to
 * left.
 *
 * @author david.brady
 */
public class DirectionFlipPlugin implements DependentPlugin {

    /*
     * Set of properties that are flipped directly to another property.
     */
    private static final Map<Property, Property> PROPERTIES_THAT_FLIP = new ImmutableMap.Builder<Property, Property>()
        .put(Property.BORDER_BOTTOM_LEFT_RADIUS, Property.BORDER_BOTTOM_RIGHT_RADIUS)
        .put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Property.BORDER_BOTTOM_LEFT_RADIUS)
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
        .put(Property.LEFT, Property.RIGHT)
        .put(Property.MARGIN_LEFT, Property.MARGIN_RIGHT)
        .put(Property.MARGIN_RIGHT, Property.MARGIN_LEFT)
        .put(Property.NAV_LEFT, Property.NAV_RIGHT)
        .put(Property.NAV_RIGHT, Property.NAV_LEFT)
        .put(Property.PADDING_LEFT, Property.PADDING_RIGHT)
        .put(Property.PADDING_RIGHT, Property.PADDING_LEFT)
        .put(Property.RIGHT, Property.LEFT)
        .build();

    /*
     * Set of keywords that are flipped directly to another keyword
     */
    private static final Map<Keyword, Keyword> KEYWORDS_THAT_FLIP = new ImmutableMap.Builder<Keyword, Keyword>()
        .put(Keyword.E_RESIZE, Keyword.W_RESIZE)
        .put(Keyword.LEFT, Keyword.RIGHT)
        .put(Keyword.LTR, Keyword.RTL)
        .put(Keyword.NE_RESIZE, Keyword.NW_RESIZE)
        .put(Keyword.NESW_RESIZE, Keyword.NWSE_RESIZE)
        .put(Keyword.NW_RESIZE, Keyword.NE_RESIZE)
        .put(Keyword.NWSE_RESIZE, Keyword.NESW_RESIZE)
        .put(Keyword.RIGHT, Keyword.LEFT)
        .put(Keyword.RTL, Keyword.LTR)
        .put(Keyword.SE_RESIZE, Keyword.SW_RESIZE)
        .put(Keyword.SW_RESIZE, Keyword.SE_RESIZE)
        .put(Keyword.W_RESIZE, Keyword.E_RESIZE)
        .build();

    /*
     * Set of properties that have flippable percentage values.
     */
    private static final Set<Property> PROPERTIES_WITH_FLIPPABLE_PERCENTAGE =
        ImmutableSet.of(
            Property.BACKGROUND,
            Property.BACKGROUND_POSITION,
            Property.BACKGROUND_POSITION_X);

    /*
     * Set of properties whose property values may flip if they match the
     * four-part pattern.
     */
    private static final Set<Property> FOUR_PART_PROPERTIES_THAT_SHOULD_FLIP =
        ImmutableSet.of(
            Property.BORDER_COLOR,
            Property.BORDER_STYLE,
            Property.BORDER_WIDTH,
            Property.PADDING);

    /*
     * Set of properties that indicate if a four part property should not
     * be flipped.
     */
    private static final Set<Keyword> LEFT_RIGHT_CENTER =
        ImmutableSet.of(
            Keyword.LEFT,
            Keyword.RIGHT,
            Keyword.CENTER);

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(AutoRefiner.class).declarations();
    }

    /**
     * Flips property names and/or property values.
     *
     * @param declaration
     *     Declaration to be flippped
     */
    @Rework
    public void rework(Declaration declaration) {
        Optional<Property> optionalProperty = declaration.propertyName().asPropertyIgnorePrefix();

        if (!optionalProperty.isPresent()) {
            return;
        }

        // flip the property name
        Property property = optionalProperty.get();
        handleFlippablePropertyName(declaration, property);

        // flip property values
        // Careful!  If a handler depends on the changes a previous handler made,
        // it won't be able to use the property or property value we've grabbed above.
        if (handleFlippableFourPartProperties(declaration, property)) return;
        if (handleFlippablePercentages(declaration, property)) return;
        handleFlippableBorderRadius(declaration, property);
    }

    /**
     * Flips keywords.
     *
     * @param keywordValue
     *     keywordValue to be flipped
     */
    @Rework
    public void rework(KeywordValue keywordValue) {
        Optional<Keyword> optionalKeyword = keywordValue.asKeyword();
        if (optionalKeyword.isPresent()) {
            Keyword keyword = optionalKeyword.get();
            if (KEYWORDS_THAT_FLIP.containsKey(keyword)) {
                keywordValue.keyword(KEYWORDS_THAT_FLIP.get(keyword));
            }
        }
    }

    private boolean handleFlippablePropertyName(Declaration declaration, Property property) {
        if (PROPERTIES_THAT_FLIP.containsKey(property)) {
            declaration.propertyName(PROPERTIES_THAT_FLIP.get(property));
            return true;
        }
        return false;
    }

    private boolean handleFlippableBorderRadius(Declaration declaration, Property property) {
        if (Property.BORDER_RADIUS == property) {
            PropertyValue[] values = splitPropertyValueAtSlash(declaration.propertyValue());
            switch (values.length) {
            case 1:
                declaration.propertyValue(flipBorderRadiusSet(declaration.propertyValue()));
                return true;
            case 2:
                values[0] = flipBorderRadiusSet(values[0]);
                values[1] = flipBorderRadiusSet(values[1]);
                values[0].append(OperatorType.SLASH);
                for (PropertyValueMember member : values[1].members()) {
                    values[0].append(member);
                }
                declaration.propertyValue(values[0]);
                return true;
            }
        }
        return false;
    }

    private boolean handleFlippablePercentages(Declaration declaration, Property property) {
        if (PROPERTIES_WITH_FLIPPABLE_PERCENTAGE.contains(property)) {
            Iterator<Term> originalTermIter = declaration.propertyValue().terms().iterator();
            PropertyValue replacement = new PropertyValue();
            boolean flipped = false;
            while (originalTermIter.hasNext()) {
                Term term = originalTermIter.next();
                // we never flip if the term list includes left, right, or center
                if (isLeftRightCenter(term)) {
                    return false;
                }
                // flip the first percentage we see
                if (!flipped) {
                    Term flippedTerm = flipPercentage(term);
                    if (flippedTerm != term) {
                        term = flippedTerm;
                        flipped = true;
                    }
                }
                replacement.append(term);
                if (originalTermIter.hasNext()) {
                    replacement.append(OperatorType.SPACE);
                }
            }
            // only
            if (flipped) {
                declaration.propertyValue(replacement);
                return true;
            }
        }
        return false;
    }

    private boolean handleFlippableFourPartProperties(Declaration declaration, Property property) {
        ImmutableList<Term> terms = declaration.propertyValue().terms();
        if (FOUR_PART_PROPERTIES_THAT_SHOULD_FLIP.contains(property) && terms.size() == 4) {
            declaration.propertyValue(PropertyValue.ofTerms(OperatorType.SPACE, terms.get(0), terms.get(3), terms.get(2), terms.get(1)));
            return true;
        }
        return false;
    }

    // @todo Candidate for a utility method?
    private PropertyValue[] splitPropertyValueAtSlash(PropertyValue value) {
        PropertyValue beforeSlash = new PropertyValue();
        PropertyValue afterSlash = new PropertyValue();
        PropertyValue currentPropertyValue = beforeSlash;
        SyntaxCollection<PropertyValue, PropertyValueMember> members = value.members();
        for (PropertyValueMember member : members) {
            if (member instanceof Operator && ((Operator)member).type() == OperatorType.SLASH) {
                currentPropertyValue = afterSlash;
                continue;
            }
            currentPropertyValue.append(member);
        }
        return afterSlash.members().size() == 0 ? new PropertyValue[]{beforeSlash} : new PropertyValue[]{beforeSlash, afterSlash};
    }

    /*
     * If a border radius has 2, 3, or 4 terms, they'll be flipped using these patterns:
     *
     * <ul>
     *     <li>a b => b a</li>
     *     <li>a b c => b a b c</li>
     *     <li>a b c d => b a d c</li>
     * </ul>
     *
     * Otherwise, the terms aren't flipped at all
     */
    private PropertyValue flipBorderRadiusSet(PropertyValue value) {
        splitPropertyValueAtSlash(value);
        List<Term> terms = value.terms();
        switch (terms.size()) {
        case 2:
            return PropertyValue.ofTerms(OperatorType.SPACE, terms.get(1), terms.get(0));
        case 3:
            // the 2nd term, when flipped, is used in both the first and 3rd positions.  Use a copy of the term for the 3rd.
            return PropertyValue.ofTerms(OperatorType.SPACE, terms.get(1), terms.get(0), (Term)terms.get(1).copy(), terms.get(2));
        case 4:
            return PropertyValue.ofTerms(OperatorType.SPACE, terms.get(1), terms.get(0), terms.get(3), terms.get(2));
        default:
            return value;
        }
    }

    private boolean isLeftRightCenter(Term term) {
        if (term instanceof KeywordValue) {
            Optional<Keyword> keyword = ((KeywordValue)term).asKeyword();
            return keyword.isPresent() && LEFT_RIGHT_CENTER.contains(keyword.get());
        } else {
            return false;
        }
    }

    /*
     * If the term passed in is a percentage, the value for that term will be subtracted
     * from 100 and returned as a new term.  Otherwise, this returns the passed in term.
     */
    private Term flipPercentage(Term term) {
        if (term instanceof NumericalValue) {
            NumericalValue numericalValue = (NumericalValue)term;
            if (numericalValue.unit().isPresent() && numericalValue.unit().get().equals("%")) {
                NumericalValue newValue = new NumericalValue(100 - numericalValue.doubleValue());
                newValue.unit("%");
                return newValue;
            }
        }
        return term;
    }

}
