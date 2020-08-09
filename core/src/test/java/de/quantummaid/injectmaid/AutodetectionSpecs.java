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

import de.quantummaid.injectmaid.domain.InterfaceWithFactoryType;
import de.quantummaid.injectmaid.domain.OneConstructorAndManyFactoriesType;
import de.quantummaid.injectmaid.domain.StaticFactoryType;
import de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class AutodetectionSpecs {

    @Test
    public void injectMaidCanInstantiateAClassWithAZeroArgumentsConstructors() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(ZeroArgumentsConstructorType.class)
                .build();
        final ZeroArgumentsConstructorType instance = injectMaid.getInstance(ZeroArgumentsConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidCanDetectStaticFactoryMethod() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(StaticFactoryType.class)
                .build();
        final StaticFactoryType instance = injectMaid.getInstance(StaticFactoryType.class);
        assertThat(instance, notNullValue());
        assertThat(instance.checkString(), is("one argument static factory type"));
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void constructorIsPreferredOverFactoryMethods() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(OneConstructorAndManyFactoriesType.class)
                .build();
        final OneConstructorAndManyFactoriesType instance = injectMaid.getInstance(OneConstructorAndManyFactoriesType.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("from constructor"));
    }

    @Test
    public void anInterfaceWithAFactoryMethodCanBeAutodetected() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(InterfaceWithFactoryType.class)
                .build();
        final InterfaceWithFactoryType instance = injectMaid.getInstance(InterfaceWithFactoryType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(InterfaceWithFactoryType.class));
    }
}
