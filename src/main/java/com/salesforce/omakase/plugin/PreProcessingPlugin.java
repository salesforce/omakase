/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

/**
 * A {@link Plugin} that wishes to be notified before and after preprocessing.
 *
 * @author nmcwilliams
 */
public interface PreProcessingPlugin extends Plugin {
    /** This method will be called before preprocessing begins. */
    void beforePreProcess();

    /** This method will be called after preprocessing is completed (but before rework and validation). */
    void afterPreProcess();
}
