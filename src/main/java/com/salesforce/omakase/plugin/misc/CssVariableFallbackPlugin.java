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

package com.salesforce.omakase.plugin.misc;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.CssAnnotation;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CssVariableFallbackPlugin adds fallback declarations to annotated CSS variable declarations.
 *
 * @author fboyle
 */
public final class CssVariableFallbackPlugin implements DependentPlugin {
    public static final String FALLBACK = "css-var-fallback";
    
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(DeclarationPlugin.class);
    }

    /**
     * Checks for a {@link CssAnnotation} indicating a CSS variable fallback was provided.
     */
    private boolean hasCssVariableFallback(Declaration declaration) {
        return declaration.hasAnnotation(FALLBACK);
    }

    /**
     * Add CSS variable fallback declaration.
     *
     * @param declaration
     *     Declaration needing fallback.
     */
    @Rework
    public void addFallback(Declaration declaration) {
        PropertyValue value = declaration.propertyValue();
        String rawValue;

        // if we have a property value then get its content
        // otherwise return since there's nothing to process
        if (declaration.rawPropertyValue().isPresent()) {
            rawValue = declaration.rawPropertyValue().get().content();
        } else {
            return;
        }

        // only do something if we have a properly annotated declaration
        if (hasCssVariableFallback(declaration)) {
            Optional<CssAnnotation> annotation = declaration.annotation(FALLBACK);
            ImmutableList<String> args = annotation.get().spaceSeparatedArgs();

            // skip if we have no fallback value
            if (args.size() > 0) {
                // if CSS variable value drill in to find the fallback value
                if (isCssVarClause(rawValue)) {
                    value = PropertyValue.of(KeywordValue.of(findFallbackRecursively(rawValue)));
                }

                // create a new declaration
                Declaration newDeclaration = new Declaration(PropertyName.of(args.get(0)), value);

                declaration.append(newDeclaration);
            }
        } else {
            // if CSS variable value drill in to find the fallback value
            if (isCssVarClause(rawValue)) {
                value = PropertyValue.of(KeywordValue.of(findFallbackRecursively(rawValue)));

                // create a new declaration
                Declaration newDeclaration = new Declaration(declaration.propertyName(), value);

                declaration.prepend(newDeclaration);
                // declaration.propertyValue(value);
            }
        }
    }

    /*
     * Check if a property value is a CSS variable clause such as `var(--css-var, 10px)` 
     *
     * input: var(--sds-g-radius-border, var(--sds-g-border, var(--pickles, 10%)))
     * output: true
     */
    private Boolean isCssVarClause(String value) {
        return value.startsWith("var(");
    }

    /*
     * Recursive function to drill into a complex property value/string 
     *
     * input: var(--sds-g-radius-border, var(--sds-g-border, var(--pickles, 10%)))
     * output: 10%
     */
    private String findFallbackRecursively(String value) {
        // Define the pattern
        String pattern = "\\((.+)\\){1}";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object
        Matcher m = r.matcher(value);

        if (m.find()) {
            String group = m.group(1); // first capture group
            
            if (group.indexOf(",") > -1) {
                String[] split = group.split(",", 2); // split in pairs

                // if second item can be further split, keep drilling in
                if (split[1].indexOf(",") > -1) {
                    return findFallbackRecursively(split[1]);
                } else {
                    return split[1].trim();
                }
            }
        }
        
        return value;
    }
}