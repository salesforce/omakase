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
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Plugin} that wishes to be notified when all processing (rework and validation) is completed.
 * <p>
 * Please be aware that as this is post processing, and changes or modifications made to AST objects will not be automatically
 * provided to plugin subscription methods. Post processing is generally best for read-only operations.
 * <p>
 * If you are looking to make modifications after the last rule has been parsed, consider creating a subscription method to {@link
 * Stylesheet}, which should be the very last syntax unit to be broadcasted (except any broadcasts resulted from changes you make,
 * which means multiple plugins doing this may not work well together).
 *
 * @author nmcwilliams
 */
public interface PostProcessingPlugin extends Plugin {
    /**
     * This method will be called after all processing has completed (rework and validation).
     * <p>
     * This could be used when the {@link Plugin} must defer it's processing until it is certain that all {@link Selector}s and
     * {@link Declaration}s within the source are processed.
     * <p>
     * The order in which this will be invoked (between plugins) is the same order that the {@link Plugin} was registered.
     *
     * @param registry
     *     The {@link PluginRegistry} instance.
     */
    void postProcess(PluginRegistry registry);
}
