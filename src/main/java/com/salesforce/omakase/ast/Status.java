/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.ast;

import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;

/**
 * Represents the broadcast status of {@link Syntax} unit.
 * <p>
 * See the main readme for more information on the idea of phases. Basically, there are several phases in which a particular
 * {@link Syntax} unit may be broadcasted. The refine phase, the process phase and the validate phase. Each unit must only be
 * emitted at most once per phase.
 *
 * @author nmcwilliams
 */
public enum Status {
    /** The unit is unrefined and unbroadcasted */
    RAW {
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

    /** The unit has completed the {@link SubscriptionPhase#REFINE} phase */
    PARSED { // no further parsing necessary. does not guarantee refined
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.PROCESS;
        }
    },

    /** The unit has completed the {@link SubscriptionPhase#PROCESS} phase */
    PROCESSED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return phase == SubscriptionPhase.VALIDATE;
        }
    },

    /** The unit has completed the {@link SubscriptionPhase#VALIDATE} phase */
    VALIDATED {
        @Override
        public boolean shouldBroadcastForPhase(SubscriptionPhase phase) {
            return false;
        }
    },

    /** The unit should not be emitted any further */
    NEVER_EMIT {
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
}
