/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.UnicodeRangeValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;
import com.salesforce.omakase.parser.token.Tokens;

import static com.salesforce.omakase.parser.Source.Snapshot;

/**
 * Parses a {@link UnicodeRangeValue}.
 * <p/>
 * There are three types of allowed values:
 * <p/>
 * - single codepoint (e.g. {@code U+416})
 * <p/>
 * - interval range (e.g. {@code U+400-4ff})
 * <p/>
 * - wildcard range (e.g. {@code U+4??})
 * <p/>
 * Check out the <a href='http://www.w3.org/TR/CSS21/syndata.html#tokenization (see UNICODE-RANGE)'>grammar</a> and the <a
 * href='http://dev.w3.org/csswg/css-fonts/#urange-value'>textual spec</a>.
 * <p/>
 * The grammar indicates otherwise, but textual spec says that only trailing '?' are allowed (not mixed in between hexidecimals),
 * and also that '?' is not allowed in ranges. Verified in chrome that this is how they treat it as well, so going with that (even
 * though it makes this parser much more complicated than otherwise). The textual spec also implies that a max of 5 '?' are
 * allowed, not 6, but we won't worry about that for now.
 * <p/>
 * This does <em>not</em> validate the range of the code points specified (unicode codepoint values must be between 0 and 10FFFF
 * inclusive, end ranges must come after start ranges, etc...). If this is important, a custom validation plugin can be written.
 * However this does validate the length of the code point, {1-6}.
 *
 * @author nmcwilliams
 * @see UnicodeRangeValue
 */
public final class UnicodeRangeValueParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // grab current position before parsing
        int line = source.originalLine();
        int column = source.originalColumn();

        // check for u+ or U+
        if (Tokens.U.matches(source.current()) && Tokens.PLUS.matches(source.peek())) {
            source.skip().skip(); // skip past u+

            // note: it must be a valid unicode-range term at this point, or we have to thrown an error
            StringBuilder builder = new StringBuilder("u+");

            // check for hexidecimal chars
            String hexidecimals = source.chomp(Tokens.HEXIDECIMAL);
            int hlen = hexidecimals.length();
            builder.append(hexidecimals);

            // check for wildcards
            Snapshot wildcardSnapshot = source.snapshot();
            String wildcards = source.chomp(Tokens.QUESTION);
            int wlen = wildcards.length();
            builder.append(wildcards);

            // must have at least one char, max of six
            final int len = hlen + wlen;
            if (len == 0) {
                throw new ParserException(source, Message.EXPECTED_TO_FIND, Tokens.HEXIDECIMAL.description());
            } else if (len > 6) {
                throw new ParserException(source, Message.UNICODE_LONG);
            }

            // no hexidecimals allowed after wildcards
            if (wlen > 0 && Tokens.HEXIDECIMAL.matches(source.current())) {
                throw new ParserException(source, Message.HEX_AFTER_WILDCARD);
            }

            // check for a range
            if (source.optionallyPresent(Tokens.HYPHEN)) {
                builder.append("-");

                // if there's a range and wildcard, throw an error
                if (wlen > 0) {
                    wildcardSnapshot.rollback(Message.WILDCARD_NOT_ALLOWED);
                }

                // check for hexidecimal chars
                hexidecimals = source.chomp(Tokens.HEXIDECIMAL);
                hlen = hexidecimals.length();
                builder.append(hexidecimals);

                // must have at least one char, max of six
                if (hlen == 0) {
                    throw new ParserException(source, Message.EXPECTED_TO_FIND, Tokens.HEXIDECIMAL.description());
                } else if (hlen > 6) {
                    throw new ParserException(source, Message.UNICODE_LONG);
                }

                // wildcards not allowed in second hexidecimal
                if (Tokens.QUESTION.matches(source.current())) {
                    throw new ParserException(source, Message.WILDCARD_NOT_ALLOWED);
                }
            }

            // create and broadcast the AST object
            UnicodeRangeValue range = new UnicodeRangeValue(line, column, builder.toString());
            range.comments(source.flushComments());

            broadcaster.broadcast(range);
            return true;
        }

        return false;
    }
}
