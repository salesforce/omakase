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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.util.As;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;

/**
 * A plugin that stores the parsed {@link Stylesheet} object.
 * <p/>
 * Use this plugin when you want to get a reference to the top-level {@link Stylesheet} object outside of your own custom plugin.
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
