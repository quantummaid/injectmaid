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

import de.quantummaid.injectmaid.api.SingletonType;
import de.quantummaid.injectmaid.domain.NumberedType;
import de.quantummaid.injectmaid.domain.TwoNumberedTypes;
import de.quantummaid.injectmaid.failing.FailingInConstructorType;
import de.quantummaid.injectmaid.failing.FailingInFactoryType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.api.ReusePolicy.*;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class SingletonSpecs {

    @Test
    public void injectMaidSupportsLazySingletons() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(TwoNumberedTypes.class)
                .withType(NumberedType.class, LAZY_SINGLETON)
                .build();
        assertThat(NumberedType.counter, is(0));
        final TwoNumberedTypes instance = injectMaid.getInstance(TwoNumberedTypes.class);
        assertThat(instance, notNullValue());
        assertThat(instance.numberedTypeA.instanceNumber(), is(0));
        assertThat(instance.numberedTypeB.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(1));
    }

    @Test
    public void injectMaidSupportsEagerSingletons() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(TwoNumberedTypes.class)
                .withType(NumberedType.class, EAGER_SINGLETON)
                .build();
        assertThat(NumberedType.counter, is(1));
        final TwoNumberedTypes instance = injectMaid.getInstance(TwoNumberedTypes.class);
        assertThat(instance, notNullValue());
        assertThat(instance.numberedTypeA.instanceNumber(), is(0));
        assertThat(instance.numberedTypeB.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(1));
    }

    @Test
    public void injectMaidSupportsEagerSingletonsByDefaultWhenConfigured() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(TwoNumberedTypes.class)
                .withType(NumberedType.class, DEFAULT_SINGLETON)
                .usingDefaultSingletonType(SingletonType.EAGER)
                .build();
        assertThat(NumberedType.counter, is(1));
        final TwoNumberedTypes instance = injectMaid.getInstance(TwoNumberedTypes.class);
        assertThat(instance, notNullValue());
        assertThat(instance.numberedTypeA.instanceNumber(), is(0));
        assertThat(instance.numberedTypeB.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(1));
    }

    @Test
    public void allSingletonsCanBeInitializedOnDemand() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(TwoNumberedTypes.class)
                .withType(NumberedType.class, LAZY_SINGLETON)
                .build();
        assertThat(NumberedType.counter, is(0));
        injectMaid.initializeAllSingletons();
        assertThat(NumberedType.counter, is(1));
        final TwoNumberedTypes instance = injectMaid.getInstance(TwoNumberedTypes.class);
        assertThat(instance, notNullValue());
        assertThat(instance.numberedTypeA.instanceNumber(), is(0));
        assertThat(instance.numberedTypeB.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(1));
    }

    @Test
    public void exceptionInConstructorDuringEagerInitializationAreThrown() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withType(FailingInConstructorType.class, EAGER_SINGLETON)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Exception during instantiation of 'FailingInConstructorType' using constructor " +
                "'public de.quantummaid.injectmaid.failing.FailingInConstructorType()'"));
        assertThat(exception.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void exceptionInFactoryDuringEagerInitializationAreThrown() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withType(FailingInFactoryType.class, EAGER_SINGLETON)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Exception during instantiation of " +
                "'FailingInFactoryType' using static method" +
                " ''FailingInFactoryType failingInFactoryType()' [public static de.quantummaid.injectmaid.failing.FailingInFactoryType " +
                "de.quantummaid.injectmaid.failing.FailingInFactoryType.failingInFactoryType()]'"));
        assertThat(exception.getCause(), instanceOf(IllegalArgumentException.class));
    }
}
