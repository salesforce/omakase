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

package com.salesforce.omakase.plugin.core;

import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.As;

/**
 * A plugin that stores the parsed {@link Stylesheet} object.
 * <p>
 * Use this plugin when you want to get a reference to the top-level {@link Stylesheet} object instead of creating your own
 * custom plugin.
 *
 * @author nmcwilliams
 */
public final class SyntaxTree implements Plugin {
    private Stylesheet stylesheet;

    /**
     * Sets the stylesheet. Library method - do not call directly.
     *
     * @param stylesheet
     *     The stylesheet.
     */
    @Observe
    public void stylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Gets the {@link Stylesheet} instance.
     *
     * @return The {@link Stylesheet}.
     */
    public Stylesheet stylesheet() {
        return stylesheet;
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }
}
