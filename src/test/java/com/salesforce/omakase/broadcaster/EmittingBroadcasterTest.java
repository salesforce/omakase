/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.emitter.Rework;
import com.salesforce.omakase.emitter.SubscriptionPhase;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmittingBroadcaster}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class EmittingBroadcasterTest {
    @Test
    public void emits() {
        EmittingBroadcaster eb = new EmittingBroadcaster();
        InnerPlugin ip = new InnerPlugin();
        eb.register(ip);
        eb.phase(SubscriptionPhase.PROCESS);
        eb.broadcast(new ClassSelector(1, 1, "test"));
        assertThat(ip.called).isTrue();
    }

    @Test
    public void relaysToInner() {
        InnerBroadcaster ib = new InnerBroadcaster();
        EmittingBroadcaster eb = new EmittingBroadcaster(ib);

        eb.broadcast(new ClassSelector(1, 1, "test"));
        assertThat(ib.called).isTrue();
    }

    public static final class InnerPlugin implements Plugin {
        boolean called = false;

        @Rework
        public void rework(ClassSelector selector) {
            called = true;
        }
    }

    public static final class InnerBroadcaster implements Broadcaster {
        boolean called = false;

        @Override
        public <T extends Syntax> void broadcast(T syntax) {
            called = true;
        }

        @Override
        public <T extends Syntax> void broadcast(T syntax, boolean propagate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Broadcaster wrap(Broadcaster relay) {
            throw new UnsupportedOperationException();
        }
    }
}
