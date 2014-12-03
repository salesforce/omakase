/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.custom.declaration;

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
