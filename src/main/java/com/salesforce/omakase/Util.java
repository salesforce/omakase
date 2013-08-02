/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * General utilities.
 * 
 * @author nmcwilliams
 */
public final class Util {
    private Util() {}

    /**
     * Creates an <em>immutable</em> copy of the given list. If the given list is null, an empty {@link ImmutableList}
     * will be returned instead.
     * 
     * @param <T>
     *            The Type of the items in the original list.
     * @param original
     *            Create an {@link ImmutableList} from the items in this list.
     * @return An {@link ImmutableList} with the same items as the original, or an empty {@link ImmutableList} if the
     *         original is null.
     */
    public static <T> ImmutableList<T> immutable(@Nullable List<T> original) {
        return original == null ? ImmutableList.<T>of() : ImmutableList.copyOf(original);
    }
}
