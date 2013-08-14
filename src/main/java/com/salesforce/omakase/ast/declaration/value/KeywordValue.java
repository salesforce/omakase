/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class KeywordValue implements Term {
    private String keyword;

    /**
     * TODO
     * 
     * @param keyword
     *            TODO
     */
    public KeywordValue(String keyword) {
        this.keyword = keyword;
    }

    /**
     * TODO Description
     * 
     * @param keyword
     *            TODO
     * @return TODO
     */
    public KeywordValue keyword(String keyword) {
        this.keyword = checkNotNull(keyword, "keyword cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String keyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("keyword", keyword)
            .toString();
    }

}
