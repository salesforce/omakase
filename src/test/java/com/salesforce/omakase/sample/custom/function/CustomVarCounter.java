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

package com.salesforce.omakase.sample.custom.function;

import java.io.IOException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Sample class showing how you can subscribe to and work with your custom AST objects.
 * <p>
 * This just keeps a count of the number of custom functions that were parsed in the CSS source code.
 *
 * @author nmcwilliams
 */
public class CustomVarCounter implements Plugin {
    private final Multiset<String> details = HashMultiset.create();

    public int count() {
        return details.size();
    }

    @Observe
    public void observe(CustomVarFunction function) {
        details.add(function.arg());
    }

    public void summarize(Appendable out) throws IOException {
        out.append(String.format("\n\n%s total custom functions were found in the CSS source code.", details.size()));
        out.append("\n");
        out.append(details.toString());
    }
}
