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
    void errorManager(ErrorManager em);
}
