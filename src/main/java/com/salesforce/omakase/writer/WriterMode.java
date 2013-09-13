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

import com.salesforce.omakase.ast.Rule;

/**
 * The various levels of compression and minification for output.
 *
 * @author nmcwilliams
 */
public enum WriterMode {
    /** Outputs newlines, whitespace, etc... Usually for development mode or testing. */
    VERBOSE,

    /** Outputs each {@link Rule} on a single line, mostly minified. Useful for testing or debugging. */
    INLINE,

    /** Outputs fully minified and compressed code. Usually for production environments. */
    COMPRESSED
}
