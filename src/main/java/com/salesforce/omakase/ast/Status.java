/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.SubscriptionPhase;

/**
 * Represents the broadcast status of {@link Syntax} unit.
 *
 * @author nmcwilliams
 */
public enum Status {
    /** For units that should never be broadcasted */
    DO_NOT_BROADCAST {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** The unit has never been broadcasted. */
    UNBROADCASTED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return true;
        }
    },

    /** The unit is currently being broadcasted */
    BROADCASTING {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** The unit has been broadcasted in the {@link SubscriptionPhase#PREPROCESS} phase */
    BROADCASTED_PREPROCESS {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.PROCESS || phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** The unit has been broadcasted in the {@link SubscriptionPhase#PROCESS} phase */
    BROADCASTED_PROCESS {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** The unit has been broadcasted in the {@link SubscriptionPhase#VALIDATE} phase */
    BROADCASTED_VALIDATION {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    };

    /**
     * Gets whether a {@link Syntax} unit with this status should be broadcasted.
     *
     * @param phase
     *     The {@link SubscriptionPhase} to check.
     *
     * @return True if the unit should be broadcasted based on the given phase.
     */
    public abstract boolean shouldBroadcastForPhase(SubscriptionPhase phase);

    /**
     * Gets the next status for a {@link Syntax} unit based on the given phase.
     *
     * @param phase
     *     The current phase that the unit was broadcasted under.
     *
     * @return The next status level.
     */
    public Status nextStatus(SubscriptionPhase phase) {
        switch (phase) {
            case PREPROCESS:
                return Status.BROADCASTED_PREPROCESS;
            case PROCESS:
                return Status.BROADCASTED_PROCESS;
            case VALIDATE:
                return Status.BROADCASTED_VALIDATION;
        }
        return null;
    }
}
