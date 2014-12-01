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

package com.salesforce.omakase.test.sample;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;

import java.io.IOException;

/**
 * Sample class showing how you can subscribe to and work with your custom AST objects.
 * <p/>
 * This just keeps a count of the number of custom functions that were parsed in the CSS source code.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "UnusedParameters"})
public class SampleCustomFunctionCounter implements Plugin {
    private final Multiset<String> details = HashMultiset.create();

    public int count() {
        return details.size();
    }

    public void summarize(Appendable out) throws IOException {
        out.append(String.format("\n\n%s total custom functions were found in the CSS source code.", details.size()));
        out.append("\n");
        out.append(details.toString());
    }

    @Observe
    public void observe(SampleCustomFunction function) {
        details.add(function.arg());
    }
}
