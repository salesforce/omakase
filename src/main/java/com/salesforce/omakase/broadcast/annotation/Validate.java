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

package com.salesforce.omakase.broadcast.annotation;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation for methods that validate and report errors to an {@link ErrorManager}.
 * <p/>
 * The first parameter for methods with this annotation should be one of the {@link Syntax} types. The second parameter should be
 * of type {@link ErrorManager}. This error manager should be used to report any problems during validation.
 * <p/>
 * Validation always occurs <em>after</em> {@link Observe} and {@link Rework} subscription methods.. This is based on the
 * assumption that validation should happen last, and it prevents validating the same thing over and over as a result of rework or
 * other changes (and also ensures that new or changed units as a result of rework are validated as well).
 * <p/>
 * <b>Important:</b> Do not perform any modifications in a {@link Validate} method! The only thing the method should do is check
 * for certain conditions and report an error to the {@link ErrorManager} if applicable. All rework must be done in a method
 * annotation with {@link Rework}, which is mutually exclusive with this annotation.
 * <p/>
 * If you have a situation where both rework and validation are required, split it into separate methods, one with {@link Rework}
 * and one with {@link Validate}. Of course as mentioned, the assumption is that all validation should be done last, not before,
 * any rework is done. If this is not true, perhaps try rethinking about the relationship between the rework and validation and
 * refactor accordingly.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Validate {
}
