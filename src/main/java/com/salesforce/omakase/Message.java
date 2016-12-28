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

package com.salesforce.omakase;

/**
 * Error messages.
 *
 * @author nmcwilliams
 */
public final class Message {
    // expected to find...
    public static final String EXPECTED_SELECTOR = "Expected to find a selector (after the trailing '%s')";
    public static final String MISSING_PSEUDO_NAME = "expected to find a valid pseudo element or " +
        "class name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])";
    public static final String EXPECTED_VALID_ID = "expected to find a valid id name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String EXPECTED_VALID_CLASS = "expected to find a valid class name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String MISSING_AT_RULE_NAME = "Expected to find a valid at-rule name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String EXPECTED_ATTRIBUTE_NAME = "Expected to find the attribute name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String KEYFRAME_NAME = "Expected to find a valid keyframe name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String CONDITION_NAME = "Expected to find a valid condition name ([-_0-9a-zA-Z], " +
        "cannot start with a number, --, or -[0-9])";
    public static final String EXPECTED_TO_FIND = "Expected to find %s";
    public static final String EXPECTED_CLOSING = "Expected to find closing %s";
    public static final String INVALID_HEX = "Expected a hex color of length 3 or 6, but found %s";
    public static final String EXPECTED_DECIMAL = "Expected to find decimal value";
    public static final String MISSING_AT_RULE_VALUE = "Expected to find an at-rule expression, block, or both";
    public static final String EXPECTED_IMPORTANT = "Expected to find 'important'";
    public static final String EXPECTED_ATTRIBUTE_MATCH_VALUE = "Expected to find the attribute selector's match value " +
        "(a string or an identifier)";
    public static final String MISSING_COLON = "Expected to find ':' after the property name";
    public static final String DIDNT_FIND_MEDIA_LIST = "Expected to parse a valid media query list";
    public static final String MISSING_MEDIA_TYPE = "Expected to find media type " +
        "(e.g., 'screen', the type is required after 'only' or 'not')";
    public static final String MISSING_AND = "Expected to find keyword 'and'";
    public static final String MISSING_FEATURE = "Expected to find media feature name (e.g., 'min-width')";
    public static final String MISSING_MEDIA_TERMS = "Expected to find one or more terms";
    public static final String MISSING_CONDITIONAL_EXPRESSION = "Missing expression for conditional at-rule (@if). " +
        "Expressions must be within parenthesis, e.g., (ie7)";
    public static final String MISSING_CONDITIONAL_BLOCK = "Missing block for the conditional at-rule (@if). " +
        "The block must be encased within curly braces {}";
    public static final String MEDIA_EXPR = "Missing the media query's expression";
    public static final String SUPPORTS_EXPR = "Missing the supports condition";
    public static final String MEDIA_BLOCK = "Missing the media query's block";
    public static final String SUPPORTS_BLOCK = "Missing the block for the supports at-rule";
    public static final String FONT_FACE = "Missing the font face's block";
    public static final String MISSING_COMMENT_CLOSE = "Unclosed comment";
    public static final String MISSING_KEYFRAMES_BLOCK = "Missing keyframes block";
    public static final String MISSING_PERCENTAGE = "Missing '%' in keyframe selector";

    // unparsable content
    public static final String EXTRANEOUS = "Unparsable text found at the end of the source '%s'";
    public static final String UNPARSABLE_SELECTOR = "Unable to parse remaining selector content " +
        "(Check that the selector is valid and is allowed here)";
    public static final String UNPARSABLE_DECLARATION_VALUE = "Unable to parse remaining declaration " +
        "value '%s' (did you forget a semicolon?)";
    public static final String UNPARSABLE_CONDITIONAL_CONTENT = "Unable to parse the remaining content in" +
        " the conditional at-rule: %s";
    public static final String UNPARSABLE_MEDIA = "Unable to parse the remaining content in the media query '%s'";
    public static final String UNPARSABLE_FONT_FACE = "Unable to parse the remaining content in the font face rule '%s'";
    public static final String UNPARSABLE_SUPPORTS = "Unable to parse the remaining content in the supports at-rule  '%s'";
    public static final String UNPARSABLE_KEYFRAMES = "Unable to parse the remaining content in the keyframes at-rule '%s'";
    public static final String UNEXPECTED_AFTER_QUOTE = "Unexpected content in url after closing quote '%s'";
    public static final String UNEXPECTED_KEYFRAME_NAME = "Unexpected content after the keyframes name: %s";
    public static final String UNEXPECTED_EXPRESSION_FONT_FACE = "Unexpected expression after '@font-face'";

    // trailing content
    public static final String TRAILING_OPERATOR = "Expected to find another term following the term operator. " +
        "Either the operator should be removed, the subsequent term is missing, " +
        "or the subsequent term is not currently recognized";
    public static final String TRAILING_COMBINATOR = "Trailing combinator (%s)";
    public static final String TRAILING_AND = "Trailing 'and' in media query";
    public static final String TRAILING = "Unexpected trailing '%s'";

    // X not allowed
    public static final String NAME_SELECTORS_NOT_ALLOWED = "universal or type selector not allowed here";
    public static final String UNICODE_LONG = "More than 6 hexidecimal or wildcard characters in a unicode range is not allowed";
    public static final String PSEUDO_ELEMENT_LAST = "Only pseudo-classes are allowed after the pseudo-element '%s'";
    public static final String HEX_AFTER_WILDCARD = "Hexidecimal characters are not allowed after a wildcard in a unicode range";
    public static final String WILDCARD_NOT_ALLOWED = "Wildcard not allowed in unicode interval ranges";

    // malformed subscription methods
    public static final String ONE_PARAM = "Methods annotated with @Observe or @Rework " +
        "must have exactly one parameter (the Syntax type): on method %s";
    public static final String TWO_PARAMS = "Methods annotated with @Validate must have exactly two parameters " +
        "(first being the Syntax type, second being an ErrorManager): on method %s";
    public static final String THREE_PARAMS = "Methods annotated with @Refine must have exactly three parameters " +
        "(first being the Syntax type, second being Grammar, third being Broadcaster): on method %s";
    public static final String ANNOTATION_EXCLUSIVE = "The @Observe, @Rework, @Validate and @Refine annotations " +
        "are mutually exclusive: '%s";
    public static final String MISSING_ERROR_MANAGER = "The second parameter for methods annotated with " +
        "@Validate must be of type ErrorManager: on method %s";
    public static final String MISSING_REFINABLE = "The first parameter for methods annotated with " +
        "@Refine must implement the Refinable interface: on method %s";
    public static final String MISSING_GRAMMAR = "The second parameter for methods annotated with " +
        "@Refine must be of type Grammar: on method %s";
    public static final String MISSING_BROADCASTER = "The third parameter for methods annotated with " +
        "@Refine must be of type Broadcaster: on method %s";

    // plugin registration
    public static final String DUPLICATE_PLUGIN = "Only one plugin instance of each type allowed: %s";
    public static final String UNIQUE_PLUGIN = "Only one %s is allowed. If multiple %1$ss are required," +
        " use each one in a different parsing operation";

    public static final String NO_SUPPLIER = "No supplier defined for %s. Use require(Class, Supplier) instead.";

    private Message() {}

    /** format a message with parameters */
    public static String fmt(String message, Object... args) {
        return (args == null || args.length == 0) ? message : String.format(message, args);
    }

    /** format a message with a Class as the parameter */
    public static String fmt(String message, Class<?> klazz) {
        return (klazz == null) ? message : String.format(message, klazz.getSimpleName());
    }
}
