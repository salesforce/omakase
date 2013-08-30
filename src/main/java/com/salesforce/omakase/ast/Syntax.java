/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * A distinct unit of syntax within CSS.
 * 
 * <p>
 * {@link Syntax} objects are used to represent the individual pieces of content of the parsed CSS source, and are the
 * primary objects used to construct the AST (Abstract Syntax Tree). Not all {@link Syntax} objects have content
 * directly associated with them. Some are used to represent the logical grouping of content, such as the {@link Rule}.
 * 
 * <p>
 * Each unit has a particular line and column indicating where it was parsed within the source.
 * 
 * <p>
 * It's important to remember that <em>unrefined</em> Syntax objects, unless validation is performed, may actually
 * contain invalid CSS. Simply refining the syntax unit will verify it's grammatical compliance, which can be coupled
 * with (linting?) to ensure correct usage.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description("parent interface of all subscribable units")
public interface Syntax extends Writable {
    /**
     * The line number within the source where this {@link Syntax} unit was parsed.
     * 
     * @return The line number.
     */
    int line();

    /**
     * The column number within the source where this {@link Syntax} unit was parsed.
     * 
     * @return The column number.
     */
    int column();

    /**
     * Gets the name of this syntax unit for filtering purposes.
     * 
     * <p>
     * The primary purpose of this is to allow checking the name of something before actually refining it. For example,
     * for {@link Declaration}s this returns the property-name, which you may want to check first before performing
     * rework (which may require refinement of the declaration). Many syntax units return a useful value here (e.g., for
     * {@link ClassSelector} it returns the class name), however for units without an associated logical "name" an empty
     * string is returned.
     * 
     * @return The name, or an empty string if there isn't one.
     */
    String filterName();
}
