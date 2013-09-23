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

import com.salesforce.omakase.broadcast.BroadcastRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description of a {@link Subscribable} type.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Description {
    /**
     * A description of the item receiving this annotation.
     *
     * @return The description.
     */
    String value() default "(no description)";

    /**
     * Indicates the conditions for this type of object to be broadcasted.
     *
     * @return The conditions for this type of object to be broadcasted.
     */
    BroadcastRequirement broadcasted() default BroadcastRequirement.AUTOMATIC;
}
