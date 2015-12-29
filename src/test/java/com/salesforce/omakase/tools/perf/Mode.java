/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.tools.perf;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.tools.Tools;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

@SuppressWarnings("ALL")
enum Mode {
    /**
     * A simple collection of styles, with the minimum amount of parsing possible (e.g., omakase in 1-phase only).
     */
    LIGHT("light.css"),
    /**
     * A simple colleciton of styles, with normal parsing.
     */
    NORMAL("light.css"),

    /**
     * The kitchen-sink of styles, with normal parsing.
     */
    HEAVY("heavy.css"),

    /**
     * The kitchen-sink of styles, with auto prefixer behavior turned on.
     */
    PREFIX_HEAVY("heavy.css");

    private String source;

    Mode(String path) {
        try {
            StyleWriter writer = StyleWriter.verbose();
            Omakase.source(Tools.readFile("/perftest/" + path)).use(writer).process();
            this.source = writer.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String source() {
        return source;
    }
}
