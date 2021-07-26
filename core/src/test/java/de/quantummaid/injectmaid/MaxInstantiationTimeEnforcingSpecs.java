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

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.api.interception.timing.EnforcedMaxInstantiationTimeExceededException;
import de.quantummaid.injectmaid.domain.MyTypeWithString;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public final class MaxInstantiationTimeEnforcingSpecs {

    @Test
    public void maxInstantiationTimeCanBeEnforced() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(300);
                    return "foo";
                })
                .enforcingMaximumInstantiationTimeOf(Duration.ofMillis(200))
                .build();
        EnforcedMaxInstantiationTimeExceededException exception = null;
        try {
            injectMaid.getInstance(String.class);
        } catch (final EnforcedMaxInstantiationTimeExceededException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.type().simpleDescription(), is("String"));
        assertThat(exception.rootType().simpleDescription(), is("String"));
        assertThat(exception.instance(), is("foo"));
        assertThat((int) exception.actualTime().toMillis(), is(greaterThanOrEqualTo(300)));
        assertThat((int) exception.maxTime().toMillis(), is(200));
        assertThat(exception.reusePolicy(), is(ReusePolicy.PROTOTYPE));
    }

    @Test
    public void maxInstantiationTimeCanBeEnforcedInLazySingleton() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(300);
                    return "foo";
                }, ReusePolicy.LAZY_SINGLETON)
                .enforcingMaximumInstantiationTimeOf(Duration.ofMillis(200))
                .build();
        EnforcedMaxInstantiationTimeExceededException exception = null;
        try {
            injectMaid.getInstance(String.class);
        } catch (final EnforcedMaxInstantiationTimeExceededException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.type().simpleDescription(), is("String"));
        assertThat(exception.rootType().simpleDescription(), is("String"));
        assertThat(exception.instance(), is("foo"));
        assertThat((int) exception.actualTime().toMillis(), is(greaterThanOrEqualTo(300)));
        assertThat((int) exception.maxTime().toMillis(), is(200));
        assertThat(exception.reusePolicy(), is(ReusePolicy.LAZY_SINGLETON));
    }

    @Test
    public void maxInstantiationTimeCanBeEnforcedInEagerSingleton() {
        EnforcedMaxInstantiationTimeExceededException exception = null;
        try {
            anInjectMaid()
                    .withCustomType(String.class, () -> {
                        sleep(300);
                        return "foo";
                    }, ReusePolicy.EAGER_SINGLETON)
                    .enforcingMaximumInstantiationTimeOf(Duration.ofMillis(200))
                    .build();
        } catch (final EnforcedMaxInstantiationTimeExceededException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.type().simpleDescription(), is("String"));
        assertThat(exception.rootType().simpleDescription(), is("String"));
        assertThat(exception.instance(), is("foo"));
        assertThat((int) exception.actualTime().toMillis(), is(greaterThanOrEqualTo(300)));
        assertThat((int) exception.maxTime().toMillis(), is(200));
        assertThat(exception.reusePolicy(), is(ReusePolicy.EAGER_SINGLETON));
    }

    @Test
    public void maxInstantiationTimeCanBeEnforcedInDependencies() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(MyTypeWithString.class, String.class, MyTypeWithString::new)
                .withCustomType(String.class, () -> {
                    sleep(300);
                    return "foo";
                })
                .enforcingMaximumInstantiationTimeOf(Duration.ofMillis(200))
                .build();
        EnforcedMaxInstantiationTimeExceededException exception = null;
        try {
            injectMaid.getInstance(MyTypeWithString.class);
        } catch (final EnforcedMaxInstantiationTimeExceededException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessage(), containsString("as a dependency of type de.quantummaid.injectmaid.domain.MyTypeWithString"));
        assertThat(exception.type().simpleDescription(), is("String"));
        assertThat(exception.rootType().simpleDescription(), is("MyTypeWithString"));
        assertThat(exception.instance(), is("foo"));
        assertThat((int) exception.actualTime().toMillis(), is(greaterThanOrEqualTo(300)));
        assertThat((int) exception.maxTime().toMillis(), is(200));
        assertThat(exception.reusePolicy(), is(ReusePolicy.PROTOTYPE));
    }

    private static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }
}
