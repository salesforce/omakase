/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Helper for creating instances of library-provided {@link Plugin}s.
 * 
 * @author nmcwilliams
 */
final class Suppliers {
    /** map of suppliers for all library-provided plugins. This map isn't type safe... so don't screw it up */
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
    private Suppliers() {}

    /**
     * Gets the supplier for the given class.
     * 
     * @param <T>
     *            Type of class/supplier.
     * @param klass
     *            Get a supplier for this class.
     * @return The supplier for the class, or {@link Optional#absent()} if not present.
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
