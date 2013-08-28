/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

/**
 * The difference phases in the processing lifecycle.
 * 
 * @author nmcwilliams
 */
public enum SubscriptionPhase {
    /** Before all processing and validation */
    PREPROCESS,
    /** During processing (rework, observe) */
    PROCESS,
    /** During validation, after processing is complete */
    VALIDATE
}
