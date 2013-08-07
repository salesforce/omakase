/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import javax.annotation.concurrent.Immutable;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.consumer.Plugin;

/**
 * Parses a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
@Immutable
public class DeclarationParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Iterable<Plugin> consumers) {
        // skip whitespace
        stream.skipWhitepace();

        // the first non comment or space character must match the beginning of a declaration
        if (!tokenFactory().declarationBegin().matches(stream.current())) return false;

        // line and columns must be calculated before content
        int line = stream.line();
        int column = stream.column();

        // take everything until the end of declaration token
        String content = stream.until(tokenFactory().declarationEnd());

        Declaration declaration = factory().declaration()
            .content(content)
            .line(line)
            .column(column)
            .build();

        notify(consumers, declaration);

        return true;
    }
}
