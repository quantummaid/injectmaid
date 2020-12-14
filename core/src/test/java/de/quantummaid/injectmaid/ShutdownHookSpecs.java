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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ShutdownHookSpecs {

    @Test
    public void shutdownHookCanOnlyBeRegisteredWithActiveLifecycleManagement() {
        final Exception exception = catchException(() ->
                anInjectMaid()
                        .closeOnJvmShutdown()
                        .build()
        );
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("can only close on JVM shutdown if lifecycle management is activated"));
    }

    @Test
    public void noShutdownHookIsRegisteredWhenNotExplicitlyConfigured() {
        final int numberOfShutdownHooksBefore = numberOfShutdownHooks();
        anInjectMaid()
                .withLifecycleManagement()
                .build();
        final int numberOfShutdownHooksAfterCreation = numberOfShutdownHooks();
        assertThat(numberOfShutdownHooksAfterCreation, is(numberOfShutdownHooksBefore));
    }

    @Test
    public void shutdownHookIsRegisteredAndThenUnregisteredOnClose() {
        final int numberOfShutdownHooksBefore = numberOfShutdownHooks();
        final InjectMaid injectMaid = anInjectMaid()
                .withLifecycleManagement()
                .closeOnJvmShutdown()
                .build();
        final int numberOfShutdownHooksAfterCreation = numberOfShutdownHooks();
        assertThat(numberOfShutdownHooksAfterCreation, is(numberOfShutdownHooksBefore + 1));
        injectMaid.close();
        final int numberOfShutdownHooksAfterClose = numberOfShutdownHooks();
        assertThat(numberOfShutdownHooksAfterClose, is(numberOfShutdownHooksBefore));
    }

    @SuppressWarnings("unchecked")
    private static int numberOfShutdownHooks() {
        try {
            final Class<?> clazz = Class.forName("java.lang.ApplicationShutdownHooks");
            final Field field = clazz.getDeclaredField("hooks");
            field.setAccessible(true);
            final Map<String, Object> hooks = (Map<String, Object>) field.get(null);
            field.setAccessible(false);
            return hooks.size();
        } catch (final ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
