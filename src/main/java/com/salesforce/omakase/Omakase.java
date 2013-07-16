package com.salesforce.omakase;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.SelectorGroup;

public final class Omakase {
	private final EventListener[] listeners;

	public Omakase(EventListener... listeners) {
		this.listeners = listeners;
	}

	public void parse(CharSequence input) {
		selectorGroup(".THIS #one");
		declaration("margin", "2px 3px");
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

	public static Omakase using(EventListener... listeners) {
		return new Omakase(listeners);
	}
}
