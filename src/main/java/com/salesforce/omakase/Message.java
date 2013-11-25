/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    NO_SUPPLIER("No supplier defined for %s. Use require(Class, Supplier) instead."),
    EXPECTED_SELECTOR("Expected to find a selector (after the trailing '%s')"),
    MISSING_PSEUDO_NAME("expected to find a valid pseudo element or class name " +
        "([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_VALID_ID("expected to find a valid id name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_VALID_CLASS("expected to find a valid class name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    MISSING_AT_RULE_NAME("Expected to find a valid at-rule name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXPECTED_ATTRIBUTE_NAME("Expected to find the attribute name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    KEYFRAME_NAME("Expected to find a valid keyframe name ([-_0-9a-zA-Z], cannot start with a number, --, or -[0-9])"),
    EXTRANEOUS("Unparsable text found at the end of the source '%s'"),
    UNPARSABLE_SELECTOR("Unable to parse remaining selector content (Check that the selector is valid and is allowed here)"),
    UNPARSABLE_DECLARATION_VALUE("Unable to parse remaining declaration value '%s' (did you forget a semicolon?)"),
    UNPARSABLE_CONDITIONAL_CONTENT("Unable to parse the remaining content in the conditional at-rule: %s"),
    UNPARSABLE_MEDIA("Unable to parse the remaining content in the media query '%s'"),
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
    PSEUDO_ELEMENT_LAST("Pseudo elements must be last in the selector sequence"),
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
    MISSING_CONDITIONAL_BLOCK("Missing block for the conditional at-rule (@oif). The block must be encased within curly " +
        "braces {}"),
    UNEXPECTED_AFTER_QUOTE("Unexpected content in url after closing quote '%s'"),
    MALFORMED_DECLARATION("Malformed declaration. Did you forget to add the property name or the colon delimiter?"),
    MEDIA_EXPR("Missing the media query's expression"),
    MEDIA_BLOCK("Missing the media query's block"),
    DIDNT_FIND_MEDIA_LIST("Expected to parse a valid media query list"),
    MISSING_MEDIA_TYPE("Expected to find media type (e.g., 'screen', the type is required after 'only' or 'not')"),
    MISSING_AND("Expected to find keyword 'and'"),
    MISSING_FEATURE("Expected to find media feature name (e.g., 'min-width')"),
    MISSING_MEDIA_TERMS("Expected to find one or more terms"),
    MISSING_COLON("Expected to find ':' after the property name"),
    UNEXPECTED_KEYFRAME_NAME("Unexpected content after the keyframes name: %s"),
    MISSING_KEYFRAMES_BLOCK("Missing keyframes block"),
    MISSING_PERCENTAGE("Missing '%' in keyframe selector");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    /**
     * Gets the error message. If the message contains parameters for {@link String#format(String, Object...)} , use {@link
     * #message(Object...)} instead.
     */
    public String message() {
        return message;
    }

    /**
     * Gets the error message, passing in the given arguments to {@link String#format(String, Object...)}.
     *
     * @param parameters
     *     Arguments to {@link String#format(String, Object...)}.
     *
     * @return The formatted message.
     */
    public String message(Object... parameters) {
        return String.format(message, parameters);
    }
}
