package com.salesforce.omakase;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.SelectorGroup;

public interface EventListener {
	public void selectorGroup(SelectorGroup selectorGroup);

	public void declaration(Declaration declaration);
}
