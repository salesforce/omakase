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

package com.salesforce.omakase.broadcast.annotation;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to preprocess a particular {@link Syntax} unit.
 * <p/>
 * Methods with this annotation will be called before any {@link Rework} or {@link Validate} methods are called.
 * <p/>
 * The {@link PreProcess} annotation is mainly used to provide necessary metadata or setup that subsequent {@link Rework}
 * operations will rely on. For example, the {@link SyntaxTree} uses it to link related objects together, and {@link AutoRefiner}
 * uses it to automatically refine certain detailed syntax units, ensuring that they are received by {@link Rework} plugins.
 * <p/>
 * In most cases this is <b>not</b> what you want to use. Prefer {@link Rework} for operations that change content, {@link
 * Observe} for operations that do not change content, and {@link Validate} for validation that can result in throwing an error
 * (via an {@link ErrorManager}).
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreProcess {
}
