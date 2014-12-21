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

import com.google.common.base.Suppliers;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.refiner.RefinerRegistry;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * This is the actual plugin that gets registered with the parser.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MixinPlugin implements SyntaxPlugin, DependentPlugin {
    private static final MixinTokenFactory TOKEN_FACTORY = new MixinTokenFactory();

    @Override
    public void dependencies(PluginRegistry registry) {
        // we need our token factory registered in order to work
        registry.requireTokenFactory(MixinTokenFactory.class, Suppliers.ofInstance(TOKEN_FACTORY));
    }

    @Override
    public void registerRefiners(RefinerRegistry registry) {
        // using registerMulti as this refiner handles multiple types of AST objects
        registry.registerMulti(new MixinRefiner());
    }

    /**
     * This handles taking the {@link MixinReference} within a rule and replacing it copies of the mixin's template declarations.
     * Ideally this could have been in the refiner?... but prepending new declarations is not allowed at that point in time...
     */
    @Rework
    public void resolve(MixinReference mixinReference) {
        Declaration placeholder = mixinReference.declaration();
        Mixin mixin = mixinReference.mixin();

        for (Declaration declaration : mixin.declarations()) {
            RawSyntax rawName = declaration.rawPropertyName().get();
            RawSyntax rawProp = declaration.rawPropertyValue().get();

            // replace var refs
            if (rawProp.content().startsWith("$")) {
                String resolved = mixinReference.params().get(rawProp.content().substring(1));
                if (resolved == null) throw new ParserException(declaration, "unknown mixin param ref");
                rawProp = new RawSyntax(rawProp.line(), rawProp.column(), resolved);
            }

            Declaration cloned = new Declaration(rawName, rawProp, null);
            placeholder.prepend(cloned);
        }

        // not strictly necessary as it won't write out anyway, but we're done with it
        placeholder.destroy();
    }
}
