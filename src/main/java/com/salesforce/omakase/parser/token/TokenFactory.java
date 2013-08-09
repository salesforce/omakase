/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface TokenFactory {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token selectorBegin();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token selectorDelimiter();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token selectorEnd();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token declarationBlockBegin();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token declarationBlockEnd();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token declarationBegin();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token declarationDelimiter();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token declarationEnd();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Token propertyNameEnd();
}
