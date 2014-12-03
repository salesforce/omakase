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

package com.salesforce.omakase.parser.refiner;

/**
 * The potential return value for {@link Refiner}s.
 *
 * @author nmcwilliams
 */
public enum Refinement {
    /**
     * Indicates that refinement was fully completed, and no subsequent refiners should be allowed to attempt further refinement.
     */
    FULL,
    /**
     * Indicates that some refinement has occurred, but subsequent refiners should still be allowed to attempt further refinement.
     * For AST objects that are commonly subject to disjoint refinement, it's usually a good idea that each refiner checks that
     * the relevant refinement has not already occurred first.
     */
    PARTIAL,
    /** Indicates that no refinement has occurred, and subsequent refiners should be allowed to attempt refinement. */
    NONE
}
