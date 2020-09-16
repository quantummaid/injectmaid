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
import de.quantummaid.injectmaid.domain.closing.AutoclosableWithDependency;
import de.quantummaid.injectmaid.domain.closing.CountingClosable;
import de.quantummaid.injectmaid.domain.closing.NonAutoclosableType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CloseSpecs {

    @Test
    public void autocloseablesAreNotClosedByDefault() {
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
                .withScope(String.class, builder -> builder.withScope(Integer.class,
                        innerBuilder -> builder.withType(AutoclosableType.class)))
                .withLifecycleManagement()
                .build();
        final Injector outerScopedInjector = injectMaid.enterScope("foo");
        final Injector innerScopedInjector = outerScopedInjector.enterScope(1);

        final AutoclosableType instance = innerScopedInjector.getInstance(AutoclosableType.class);
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

    @Test
    public void dependenciesAreClosedFirst() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableWithDependency.class)
                .withLifecycleManagement()
                .build();
        final AutoclosableWithDependency instance = injectMaid.getInstance(AutoclosableWithDependency.class);
        injectMaid.close();
        assertThat(instance.closed, is(true));
        assertThat(instance.dependencyHasBeenClosedFirst, is(true));
    }

    @Test
    public void exceptionsDuringCloseArePropagated() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableType.class)
                .withLifecycleManagement()
                .build();
        final AutoclosableType instance = injectMaid.getInstance(AutoclosableType.class);
        assertThat(instance.closed, is(false));
        instance.fail = true;

        final Exception exception = catchException(injectMaid::close);
        assertThat(instance.closed, is(true));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("exception(s) during close:\n" +
                "AutoclosableType(closed=true, fail=true): failed to close"));
    }

    @Test
    public void exceptionsDoNotHinderFurtherClosings() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableWithDependency.class)
                .withLifecycleManagement()
                .build();
        final AutoclosableWithDependency instance = injectMaid.getInstance(AutoclosableWithDependency.class);
        instance.autoclosableType.fail = true;
        final Exception exception = catchException(injectMaid::close);
        assertThat(exception.getMessage(), is("exception(s) during close:\n" +
                "AutoclosableType(closed=true, fail=true): failed to close"));
        assertThat(exception.getSuppressed().length, is(1));
        assertThat(exception.getSuppressed()[0].getMessage(), is("failed to close"));
        assertThat(instance.closed, is(true));
        assertThat(instance.dependencyHasBeenClosedFirst, is(true));
    }

    @Test
    public void exceptionsOfClosingsAreAggregated() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(AutoclosableType.class)
                .withType(NonAutoclosableType.class)
                .closingInstancesOfType(NonAutoclosableType.class, NonAutoclosableType::close)
                .build();

        final AutoclosableType instance1 = injectMaid.getInstance(AutoclosableType.class);
        instance1.fail = true;

        final NonAutoclosableType instance2 = injectMaid.getInstance(NonAutoclosableType.class);
        instance2.fail = true;

        final Exception exception = catchException(injectMaid::close);
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("exception(s) during close:\n" +
                "AutoclosableType(closed=true, fail=true): failed to close\n" +
                "NonAutoclosableType(closed=true, fail=true): failed to close"));
        assertThat(exception.getSuppressed().length, is(2));
        assertThat(exception.getSuppressed()[0], instanceOf(UnsupportedOperationException.class));
        assertThat(exception.getSuppressed()[1], instanceOf(IllegalArgumentException.class));

        assertThat(instance1.closed, is(true));
        assertThat(instance2.closed, is(true));
    }

    @Test
    public void resourcesAreNotClosedTwice() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(CountingClosable.class)
                .withLifecycleManagement()
                .build();
        final CountingClosable instance = injectMaid.getInstance(CountingClosable.class);
        assertThat(instance.count, is(0));
        injectMaid.close();
        assertThat(instance.count, is(1));
        injectMaid.close();
        assertThat(instance.count, is(1));
    }
}
