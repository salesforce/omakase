/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.*;

import com.salesforce.omakase.ast.Syntax;

/**
 * Use this annotation to subscribe to {@link Syntax} objects when the method is expected to change or modify the object
 * or CSS source.
 * 
 * <p>
 * Examples of rework include adding cache-busters to urls, changing class names, flipping directions for RTL support,
 * etc... It basically represents changing the content.
 * 
 * <p>
 * The one an only parameter for methods with this annotation should be one of the {@link Syntax} types.
 * 
 * <p>
 * If the method does not intend to change the content or object, use {@link Observe} instead.
 * 
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rework {
}
