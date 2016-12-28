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

package com.salesforce.omakase.plugin;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.plugin.syntax.MediaPlugin;
import com.salesforce.omakase.plugin.syntax.SelectorPlugin;

import java.util.function.Supplier;

/**
 * A {@link Plugin} that has dependencies on other {@link Plugin}s.
 *
 * @author nmcwilliams
 */
public interface DependentPlugin extends Plugin {
    /**
     * Registers plugin dependencies.
     * <p>
     * Any plugins you add to the registry in this method will be ordered before this plugin itself.
     * <p>
     * Keep in mind that only one instance of a plugin can be added in a single parsing operation. You can use {@link
     * PluginRegistry#require(Class)}, {@link PluginRegistry#require(Class, Supplier)} and {@link
     * PluginRegistry#retrieve(Class)} to assist in scenarios where a plugin instance may have already been added.
     * <p>
     * Dependencies to include can range from refinement dependencies such as {@link SelectorPlugin} and {@link MediaPlugin} to
     * other custom plugins.
     *
     * @param registry
     *     The {@link PluginRegistry} instance.
     *
     * @see PluginRegistry#require(Class)
     * @see PluginRegistry#require(Class, Supplier)
     * @see PluginRegistry#retrieve(Class)
     */
    void dependencies(PluginRegistry registry);
}
