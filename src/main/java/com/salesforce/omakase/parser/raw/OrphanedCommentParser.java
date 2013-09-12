/*
 * Copyright (c) 2013. UPDATE COPYRIGHT
 */

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

import java.util.List;

/**
 * TESTME
 * <p/>
 * Parses an {@link OrphanedComment}.
 */
public class OrphanedCommentParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // collect comments
        List<String> comments = stream.collectComments().flushComments();
        if (comments.isEmpty()) return false;

        // broadcast each comment individually
        for (String comment : comments) {
            broadcaster.broadcast(new OrphanedComment(comment));
        }

        return true;
    }
}
