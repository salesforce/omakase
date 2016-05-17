/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.ast.atrule;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link GenericAtRuleBlock}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class GenericAtRuleBlockTest {
    private Statement statement;

    @Before
    public void setup() {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("test")));
        Declaration d = new Declaration(Property.DISPLAY, PropertyValue.of(KeywordValue.of("none")));
        rule.declarations().append(d);
        statement = rule;
    }

    @Test
    public void getStatement() {
        GenericAtRuleBlock block = new GenericAtRuleBlock();
        block.statements().append(statement);
        assertThat(block.statements()).containsExactly(statement);
    }

    @Test
    public void propagatesBroadcast() {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        QueryableBroadcaster qb = new QueryableBroadcaster();
        block.propagateBroadcast(qb);
        assertThat(qb.find(Statement.class).get()).isSameAs(statement);
    }

    @Test
    public void isWritableWhenHasStatements() {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        assertThat(block.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWithoutStatements() {
        GenericAtRuleBlock block = new GenericAtRuleBlock();
        assertThat(block.isWritable()).isFalse();
    }

    @Test
    public void writeVerbose() throws IOException {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        assertThat(StyleWriter.verbose().writeSingle(block)).isEqualTo(" {\n  .test {\n    display: none;\n  }\n}");
    }

    @Test
    public void writeInline() throws IOException {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        assertThat(StyleWriter.inline().writeSingle(block)).isEqualTo(" {\n  .test {display:none}\n}");
    }

    @Test
    public void writeCompressed() throws IOException {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        assertThat(StyleWriter.compressed().writeSingle(block)).isEqualTo("{.test{display:none}}");
    }

    @Test
    public void copy() {
        GenericAtRuleBlock block = new GenericAtRuleBlock(Lists.newArrayList(statement), null);
        GenericAtRuleBlock copy = block.copy();
        assertThat(copy.statements()).hasSameSizeAs(block.statements());
    }
}
