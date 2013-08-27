/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class QueuingBroadcaster implements Broadcaster {
    private final Deque<QueuedBroadcast> queue = new ArrayDeque<>();
    private final Broadcaster relay;

    private State state = State.READY;

    private enum State {
        READY,
        PAUSED,
        BROADCASTING
    }

    /**
     * TODO
     * 
     * @param relay
     *            TODO
     */
    public QueuingBroadcaster(Broadcaster relay) {
        this.relay = checkNotNull(relay, "relay cannot be null");
    }

    /**
     * TODO Description
     * 
     */
    public void pause() {
        state = State.PAUSED;
    }

    /**
     * TODO Description
     * 
     */
    public void resume() {
        flush();
        state = State.READY;
    }

    /**
     * TODO Description
     * 
     */
    private void flush() {
        while (!queue.isEmpty()) {
            QueuedBroadcast queued = queue.pop();
            relay.broadcast(queued.type, queued.syntax);
        }
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        queue.push(new QueuedBroadcast(type, syntax));

        if (state == State.PAUSED || state == State.BROADCASTING) return;

        flush();
    }

    private static final class QueuedBroadcast {
        final SubscriptionType type;
        final Syntax syntax;

        public QueuedBroadcast(SubscriptionType type, Syntax syntax) {
            this.type = type;
            this.syntax = syntax;
        }
    }
}
