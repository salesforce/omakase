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

package com.salesforce.omakase.test.sample.custom.selector;

import com.google.common.base.Suppliers;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.parser.refiner.RefinerRegistry;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * This is the actual plugin that gets registered with the parser.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PlaceholderSelectorPlugin implements SyntaxPlugin, DependentPlugin {
    private static final PlaceholderTokenFactory TOKEN_FACTORY = new PlaceholderTokenFactory();

    @Override
    public void dependencies(PluginRegistry registry) {
        // this ensures our token factory is registered, but allows for another plugin to have
        // registered an instance of the same token factory class already
        registry.requireTokenFactory(PlaceholderTokenFactory.class, Suppliers.ofInstance(TOKEN_FACTORY));
    }

    @Override
    public void registerRefiners(RefinerRegistry registry) {
        // register our refiner
        registry.register(new PlaceholderSelectorRefiner());
    }
}
