/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.basic;

import java.util.Set;

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Automatically refines all explicitly requested {@link Refinable} types.
 * 
 * <p>
 * Generally this is used when your plugin has a subscription to a particular detailed {@link Syntax} unit not exposed
 * during the high-level parsing phase. The {@link Refinable} responsible for parsing that syntax unit must be refined
 * before the syntax unit will be exposed.
 * 
 * <p>
 * For example, suppose you have a subscription to {@link FunctionValue}. You have two options to ensure you receive the
 * appropriate events. First, and more performant, you can also subscribe to {@link Declaration}, check the property
 * name for an expected value (e.g., "background") and call {@link Refinable#refine()} on the {@link Declaration} if it
 * matches. This is the best choice if you only cared about urls from certain property names, or the set of properties
 * that use url is small.
 * 
 * <p>
 * However, for something like {@link FunctionValue} that can appear in many different properties, the second option is
 * to use an {@link AutoRefiner} to automatically call {@link Refinable#refine()} on <em>every</em> {@link Declaration},
 * which will ensure you get every {@link FunctionValue} within the CSS source. This is the potentially less performant
 * option, so make the choice judiciously.
 * 
 * <p>
 * Example:
 * 
 * <pre><code> public class MyPlugin implements DependentPlugin {
 *   {@literal @}Override public void before(Context context) {
 *     context.require(AutoRefiner.class).include(Declaration.class);
 *   }
 *
 *   ...(subscriptions)...
 * }<code></pre>
 * 
 * @author nmcwilliams
 */
public class AutoRefiner implements Plugin {
    private final Set<Class<? extends Refinable<?>>> refinables = Sets.newHashSet();

    /**
     * Includes the given class in auto-refinement. This means that {@link Refinable#refine()} will be automatically
     * called on the instance.
     * 
     * @param klass
     *            The class to auto-refine.
     * @return this, for chaining.
     */
    public AutoRefiner include(Class<? extends Refinable<?>> klass) {
        refinables.add(klass);
        return this;
    }

    /**
     * Automatically refines anything that is refinable.
     * 
     * @param refinable
     *            A refinable object.
     */
    @Subscribe
    public void refine(Refinable<?> refinable) {
        if (refinables.contains(refinable.getClass())) refinable.refine();
    }
}
