/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link AtRule}.
 * 
 * @see AtRule
 * @see RawAtRuleParserTest
 * 
 * @author nmcwilliams
 */
public class RawAtRuleParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        TokenFactory tf = tokenFactory();

        stream.skipWhitepace();

        int startingLine = stream.line();
        int startingColumn = stream.column();

        // must begin with '@'
        if (!stream.optionallyPresent(Tokens.AT_RULE)) return false;

        // read the name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) throw new ParserException(stream, Message.MISSING_AT_RULE_NAME);

        // read everything up until the end of the at-rule expression (usually a semicolon or open bracket).
        int line = stream.line();
        int column = stream.column();
        String content = stream.until(tf.atRuleExpressionEnd()).trim();
        RawSyntax expression = content.isEmpty() ? null : new RawSyntax(line, column, content);

        // skip whitespace after the expression
        stream.skipWhitepace();

        RawSyntax block = null;

        // parse the termination (usually ';' or the at-rule block)
        if (!stream.optionallyPresent(tf.atRuleTermination()) && tf.atRuleBlockBegin().matches(stream.current())) {
            line = stream.line();
            column = stream.column();
            content = stream.chompEnclosedValue(tf.atRuleBlockBegin(), tf.atRuleBlockEnd()).trim();
            block = content.isEmpty() ? null : new RawSyntax(line, column, content);
        }

        // expression content must be present
        if (expression == null && block == null) throw new ParserException(stream, Message.MISSING_AT_RULE_VALUE);

        // create the new at-rule and associate comments
        AtRule rule = new AtRule(startingLine, startingColumn, name.get(), expression, block, broadcaster);
        rule.comments(stream.flushComments());

        // notifier listeners of the new at-rule
        broadcaster.broadcast(rule);

        return true;
    }
}
