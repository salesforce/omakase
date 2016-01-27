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
 * <p/>
 * Error messages are gathered in one place like this to keep code less cluttered. It also achieves greater message consistency
 * since all the messages are placed and can be reviewed together.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public enum Message {
    DUPLICATE_PLUGIN("Only one plugin instance of each type allowed: %s"),
    ONLY_ONE_TOKEN_FACTORY("Only one token factory is allowed, but an instance of %s was already registered"),
    NO_SUPPLIER("No supplier defined for %s. Use require(Class, Supplier) instead."),
    EXPECTED_SELECTOR("Expected to find a selector (after the trailing '%s')"),
    MISSING_PSEUDO_NAME("expected to find a valid pseudo element or class name " +
        "([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_VALID_ID("expected to find a valid id name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_VALID_CLASS("expected to find a valid class name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    MISSING_AT_RULE_NAME("Expected to find a valid at-rule name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_ATTRIBUTE_NAME("Expected to find the attribute name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    KEYFRAME_NAME("Expected to find a valid keyframe name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    CONDITION_NAME("Expected to find a valid condition name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXTRANEOUS("Unparsable text found at the end of the source '%s'"),
    UNPARSABLE_SELECTOR("Unable to parse remaining selector content (Check that the selector is valid and is allowed here)"),
    UNPARSABLE_DECLARATION_VALUE("Unable to parse remaining declaration value '%s' (did you forget a semicolon?)"),
    UNPARSABLE_CONDITIONAL_CONTENT("Unable to parse the remaining content in the conditional at-rule: %s"),
    UNPARSABLE_MEDIA("Unable to parse the remaining content in the media query '%s'"),
    UNPARSABLE_FONT_FACE("Unable to parse the remaining content in the font face rule '%s'"),
    UNPARSABLE_SUPPORTS("Unable to parse the remaining content in the supports at-rule  '%s'"),
    UNPARSABLE_KEYFRAMES("Unable to parse the remaining content in the keyframes at-rule '%s'"),
    EXPECTED_VALUE("Expected to parse a property value"),
    EXPECTED_TO_FIND("Expected to find %s"),
    EXPECTED_CLOSING("Expected to find closing %s"),
    INVALID_HEX("Expected a hex color of length 3 or 6, but found %s"),
    EXPECTED_DECIMAL("Expected to find decimal value"),
    TRAILING_OPERATOR("Expected to find another term following the term operator (%s). Either the operator should be removed, " +
        "the subsequent term is missing, or the subsequent term is not currently recognized"),
    TRAILING_COMBINATOR("Trailing combinator (%s)"),
    TRAILING_AND("Trailing 'and' in media query"),
    TRAILING("Unexpected trailing '%s'"),
    NAME_SELECTORS_NOT_ALLOWED("universal or type selector not allowed here"),
    COMMENTS_NOT_ALLOWED("Comments not allowed in this location. Please place the comment at the beginning of the " +
        "declaration" +
        " or selector sequence. (While the CSS specification does allow comments here," +
        " it could result in unexpected behavior after minification and removal of the comment.)"),
    MISSING_COMMENT_CLOSE("Unclosed comment"),
    PSEUDO_ELEMENT_LAST("Only pseudo-classes are allowed after the pseudo-element '%s'"),
    ONE_PARAM("Methods annotated with @PreProcess, @Observe or @Rework must have exactly " +
        "one parameter (the Syntax type): on method %s"),
    TWO_PARAMS("Methods annotated with @Validate must have exactly two parameters" +
        " (first being the Syntax type, second being an ErrorManager): on method %s"),
    ANNOTATION_EXCLUSIVE("The @PreProcess, @Observe, @Rework and @Validate annotations are mutually exclusive: '%s"),
    MISSING_ERROR_MANAGER("The second parameter for methods annotated with @Validate must be of type ErrorManager: on " +
        "method %s"),
    MISSING_AT_RULE_VALUE("Expected to find an at-rule expression, block, or both"),
    CANT_MODIFY_SYNTAX_TREE("Cannot modify syntax tree after it has been frozen"),
    MISSING_OPERATOR_NEAR_COMMENT("In order to place a comment here, please ensure that it is properly surrounded by the " +
        "appropriate operator (.e.g., a space character). Otherwise, this will result in an invalid valid during " +
        "minification " +
        "when comments are removed."),
    EXPECTED_IMPORTANT("Expected to find 'important'"),
    EXPECTED_ATTRIBUTE_MATCH_VALUE("Expected to find the attribute selector's match value (a string or an identifier)"),
    MISSING_CONDITIONAL_EXPRESSION("Missing expression for conditional at-rule (@if). Expressions must be within " +
        "parenthesis, " +
        "e.g., (ie7)"),
    MISSING_CONDITIONAL_BLOCK("Missing block for the conditional at-rule (@if). The block must be encased within curly " +
        "braces {}"),
    UNEXPECTED_AFTER_QUOTE("Unexpected content in url after closing quote '%s'"),
    MALFORMED_DECLARATION("Malformed declaration. Did you forget to add the property name or the colon delimiter?"),
    MEDIA_EXPR("Missing the media query's expression"),
    SUPPORTS_EXPR("Missing the supports condition"),
    MEDIA_BLOCK("Missing the media query's block"),
    SUPPORTS_BLOCK("Missing the block for the supports at-rule"),
    DIDNT_FIND_MEDIA_LIST("Expected to parse a valid media query list"),
    MISSING_MEDIA_TYPE("Expected to find media type (e.g., 'screen', the type is required after 'only' or 'not')"),
    MISSING_AND("Expected to find keyword 'and'"),
    MISSING_FEATURE("Expected to find media feature name (e.g., 'min-width')"),
    MISSING_MEDIA_TERMS("Expected to find one or more terms"),
    FONT_FACE("Missing the font face's block"),
    MISSING_COLON("Expected to find ':' after the property name"),
    UNEXPECTED_KEYFRAME_NAME("Unexpected content after the keyframes name: %s"),
    MISSING_KEYFRAMES_BLOCK("Missing keyframes block"),
    MISSING_PERCENTAGE("Missing '%' in keyframe selector"),
    UNEXPECTED_EXPRESSION_FONT_FACE("Unexpected expression after '@font-face'"),
    UNICODE_LONG("More than 6 hexidecimal or wildcard characters in a unicode range is not allowed"),
    HEX_AFTER_WILDCARD("Hexidecimal characters are not allowed after a wildcard in a unicode range"),
    WILDCARD_NOT_ALLOWED("Wildcard not allowed in unicode interval ranges"),
    BAD_DECLARATION_REFINER("DeclarationRefiner '%s' returned true but did not broadcast a PropertyValue");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    /**
     * Gets the error message, passing in the given arguments to {@link String#format(String, Object...)}.
     *
     * @param args
     *     Arguments to {@link String#format(String, Object...)}.
     *
     * @return The formatted message.
     */
    public String message(Object... args) {
        return (args == null || args.length == 0) ? message : String.format(message, args);
    }
}
