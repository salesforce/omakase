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
import com.salesforce.omakase.ast.collection.Groupable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to subscribe to {@link Syntax} objects when the method is expected to change or modify the object or CSS
 * source.
 * <p/>
 * Examples of rework include adding cache-busters to urls, changing class names, flipping directions for RTL support, etc... It
 * basically represents changing the content.
 * <p/>
 * The one an only parameter for methods with this annotation should be one of the {@link Syntax} types.
 * <p/>
 * If the method does not intend to change the content or object, use {@link Observe} instead.
 * <p/>
 * Inside of a rework method, you can remove a unit from the syntax tree by calling {@link Groupable#destroy()}. Once a unit is
 * destroyed it is no longer broadcasted to any subsequent plugins, including validation. Destroyed units cannot be added to the
 * tree again, however they can still be copied. If you are storing the units in a cache then you will probably want to check
 * {@link Groupable#isDestroyed()} upon later access as the unit may have been destroyed by another plugin.
 * <p/>
 * See SimpleReworkTest.java for same rework method implementations.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rework {
}
