/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.injectmaid;

import de.quantummaid.injectmaid.domain.Request;
import de.quantummaid.injectmaid.failing.CircularTypeA;
import de.quantummaid.injectmaid.failing.CircularTypeB;
import de.quantummaid.injectmaid.failing.SelfReferencingType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CircularDependencySpecs {

    @Test
    public void selfReferencingTypeCannotBeAutoregistered() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withType(SelfReferencingType.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Illegal circular dependency in scope '/' detected: SelfReferencingType -> SelfReferencingType"));
    }

    @Test
    public void circularDependencyGetsDetected() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withType(CircularTypeA.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), anyOf(
                is("Illegal circular dependency in scope '/' detected: CircularTypeA -> CircularTypeB -> CircularTypeA"),
                is("Illegal circular dependency in scope '/' detected: CircularTypeB -> CircularTypeA -> CircularTypeB")
        ));
    }

    @Test
    public void circularDependencyCanBeBrokenByManuallyRegisteringAType() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(CircularTypeA.class)
                .withCustomType(CircularTypeB.class, () -> new CircularTypeB(null))
                .build();

        final CircularTypeA circularTypeA = injectMaid.getInstance(CircularTypeA.class);
        assertThat(circularTypeA, notNullValue());
        assertThat(circularTypeA, instanceOf(CircularTypeA.class));
        assertThat(circularTypeA.circularTypeB, notNullValue());
        assertThat(circularTypeA.circularTypeB, instanceOf(CircularTypeB.class));

        final CircularTypeB circularTypeB = injectMaid.getInstance(CircularTypeB.class);
        assertThat(circularTypeB, notNullValue());
        assertThat(circularTypeB, instanceOf(CircularTypeB.class));
        assertThat(circularTypeB.circularTypeA, nullValue());
    }

    @Test
    public void circularDependenciesInScopesAreDetectedCorrectly() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withScope(Request.class, builder -> builder.withType(CircularTypeA.class))
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), anyOf(
                is("Illegal circular dependency in scope '/Request' detected: CircularTypeA -> CircularTypeB -> CircularTypeA"),
                is("Illegal circular dependency in scope '/Request' detected: CircularTypeB -> CircularTypeA -> CircularTypeB")
        ));
    }
}
