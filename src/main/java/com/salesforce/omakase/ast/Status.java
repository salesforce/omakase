/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.ast;

import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;

/**
 * Represents the broadcast status of {@link Syntax} unit.
 * <p/>
 * See Context for more information on the idea of phases. Basically, there are three phases in which a particular {@link Syntax}
 * unit may be broadcasted. First the preprocess phase, then the process phase, then finally the validate phase. Each unit must
 * only be broadcasted at most once per phase. This phase of a unit is checked via the {@link Syntax#status()} method.
 *
 * @author nmcwilliams
 */
public enum Status {
    /** The unit has never been broadcasted */
    UNBROADCASTED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return true;
        }
    },

    /** The unit has been given to a {@link Broadcaster} but has been emitted yet */
    QUEUED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return true;
        }
    },

    /** The unit is currently being emitted */
    EMITTING {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** The unit has been broadcasted (emitted) in the {@link SubscriptionPhase#PROCESS} phase */
    PROCESSED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** The unit has been broadcasted (emitted)  in the {@link SubscriptionPhase#VALIDATE} phase */
    VALIDATED {
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
    public static Status nextStatusAfterPhase(SubscriptionPhase phase) {
        switch (phase) {
        case PROCESS:
            return Status.PROCESSED;
        case VALIDATE:
            return Status.VALIDATED;
        }
        return null;
    }
}
