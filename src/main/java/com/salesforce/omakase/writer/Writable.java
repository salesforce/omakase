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

import java.io.IOException;

/**
 * Indicates that something can be written to a {@link StyleAppendable}.
 *
 * @author nmcwilliams
 */
public interface Writable {
    /**
     * Outputs this {@link Writable}'s string representation.
     * <p/>
     * <b>Important notes for implementation:</b>
     * <p/>
     * Do not use the {@link StyleWriter} in an attempt to write direct content (Strings, chars, etc...). Use the {@link
     * StyleAppendable}.
     * <p/>
     * The {@link StyleWriter} should be used to make decisions based on writer settings (e.g., compressed vs. verbose output
     * mode), as well as for writing inner or child {@link Writable}s. Do <b>not</b> call the {@link #write(StyleWriter,
     * StyleAppendable)} method directly on inner or child {@link Writable}s! That would bypass any overrides that are set on the
     * {@link StyleWriter}. Use {@link StyleWriter#write(Writable, StyleAppendable)} instead.
     *
     * @param writer
     *     Writer to use for output settings and for writing inner {@link Writable}s.
     * @param appendable
     *     Append direct content to this {@link StyleAppendable}.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    void write(StyleWriter writer, StyleAppendable appendable) throws IOException;
}
