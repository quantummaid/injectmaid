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

import de.quantummaid.injectmaid.domain.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class InjectMaidSpecs {

    @Test
    public void injectMaidCanResolveConstructorDependencies() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(OneArgumentConstructorType.class)
                .build();
        final OneArgumentConstructorType instance = injectMaid.getInstance(OneArgumentConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance.checkString(), is("one argument constructor type"));
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidCreatesNewInstancesByDefault() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(TwoNumberedTypes.class)
                .build();
        final TwoNumberedTypes instance = injectMaid.getInstance(TwoNumberedTypes.class);
        assertThat(instance, notNullValue());
        assertThat(instance.numberedTypeA.instanceNumber(), is(0));
        assertThat(instance.numberedTypeB.instanceNumber(), is(1));
        assertThat(NumberedType.counter, is(2));
    }

    @Test
    public void injectMaidCanSayWhetherATypeCanBeInstantiated() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(ZeroArgumentsConstructorType.class)
                .build();
        assertThat(injectMaid.canInstantiate(ZeroArgumentsConstructorType.class), is(true));
        assertThat(injectMaid.canInstantiate(String.class), is(false));
    }
}
