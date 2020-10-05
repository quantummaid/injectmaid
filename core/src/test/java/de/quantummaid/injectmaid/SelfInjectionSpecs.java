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

import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.domain.WithDependencyToInjectMaid;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class SelfInjectionSpecs {

    @Test
    public void injectMaidCanNotInjectItselfWithoutConfiguringIt() {
        final InjectMaid injectMaid = anInjectMaid()
                .build();

        final Exception exception = catchException(() -> injectMaid.getInstance(InjectMaid.class));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Cannot instantiate unregistered type 'de.quantummaid.injectmaid.InjectMaid'"));
    }

    @Test
    public void injectMaidCanInjectItself() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(WithDependencyToInjectMaid.class)
                .build();

        final WithDependencyToInjectMaid instance = injectMaid.getInstance(WithDependencyToInjectMaid.class);
        final boolean sameReference = instance.injectMaid == injectMaid;
        assertThat(sameReference, is(true));
    }

    @Test
    public void injectMaidCanInjectItselfInScope() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(String.class, builder -> builder.withType(WithDependencyToInjectMaid.class))
                .build();

        final Injector scopedInjectMaid = injectMaid.enterScope("test");

        final WithDependencyToInjectMaid instance = scopedInjectMaid.getInstance(WithDependencyToInjectMaid.class);
        final boolean sameReference = instance.injectMaid == scopedInjectMaid;
        assertThat(sameReference, is(true));
    }

    @Test
    public void injectMaidSelfInjectionCanBeOverwritten() {
        final InjectMaid otherInjectMaid = anInjectMaid().build();

        final InjectMaid injectMaid = anInjectMaid()
                .withType(WithDependencyToInjectMaid.class)
                .withCustomType(InjectMaid.class, () -> otherInjectMaid)
                .build();

        final WithDependencyToInjectMaid instance = injectMaid.getInstance(WithDependencyToInjectMaid.class);
        final boolean sameReference = instance.injectMaid == otherInjectMaid;
        assertThat(sameReference, is(true));
    }
}
