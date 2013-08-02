/**
 * ADD LICENSE
 */
package com.salesforce.omakase.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * Simply logs the occurrence of each event.
 * 
 * @author nmcwilliams
 */
public class EchoLogger implements Consumer {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    @Override
    public void selectorGroup(SelectorGroup selectorGroup) {
        logger.info("selectorGroup: {}", selectorGroup);
    }

    @Override
    public void declaration(Declaration declaration) {
        logger.info("declaration: {}", declaration);
    }
}
