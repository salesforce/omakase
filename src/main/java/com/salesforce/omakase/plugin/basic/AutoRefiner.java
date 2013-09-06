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
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.emitter.PreProcess;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TESTME Automatically refines all explicitly requested {@link Refinable} types.
 * 
 * <p>
 * Generally this is used when your plugin has a subscription to a particular detailed {@link Syntax} unit not exposed
 * during the high-level parsing phase. The {@link Refinable} responsible for parsing that syntax unit must be refined
 * before the syntax unit will be exposed.
 * 
 * <p>
 * For example, suppose you have a subscription to {@link FunctionValue}. You have two options to ensure you receive the
 * appropriate events. First, and more performant, you can also subscribe to {@link Declaration}, check the property
 * name ({@link Syntax#filterName()}) for an expected value (e.g., "background") and call {@link Refinable#refine()} on
 * the {@link Declaration} if it matches. This is the best choice if you only cared about urls from certain property
 * names, or the set of properties that use url is small.
 * 
 * <p>
 * However, for something like {@link FunctionValue} that can appear in many different properties, the second option is
 * to use an {@link AutoRefiner} to automatically call {@link Refinable#refine()} on <em>every</em> {@link Declaration}
 * (see {@link AutoRefiner#declarations()}, which will ensure you get every {@link FunctionValue} within the CSS source.
 * This is the potentially less performant option, so make the choice judiciously.
 * 
 * <p>
 * Example:
 * 
 * <pre><code> public class MyPlugin implements DependentPlugin {
 *   {@literal @}Override public void before(PluginRegistry registry) {
 *     registry.require(AutoRefiner.class).declarations();
 *   }
 *
 *   ...(subscription methods)...
 * }<code></pre>
 * 
 * TODO add at-rules.
 * 
 * @author nmcwilliams
 */
public class AutoRefiner implements Plugin {
    private final Set<Class<? extends Refinable<?>>> refinables = Sets.newHashSet();
    private boolean all;

    /**
     * Specifies that all {@link Selector}s should be automatically refined.
     * 
     * @return this, for chaining.
     */
    public AutoRefiner selectors() {
        return include(Selector.class);
    }

    /**
     * Specifies that all {@link Declaration}s should be automatically refined.
     * 
     * @return this, for chaining.
     */
    public AutoRefiner declarations() {
        return include(Declaration.class);
    }

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
     * Specifies that <em>anything</em> that is {@link Refinable} should be automatically refined.
     * 
     * @return this, for chaining.
     */
    public AutoRefiner all() {
        all = true;
        return this;
    }

    /**
     * Automatically refines anything that is refinable.
     * 
     * @param refinable
     *            A refinable object.
     */
    @PreProcess
    public void refine(Refinable<?> refinable) {
        if (all || refinables.contains(refinable.getClass())) refinable.refine();
    }
}
