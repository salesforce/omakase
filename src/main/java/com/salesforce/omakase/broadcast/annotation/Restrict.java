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

package com.salesforce.omakase.broadcast.annotation;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Filter out units that don't match specified criteria.
 * <p/>
 * This should be used on subscription methods, along with either {@link Rework} or {@link Observe}.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Restrict {
    /**
     * Whether {@link Refinable} units <em>without</em> {@link RawSyntax} should be delivered.
     * <p/>
     * Raw syntax refers to unrefined source code specified from the parser. Dynamically created units (i.e., created manually
     * through java code) usually will not have raw syntax specified.
     * <p/>
     * You should set this as false if you only want to receive units with {@link RawSyntax} specified, or in other words to skip
     * over dynamically created units.
     *
     * @return Whether units without raw syntax should be delivered.
     *
     * @see RawSyntax
     */
    boolean dynamicUnits() default true;

    /**
     * Whether {@link Refinable} units <em>with</em> {@link RawSyntax} should be delivered.
     * <p/>
     * Raw syntax refers to unrefined source code specified from the parser. Dynamically created units (i.e., created manually
     * through java code) usually will not have raw syntax specified.
     * <p/>
     * You should set this as false if you only want to receive units that do not have {@link RawSyntax} specified, or in other
     * words only dynamically created units.
     *
     * @return Whether units with raw syntax should be delivered.
     */
    boolean rawUnits() default true;
}

