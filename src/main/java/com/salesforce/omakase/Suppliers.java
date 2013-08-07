/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.Filter;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.SyntaxTree;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 */
public final class Suppliers {
    private static final Map<Class<?>, Supplier<?>> suppliers = ImmutableMap.<Class<?>, Supplier<?>>builder()
        .put(Filter.class, new Supplier<Filter>() {
            @Override
            public Filter get() {
                return new Filter();
            }

        })
        .put(SyntaxTree.class, new Supplier<SyntaxTree>() {
            @Override
            public SyntaxTree get() {
                return new SyntaxTree();
            }
        })
        .build();

    public static <T extends Plugin> Supplier<T> supplier(Class<T> type) {
        return null;
    }

}
