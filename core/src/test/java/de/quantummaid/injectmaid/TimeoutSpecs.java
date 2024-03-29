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
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static java.time.Duration.ofMillis;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("java:S2925")
public final class TimeoutSpecs {

    @Test
    public void enterScopeCanHaveEnforcedMaxTime() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(String.class, builder -> builder.withCustomType(Integer.class, () -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }, ReusePolicy.EAGER_SINGLETON))
                .build();
        final Exception exception = catchException(() -> injectMaid.enterScopeWithTimeout("foo", ofMillis(100)));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), containsString("entering scope java.lang.String must not take longer than 100ms"));
    }

    @Test
    public void initializeAllSingletonsCanHaveEnforcedMaxTime() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(Integer.class, () -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }, ReusePolicy.LAZY_SINGLETON)
                .build();
        final Exception exception = catchException(() -> injectMaid.initializeAllSingletons(ofMillis(100)));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), containsString("initializing all singletons must not take longer than 100ms"));
    }
}
