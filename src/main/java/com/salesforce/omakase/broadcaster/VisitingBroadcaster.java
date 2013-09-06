/**
j,  * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;

/**
 * TESTME A {@link Broadcaster} that will store all broadcasted events. Replay the broadcasts using {@link #visit()}.
 * The broadcasts can be replayed multiple times.
 * 
 * @author nmcwilliams
 */
public final class VisitingBroadcaster extends AbstractBroadcaster {
    private final List<Syntax> list = Lists.newArrayList();
    private boolean visiting;

    /**
     * Constructs a new {@link VisitingBroadcaster} instance that will relay all broadcasted events to the given
     * {@link Broadcaster}.
     * 
     * @param relay
     *            Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public VisitingBroadcaster(Broadcaster relay) {
        wrap(checkNotNull(relay, "relay cannot be null"));
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        list.add(syntax);

        // while a visit is in progress, immediately send out any received broadcasts (can occur if a refinement results
        // in new syntax instances, or rework results in new syntax units being added).
        if (visiting) {
            relay.broadcast(syntax);
        }
    }

    /**
     * Replays all broadcasted events.
     */
    public void visit() {
        visiting = true;

        // make a defensive copy since the list may be modified as a result of this call
        ImmutableList<Syntax> snapshot = ImmutableList.copyOf(list);

        for (Syntax syntax : snapshot) {
            relay.broadcast(syntax);
        }

        visiting = false;
    }

}
