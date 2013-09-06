/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

/**
 * Interface for {@link Syntax} units that can have CSS comments associated with them.
 *
 * @author nmcwilliams
 */
public interface Commentable extends Syntax {
    /**
     * Adds all of the given comments.
     *
     * @param commentsToAdd
     *     Add these comments.
     *
     * @return this, for chaining.
     */
    Commentable comments(Iterable<String> commentsToAdd);

    /**
     * Gets all comments <em>associated</em> with this {@link Syntax} unit.
     *
     * @return The list of comments. Never returns null.
     */
    List<String> comments();
}
