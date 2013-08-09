/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class EchoLogger implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    @Subscribe
    public void declaration(Declaration declaration) {
        logger.info(declaration.toString());
    }

    @Subscribe
    public void selectorGroup(SelectorGroup selectorGroup) {
        logger.info(selectorGroup.toString());
    }
}
