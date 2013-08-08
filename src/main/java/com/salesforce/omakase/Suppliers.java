/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.google.common.base.Supplier;
import com.salesforce.omakase.plugin.Filter;
import com.salesforce.omakase.plugin.SyntaxTree;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 */
public final class Suppliers {
    /** supplies a new {@link Filter} instance */
    public static final Supplier<Filter> FILTER = new Supplier<Filter>() {
        @Override
        public Filter get() {
            return new Filter();
        }
    };

    /** supplies a new {@link SyntaxTree} instance */
    public static final Supplier<SyntaxTree> SYNTAX_TREE = new Supplier<SyntaxTree>() {
        @Override
        public SyntaxTree get() {
            return new SyntaxTree();
        }
    };

}
