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
        assertThat(StyleWriter.verbose().writeSingle(exp)).isEqualTo("(min-resolution: 300dpi)");
    }

    @Test
    public void writeVerboseNoTerms() throws IOException {
        MediaQueryExpression exp = new MediaQueryExpression("color");
        assertThat(StyleWriter.verbose().writeSingle(exp)).isEqualTo("(color)");
    }

    @Test
    public void writeCompressedTerms() throws IOException {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "px")));
        assertThat(StyleWriter.compressed().writeSingle(exp)).isEqualTo("(max-width:300px)");
    }

    @Test
    public void doesntPropagateBroadcastToTerms() {
        MediaQueryExpression exp = new MediaQueryExpression("max-width");
        exp.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(300, "px")));
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        exp.propagateBroadcast(broadcaster);
        assertThat(broadcaster.find(NumericalValue.class).isPresent()).isFalse();
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
