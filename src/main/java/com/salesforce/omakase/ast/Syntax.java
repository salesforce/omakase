/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.ast.selector.Selector;
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

    /**
     * TODO Description
     * 
     * @param comment
     *            TODO
     * @return TODO
     */
    Syntax comment(String comment);

    /**
     * Gets all comments <em>associated</em> with this {@link Syntax} unit.
     * 
     * <p>
     * A comment is associated if it either directly belongs to this {@link Syntax} unit, or if it belongs to any of the
     * child {@link Syntax} units within this one.
     * 
     * <p>
     * This is generally the method you want. To get only the comments directly associated with this {@link Syntax} unit
     * (i.e., appear at the beginning of the content of this unit) use {@link #ownComments()} instead.
     * 
     * @return The list of comments.
     */
    List<String> comments();

    /**
     * Gets only the comments that are at the beginning of the content of this {@link Syntax} unit.
     * 
     * <p>
     * If this {@link Syntax} unit has direct content associated with it (e.g., a {@link Selector}, this will be any
     * comments at the beginning of the content. Otherwise if this {@link Syntax} unit only has child {@link Syntax}
     * units, this will be any comments at the beginning of the first child unit with actual content.
     * 
     * @return All comments at the beginning of this {@link Syntax} unit.
     */
    List<String> ownComments();
}
