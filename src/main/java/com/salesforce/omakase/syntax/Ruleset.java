package com.salesforce.omakase.syntax;

import java.util.List;

public interface Ruleset extends Syntax {
	SelectorGroup selectorGroup();

	List<Declaration> declarations();
}
