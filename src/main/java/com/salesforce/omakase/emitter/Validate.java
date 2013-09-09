/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation for methods that validate and report errors to an {@link ErrorManager}.
 * <p/>
 * The first parameter for methods with this annotation should be one of the {@link Syntax} types. The second parameter
 * should be of type {@link ErrorManager}. This error manager should be used to report any problems during validation.
 * <p/>
 * Validation always occurs <em>after</em> {@link PreProcess}, {@link Observe} and {@link Rework} subscription methods.. This
 * is based on the assumption that validation should happen last, and it prevents validating the same thing over and over as
 * a result of rework or other changes (and also ensures that new or changed units as a result of rework are validated as
 * well).
 * <p/>
 * <b>Important:</b> Do not perform any modifications in a {@link Validate} method! The only thing the method should do is
 * check for certain conditions and report an error to the {@link ErrorManager} if applicable. All rework must be done in a
 * method annotation with {@link Rework}, which is mutually exclusive with this annotation.
 * <p/>
 * If you have a situation where both rework and validation are required, split it into separate methods, one with {@link
 * Rework} and one with {@link Validate}. Of course as mentioned, the assumption is that all validation should be done last,
 * not before, any rework is done. If this is not true, perhaps try rethinking about the relationship between the rework and
 * validation and refactor accordingly.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Validate {
}
