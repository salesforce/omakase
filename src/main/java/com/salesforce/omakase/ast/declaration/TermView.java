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

/**
 * TODO delete
 * Represents a special custom {@link Term} that is a wrapper or container of {@link TermListMember}s.
 * <p/>
 * This interface should be implemented whenever your custom {@link Term} will output inner or child {@link Term}s.
 *
 * @author nmcwilliams
 */
public interface TermView extends TermListMember {
    /**
     * Returns all of the {@link Term}s contained within this view. That is, this should return all inner or child {@link Term}s
     * that will be output when this instance itself is output.
     *
     * @return The terms.
     */
    Iterable<Term> terms();
}
