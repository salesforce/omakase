/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.SubscriptionPhase;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum Status {
    /** TODO */
    DO_NOT_BROADCAST {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** TODO */
    UNBROADCASTED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return true;
        }
    },

    /** TODO */
    BROADCASTING {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** TODO */
    BROADCASTED_PREPROCESS {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.PROCESS || phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** TODO */
    BROADCASTED_PROCESS {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** TODO */
    BROADCASTED_VALIDATION {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    };

    /**
     * TODO Description TODO
     * 
     * @param phase
     *            TODO
     * @return TODO
     */
    public abstract boolean shouldBroadcastForPhase(SubscriptionPhase phase);

    /**
     * TODO Description
     * 
     * @param phase
     *            TODO
     * @return TODO
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
