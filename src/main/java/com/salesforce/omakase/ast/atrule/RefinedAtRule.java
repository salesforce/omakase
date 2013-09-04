/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.atrule;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Statement;

/**
 * A refined {@link AtRule}, with the expression and optional block fully parsed.
 * 
 * @author nmcwilliams
 */
public interface RefinedAtRule extends Statement {
    /**
     * Gets the at-rule expression, if present.
     * 
     * @return The expression, or {@link Optional#absent()} if not present.
     */
    Optional<AtRuleExpression> expression();

    /**
     * Gets the at-rule block, if present.
     * 
     * @return The block, or {@link Optional#absent()} if not present.
     */
    Optional<AtRuleBlock> block();
}
