/**
j,  * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * A {@link Broadcaster} that will store all broadcasted events. Replay the broadcasts using {@link #visit()} or
 * {@link #visitChanged()}. The broadcasts can be replayed multiple times.
 * 
 * @author nmcwilliams
 */
public final class VisitingBroadcaster implements Broadcaster {
    private final List<Syntax> created = Lists.newArrayList();
    private final List<Syntax> changed = Lists.newArrayList();
    private final Broadcaster relay;
    private boolean visiting;

    /**
     * Constructs a new {@link VisitingBroadcaster} instance that will relay all broadcasted events to the given
     * {@link Broadcaster}.
     * 
     * @param relay
     *            Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public VisitingBroadcaster(Broadcaster relay) {
        this.relay = checkNotNull(relay, "relay cannot be null");
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        if (type == SubscriptionType.CREATED) {
            created.add(syntax);
        } else if (type == SubscriptionType.CHANGED) {
            changed.add(syntax);
        }

        // while a visit is in progress, immediately send out any received broadcasts (can occur if a refinement results
        // in new syntax instances, or rework results in new syntax units being added).
        if (visiting) {
            relay.broadcast(type, syntax);
        }
    }

    /**
     * Alias to {@link #visitCreated()}.
     */
    public void visit() {
        visitCreated();
    }

    /**
     * Replays all broadcasted events of type {@link SubscriptionType#CREATED}.
     */
    public void visitCreated() {
        visiting = true;

        // make a defensive copy since the list may be modified as a result of this call
        ImmutableList<Syntax> snapshot = ImmutableList.copyOf(created);

        for (Syntax syntax : snapshot) {
            relay.broadcast(SubscriptionType.CREATED, syntax);
        }

        visiting = false;
    }

    /**
     * Replays all broadcasted events of type {@link SubscriptionType#CHANGED}.
     * 
     */
    public void visitChanged() {
        visiting = true;

        // make a defensive copy since the list may be modified as a result of this call
        ImmutableList<Syntax> snapshot = ImmutableList.copyOf(changed);

        for (Syntax syntax : snapshot) {
            relay.broadcast(SubscriptionType.CHANGED, syntax);
        }

        visiting = false;
    }
}
