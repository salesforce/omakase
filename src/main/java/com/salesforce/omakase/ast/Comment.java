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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.util.As;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a CSS comment.
 * <p>
 * By default, comments are not written out. You can control this behavior with {@link StyleWriter#writeAllComments(boolean)}.
 */

public final class Comment implements Writable {
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
     * Creates a new {@link Comment} with the given {@link CssAnnotation} as the content.
     *
     * @param annotation
     *     The annotation representing the comment.
     */
    public Comment(CssAnnotation annotation) {
        this.annotation = checkNotNull(annotation, "annotation cannot be null");
        this.content = annotation.toString();
        this.checked = true;
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
     * <p>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]", for
     * example "@noparse", "@browser ie7", etc...
     * <p>
     * Only one annotation per comment block is allowed.
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
     * Checks if this comment has a {@link CssAnnotation} that equals the given one.
     * <p>
     * This will be true if the both the name and the args match exactly. See {@link CssAnnotation#equals(Object)}. If you only
     * care about matching the comment name, see {@link #hasAnnotation(String)} instead.
     *
     * @param annotation
     *     Check for an annotation that equals this one.
     *
     * @return True if a {@link Comment} was found that matches the given one.
     */
    public boolean hasAnnotation(CssAnnotation annotation) {
        checkForAnnotation();
        return (this.annotation != null) && this.annotation.equals(annotation);
    }

    /**
     * Gets the {@link CssAnnotation} with the given name, if there is one.
     * <p>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]", for
     * example "@noparse", "@browser ie7", etc...
     * <p>
     * Only one annotation per comment block is allowed.
     *
     * @param name
     *     Get the annotation with this name.
     *
     * @return The {@link CssAnnotation}, or an empty {@link Optional} if not found.
     */
    public Optional<CssAnnotation> annotation(String name) {
        checkForAnnotation();
        return Optional.ofNullable(annotation != null && annotation.name().equals(name) ? annotation : null);
    }

    /**
     * Gets the {@link CssAnnotation} within this comment, if there is one.
     * <p>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]", for
     * example "@noparse", "@browser ie7", etc...
     * <p>
     * Only one annotation per comment block is allowed.
     *
     * @return The {@link CssAnnotation}, or an empty {@link Optional} if not found.
     */
    public Optional<CssAnnotation> annotation() {
        checkForAnnotation();
        return Optional.ofNullable(annotation);
    }

    /**
     * Returns whether the comment starts with ! (this is strict, no whitespace allowed between the opening comment and the
     * bang).
     *
     * @return True if the comment starts with '!'.
     */
    public boolean startsWithBang() {
        return content.charAt(0) == '!';
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append("/*").append(content).append("*/");
        // if content contains new line then add a line break after it
        appendable.newlineIf(writer.isVerbose() && content.contains("\n"));
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }

    /**
     * Checks the content for a {@link CssAnnotation} (result of this check is cached and reused on subsequent checks).
     */
    private void checkForAnnotation() {
        if (checked) return;
        checked = true;

        String toCheck = content;
        if (startsWithBang()) {
            toCheck = toCheck.substring(1);
        }
        toCheck = toCheck.trim();

        if (toCheck.length() > 2 && toCheck.charAt(0) == '@') {
            String[] split = toCheck.substring(1).split(" ", 2);
            String name = split[0];
            String args = split.length > 1 ? split[1] : null;
            annotation = new CssAnnotation(name, args);
        }
    }
}
