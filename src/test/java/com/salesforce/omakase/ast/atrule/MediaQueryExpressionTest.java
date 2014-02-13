/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.ast.atrule;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaQueryExpression}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryExpressionTest {
    @Test
    public void getFeature() {
        MediaQueryExpression exp = new MediaQueryExpression("min-width");
        assertThat(exp.feature()).isEqualTo("min-width");
    }

    @Test
    public void setFeature() {
        MediaQueryExpression exp = new MediaQueryExpression(1, 1, "min-width");
        exp.feature("max-width");
        assertThat(exp.feature()).isEqualTo("max-width");
    }

    @Test
    public void setTerms() {
        MediaQueryExpression exp = new MediaQueryExpression("min-width");
        NumericalValue t1 = NumericalValue.of(1, "px");
        Operator o = new Operator(OperatorType.SPACE);
        NumericalValue t2 = NumericalValue.of(1, "px");
        exp.terms(Lists.<PropertyValueMember>newArrayList(t1, o, t2));
        assertThat(exp.terms()).containsExactly(t1, o, t2);
    }

    @Test
    public void getTermsWhenNonePresent() {
        MediaQueryExpression exp = new MediaQueryExpression("min-width");
        assertThat(exp.terms()).isEmpty();
    }

    @Test
    public void writeVerboseTerms() throws IOException {
        MediaQueryExpression exp = new MediaQueryExpression("min-resolution");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "dpi")));
        assertThat(StyleWriter.verbose().writeSnippet(exp)).isEqualTo("(min-resolution: 300dpi)");
    }

    @Test
    public void writeVerboseNoTerms() throws IOException {
        MediaQueryExpression exp = new MediaQueryExpression("color");
        assertThat(StyleWriter.verbose().writeSnippet(exp)).isEqualTo("(color)");
    }

    @Test
    public void writeCompressedTerms() throws IOException {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "px")));
        assertThat(StyleWriter.compressed().writeSnippet(exp)).isEqualTo("(max-width:300px)");
    }

    @Test
    public void propagatesBroadcastToTerms() {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "px")));
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        exp.propagateBroadcast(broadcaster);
        assertThat(broadcaster.find(NumericalValue.class).isPresent()).isTrue();
    }

    @Test
    public void copyNoTerms() {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        MediaQueryExpression copy = exp.copy();
        assertThat(copy.feature()).isEqualTo("max-width");
    }

    @Test
    public void copyWithTerms() {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "px")));
        MediaQueryExpression copy = exp.copy();
        assertThat(copy.terms()).hasSize(1);
    }
}
