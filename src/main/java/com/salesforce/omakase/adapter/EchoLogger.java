/**
 * ADD LICENSE
 */
package com.salesforce.omakase.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class EchoLogger implements Adapter {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    private int count;

    @Override
    public void beginRule() {
        logger.info("starting rule");

    }

    @Override
    public void endRule() {
        logger.info("rule complete: {} declarations", count);
        count = 0;
    }

    @Override
    public void selector(Selector selector) {
        logger.info("selector: {}", selector);

    }

    @Override
    public void declaration(Declaration declaration) {
        logger.info("declaration: {}", declaration);
        count++;

    }

}
