/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.validator;

import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SyntaxValidator extends Plugin {
    /**
     * This method passes in the {@link ErrorManager} instance to be used for reporting all validation errors.
     * 
     * @param em
     *            The {@link ErrorManager} instance.
     */
    void errorManager(ErrorManager em);
}
