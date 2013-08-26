/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface ErrorManager {
    void report(String id, ErrorLevel defaultLevel, Syntax cause, String message);
}
