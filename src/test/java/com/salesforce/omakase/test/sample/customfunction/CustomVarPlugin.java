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

package com.salesforce.omakase.test.sample.customfunction;

import com.salesforce.omakase.parser.refiner.FunctionRefiner;
import com.salesforce.omakase.parser.refiner.RefinerRegistry;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.Map;

/**
 * This is the actual plugin that gets registered with the parser.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CustomVarPlugin implements SyntaxPlugin {
    private final FunctionRefiner refiner;

    public CustomVarPlugin(CustomVarRefinerz.Mode mode, Map<String, String> vars) {
        this.refiner = new CustomVarRefinerz(mode, vars);
    }

    @Override
    public void registerRefiners(RefinerRegistry registry) {
        registry.register(refiner);
    }
}
