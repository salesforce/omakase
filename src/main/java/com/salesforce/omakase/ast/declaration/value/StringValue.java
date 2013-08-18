/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "individual string value", broadcasted = REFINED_DECLARATION)
public class StringValue extends AbstractSyntax implements Term {

    /**
     * TODO
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public StringValue(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

}
