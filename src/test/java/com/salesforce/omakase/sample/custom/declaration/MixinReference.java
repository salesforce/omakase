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

package com.salesforce.omakase.sample.custom.declaration;

import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The custom AST object representing a reference to a mixin (from within a rule).
 * <p/>
 * Specifically, this is a custom {@link Term} that gets placed within the declaration, as opposed to being a custom declaration
 * itself.
 *
 * @author nmcwilliams
 */
@Subscribable
@SuppressWarnings("JavaDoc")
public class MixinReference extends AbstractTerm {
    private final Mixin mixin;
    private final Map<String, String> params;

    public MixinReference(Mixin mixin, Map<String, String> params) {
        this.mixin = checkNotNull(mixin, "mixin cannot be null");
        this.params = ImmutableMap.copyOf(params);
    }

    public Mixin mixin() {
        return mixin;
    }

    public Map<String, String> params() {
        return params;
    }

    @Override
    public String textualValue() {
        return "";
    }

    @Override
    public MixinReference copy() {
        return null;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        throw new UnsupportedOperationException("write not supported");
    }
}
