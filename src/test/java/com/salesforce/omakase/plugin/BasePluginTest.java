/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.google.common.collect.Lists;
import com.salesforce.omakase.emitter.Subscribable;
import org.junit.Test;
import org.reflections.Reflections;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link BasePlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class BasePluginTest {
    @Test
    public void hasMethodForEverySubscribable() {
        int numMethods = BasePlugin.class.getDeclaredMethods().length;

        Reflections reflections = new Reflections("com.salesforce.omakase.ast");
        int expected = Lists.newArrayList(reflections.getTypesAnnotatedWith(Subscribable.class)).size();

        assertThat(numMethods)
            .overridingErrorMessage("BasePlugin.java must have a subscription method for each subscribable syntax type")
            .isEqualTo(expected);
    }
}
