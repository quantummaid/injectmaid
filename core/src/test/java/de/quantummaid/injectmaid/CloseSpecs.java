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

import de.quantummaid.injectmaid.domain.closing.AutoclosableType;
import de.quantummaid.injectmaid.domain.closing.NonAutoclosableType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CloseSpecs {

    @Test
    public void autoclosablesAreNotClosedByDefault() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableType.class)
                .build();
        final AutoclosableType instance = injectMaid.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));
        injectMaid.close();
        assertThat(instance.closed, is(false));
    }

    @Test
    public void autoclosablesInScopesAreNotClosedByDefault() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withScope(String.class, builder -> builder.withType(AutoclosableType.class))
                .build();
        final Injector scopedInjector = injectMaid.enterScope("foo");

        final AutoclosableType instance = scopedInjector.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));

        scopedInjector.close();
        assertThat(instance.closed, is(false));

        injectMaid.close();
        assertThat(instance.closed, is(false));
    }

    @Test
    public void autoclosablesAreClosedWithLifecycleManagment() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableType.class)
                .withLifecycleManagement()
                .build();
        final AutoclosableType instance = injectMaid.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));
        injectMaid.close();
        assertThat(instance.closed, is(true));
    }

    @Test
    public void autoclosablesInScopeAreClosedWithLifecycleManagment() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withScope(String.class, builder -> builder.withType(AutoclosableType.class))
                .withLifecycleManagement()
                .build();

        final Injector scopedInjector = injectMaid.enterScope("foo");

        final AutoclosableType instance = scopedInjector.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));

        scopedInjector.close();
        assertThat(instance.closed, is(true));
    }

    @Test
    public void autoclosablesInScopeAreClosedWhenParentIsClosed() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withScope(String.class, builder -> builder.withType(AutoclosableType.class))
                .withLifecycleManagement()
                .build();

        final Injector scopedInjector = injectMaid.enterScope("foo");

        final AutoclosableType instance = scopedInjector.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));

        injectMaid.close();
        assertThat(instance.closed, is(true));
    }

    @Test
    public void customClosingsCanBeRegistered() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(NonAutoclosableType.class)
                .closingInstancesOfType(NonAutoclosableType.class, NonAutoclosableType::close)
                .build();
        final NonAutoclosableType instance = injectMaid.getInstance(NonAutoclosableType.class);
        assertThat(instance.closed, is(false));
        injectMaid.close();
        assertThat(instance.closed, is(true));
    }
}
