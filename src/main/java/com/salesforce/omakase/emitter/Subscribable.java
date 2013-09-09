/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.plugin.Plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a {@link Syntax} unit can be <em>subscribed to</em> within a {@link Plugin}.
 * <p/>
 * Note that just because a {@link Syntax} unit can be subscribed to doesn't mean that it will necessarily be broadcasted. For
 * more information, see the notes on {@link Plugin}.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Subscribable {
}
