/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

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
public interface Syntax {
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
     * TODO Description
     * 
     * @return TODO
     */
    String filterName();
}
