/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.SelectorGroup;
import com.salesforce.omakase.syntax.impl.RawDeclaration;
import com.salesforce.omakase.syntax.impl.RawSelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final EventListener[] listeners;

    /**
     * @param listeners
     *            TODO
     */
    public Omakase(EventListener... listeners) {
        this.listeners = listeners;
    }

    /**
     * TODO Description
     * 
     * @param input
     *            TODO
     */
    public void parse(CharSequence input) {
        selectorGroup(new RawSelectorGroup(0, 0, ".THIS #one"));
        declaration(new RawDeclaration(0, 0, "margin", "3px 4px 3px"));
    }

    private void declaration(Declaration declaration) {
        for (EventListener listener : listeners) {
            listener.declaration(declaration);
        }
    }

    private void selectorGroup(SelectorGroup selectorGroup) {
        for (EventListener listener : listeners) {
            listener.selectorGroup(selectorGroup);
        }
    }

    /**
     * TODO Description
     * 
     * @param listeners
     *            TODO
     * @return TODO
     */
    public static Omakase using(EventListener... listeners) {
        return new Omakase(listeners);
    }
}
