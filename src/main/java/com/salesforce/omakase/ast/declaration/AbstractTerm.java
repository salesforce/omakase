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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;

/**
 * Base class for {@link Term}s.
 *
 * @author nmcwilliams
 */
public abstract class AbstractTerm extends AbstractGroupable<TermList, TermListMember> implements Term {
    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AbstractTerm() {}

    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AbstractTerm(int line, int column) {
        super(line, column);
    }

    @Override
    public void detach() {
        // not that this can't be supported, but I can't think of any obvious use cases where it wouldn't be better
        // to replace the members of the term list with an explicit list instead. It usually won't make sense to detach a term
        // in isolation as it could impact the meaning of the surrounding terms. In any case, the main reason for this is
        // because custom function RefinerStrategy objects may parse args not directly stored in a TermList, and we don't want
        // #isWritable returning false for those terms.
        String msg = "Detaching terms is not supported. Use SyntaxCollection#replaceExistingWith as an alternative.";
        throw new UnsupportedOperationException(msg);
    }

    @Override
    public boolean isWritable() {
        // based on the override to #detach as explained above. Other behavior is probably based on this behavior too,
        // like the loose terms in a MediaQueryExpression.
        return true;
    }

    @Override
    protected TermListMember self() {
        return this;
    }
}
