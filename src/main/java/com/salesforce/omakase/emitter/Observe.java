/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.*;

import com.salesforce.omakase.ast.Syntax;

/**
 * Use this annotation to subscribe to {@link Syntax} objects when the method <em>will not change</em> any aspect of the
 * CSS or object.
 * 
 * <p>
 * The one an only parameter for methods with this annotation should be one of the {@link Syntax} types.
 * 
 * <p>
 * Currently, this annotation is equivalent to {@link Rework}, with a clearer indication of the intended effect of the
 * method. This equivalence with {@link Rework} may change in the future, so take care to annotate properly.
 * 
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Observe {
}
