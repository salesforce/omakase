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

package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.salesforce.omakase.error.OmakaseException;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;
import com.salesforce.omakase.writer.WriterMode;

import java.io.IOException;
import java.util.List;

/**
 * Represents a CSS comment.
 * <p/>
 * By default, comments are not written out except for in {@link WriterMode#VERBOSE}.
 */

public final class Comment implements Writable {
    private static final String MSG = "CSS annotation comments have a maximum of three args, only one annotation per comment, " +
        "and annotations cannot be mixed with regular text comments -- in comment '%s'";

    private final String content;

    private boolean checked;
    private CssAnnotation annotation;

    /**
     * Creates a new {@link Comment} with the given content.
     *
     * @param content
     *     The content.
     */
    public Comment(String content) {
        this.content = content;
    }

    /**
     * Gets the content of the comment.
     *
     * @return The content.
     */
    public String content() {
        return content;
    }

    /**
     * Checks if this comment has a {@link CssAnnotation} with the given name.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     *
     * @param name
     *     Check for an annotation with this name.
     *
     * @return True if a {@link CssAnnotation} was found with the given name.
     */
    public boolean hasAnnotation(String name) {
        checkForAnnotation();
        return (annotation != null) && annotation.name().equals(name);
    }

    /**
     * Gets the {@link CssAnnotation} with the given name, if there is one.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     *
     * @param name
     *     Get the annotation with this name.
     *
     * @return The {@link CssAnnotation}, or {@link Optional#absent()} if not found.
     */
    public Optional<CssAnnotation> annotation(String name) {
        checkForAnnotation();
        return Optional.fromNullable(annotation != null && annotation.name().equals(name) ? annotation : null);
    }

    /**
     * Gets the {@link CssAnnotation} within this comment, if there is one.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     *
     * @return The {@link CssAnnotation}, or {@link Optional#absent()} if not found.
     */
    public Optional<CssAnnotation> annotation() {
        checkForAnnotation();
        return Optional.fromNullable(annotation);
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (writer.isVerbose()) {
            appendable.append("/*").append(content).append("*/");
        }
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }

    private void checkForAnnotation() {
        if (checked) return;
        checked = true;

        String trimmed = content.trim();
        if (trimmed.charAt(0) != '@') return;

        String name = null;
        List<String> args = null;
        for (String string : Splitter.on(' ').omitEmptyStrings().split(trimmed.substring(1))) {
            if (name == null) {
                name = string;
            } else {
                if (string.indexOf('@') > -1) throw new OmakaseException(String.format(MSG, content));
                if (args == null) args = Lists.newArrayList();
                args.add(string);
            }
        }

        if (name == null) return;
        if (args != null && args.size() > 5) throw new OmakaseException(String.format(MSG, content));
        annotation = new CssAnnotation(name, args);
    }
}
