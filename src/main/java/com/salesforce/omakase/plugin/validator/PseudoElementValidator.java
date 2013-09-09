/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.validator;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.emitter.Validate;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

/**
 * TESTME
 * <p/>
 * Validates that {@link PseudoElementSelector}s are last within a selector sequence (the last {@link SelectorPart} within a
 * {@link Selector}).
 * <p/>
 * "A selector is a chain of one or more sequences of simple selectors separated by combinators. <b>One pseudo-element may be
 * appended to the last sequence of simple selectors in a selector.</b>" (http://www.w3 .org/TR/css3-selectors/#selector-syntax).
 *
 * @author nmcwilliams
 */
public class PseudoElementValidator implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(AutoRefiner.class).selectors();
    }

    /**
     * Validates that the pseudo element is always last, as according to the CSS spec.
     *
     * @param selector
     *     The pseudo element.
     * @param em
     *     The error manager.
     */
    @Validate
    public void validate(PseudoElementSelector selector, ErrorManager em) {
        if (!selector.isLast()) {
            em.report(ErrorLevel.FATAL, selector, Message.PSEUDO_ELEMENT_LAST);
        }
    }
}
