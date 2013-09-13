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

package com.salesforce.omakase.writer;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

import java.io.IOException;

/**
 * Customizes the writing of a particular {@link Syntax} unit.
 * <p/>
 * This allows you to override (or augment) the writing of any {@link Syntax} unit.
 * <p/>
 * <b>Important</b>: Some syntax units will not have their overrides kick in unless the parent unit is refined. For example, a
 * {@link ClassSelector} override will not be utilized unless {@link Selector#refine()} is called on the parent {@link Selector}.
 * An easy way to handle this is with an {@link AutoRefiner}. See the notes on that class for more information.
 *
 * @param <T>
 *     The Type of object being overridden.
 *
 * @author nmcwilliams
 */
public interface CustomWriter<T extends Writable> {
    /**
     * Writes the given unit to the given {@link StyleAppendable}.
     * <p/>
     * <b>Notes for implementation:</b>
     * <p/>
     * You can completely bypass the default writing behavior of the unit by simply writing out content to the {@link
     * StyleAppendable}.
     * <p/>
     * If you are augmenting the write process instead, you can output the default representation of the unit by calling {@link
     * StyleWriter#write(Writable, StyleAppendable)} before or after your augmentations, as appropriate.
     * <p/>
     * Do not use the {@link StyleWriter} in an attempt to write direct content (Strings, chars, etc...). Use the {@link
     * StyleAppendable}.
     * <p/>
     * The {@link StyleWriter} should be used to make decisions based on writer settings (e.g., compressed vs. verbose output
     * mode), as well as for writing inner or child {@link Writable}s.
     *
     * @param unit
     *     The unit to write.
     * @param writer
     *     Writer to use for output settings and for writing inner {@link Writable}s.
     * @param appendable
     *     Append direct content to this {@link StyleAppendable}.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    void write(T unit, StyleWriter writer, StyleAppendable appendable) throws IOException;
}
