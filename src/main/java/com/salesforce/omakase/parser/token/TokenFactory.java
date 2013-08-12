/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Selector;
import com.salesforce.omakase.parser.Parser;

/**
 * A factory for retrieving various {@link Token}s. Mainly using by {@link Parser}s.
 * 
 * <p>
 * The motivation for using a factory interface for tokens is that it provides the ability for highly-customized input
 * source code grammar. This could be used, for example, to enable grammar similar to the popular Stylus open source
 * library.
 * 
 * @author nmcwilliams
 */
public interface TokenFactory {
    /**
     * Gets the {@link Token} representing what the first character of a {@link Selector} must be.
     * 
     * @return {@link Token} representing the first character of a {@link Selector}.
     */
    Token selectorBegin();

    /**
     * Gets the {@link Token} representing the delimiter between {@link Selector}s.
     * 
     * @return {@link Token} representing the {@link Selector} delimiter.
     */
    Token selectorDelimiter();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Selector}.
     * 
     * @return {@link Token} representing the end of the {@link Selector}.
     */
    Token selectorEnd();

    /**
     * Gets the {@link Token} representing the beginning of a declaration block.
     * 
     * @return {@link Token} representing the beginning of a declaration block.
     */
    Token declarationBlockBegin();

    /**
     * Gets the {@link Token} representing the end of a declaration block.
     * 
     * @return {@link Token} representing the end of a declaration block.
     */
    Token declarationBlockEnd();

    /**
     * Gets the {@link Token} representing what the first character of a {@link Declaration} must be (property name).
     * 
     * @return {@link Token} representing the first character of a {@link Declaration}.
     */
    Token declarationBegin();

    /**
     * Gets the {@link Token} representing the delimiter between {@link Declaration}s.
     * 
     * @return The {@link Token} representing the delimiter between {@link Declaration}s.
     */
    Token declarationDelimiter();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Declaration}.
     * 
     * @return The {@link Token} representing the end of a {@link Declaration}.
     */
    Token declarationEnd();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Declaration}'s property name.
     * 
     * @return The {@link Token} representing what indicates the end of a property name.
     */
    Token propertyNameEnd();
}
