/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.AutoRefiner;
import com.salesforce.omakase.plugin.SyntaxTree;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class Suppliers {

    private static final Map<Class<?>, Supplier<?>> map = ImmutableMap.<Class<?>, Supplier<?>>builder()
        .put(SyntaxTree.class, new Supplier<SyntaxTree>() {
            @Override
            public SyntaxTree get() {
                return new SyntaxTree();
            }
        })
        .put(AutoRefiner.class, new Supplier<AutoRefiner>() {
            @Override
            public AutoRefiner get() {
                return new AutoRefiner();
            }
        })
        .build();

    /** do not construct */
    private Suppliers() {};

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @return TODO
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Supplier<T>> get(Class<T> klass) {
        Supplier<?> found = map.get(klass);
        if (found != null) {
            /** cast is safe as long as the internal map is correctly formed */
            return Optional.of((Supplier<T>)found);
        }
        return Optional.absent();
    }
}
