/**
 * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import com.salesforce.omakase.ast.Syntax;

/**
 * A broadcaster that queues broadcasts.
 * 
 * @author nmcwilliams
 */
public final class QueuingBroadcaster implements Broadcaster {
    private final Deque<Syntax> queue = new ArrayDeque<>();
    private final Broadcaster relay;

    private State state = State.READY;

    private enum State {
        READY,
        PAUSED,
        BROADCASTING
    }

    /**
     * Constructs a new {@link QueuingBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     * 
     * @param relay
     *            Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public QueuingBroadcaster(Broadcaster relay) {
        this.relay = checkNotNull(relay, "relay cannot be null");
    }

    /**
     * Pauses the queue. While pause, all broadcasts will be stored and will not be relayed until {@link #resume()} is
     * called.
     */
    public void pause() {
        state = State.PAUSED;
    }

    /**
     * Resumes broadcasts. Any broadcasts currently in the queue will be immediately sent out.
     */
    public void resume() {
        flush();
        state = State.READY;
    }

    /**
     * Broadcasts all events currently in the queue, until the queue is empty.
     */
    private void flush() {
        while (!queue.isEmpty()) {
            Syntax queued = queue.pop();
            relay.broadcast(queued);
        }
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        queue.push(syntax);

        if (state == State.PAUSED || state == State.BROADCASTING) return;

        flush();
    }

}
