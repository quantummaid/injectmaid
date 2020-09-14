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

package de.quantummaid.injectmaid.lifecyclemanagement;

import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closers;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RealLifecycleManager implements LifecycleManager {
    private final Closers closers;
    private final List<AutoCloseable> autoCloseables = new ArrayList<>();

    public static LifecycleManager realLifecycleManager(final Closers closers) {
        return new RealLifecycleManager(closers);
    }

    @Override
    public LifecycleManager newInstance() {
        return new RealLifecycleManager(closers);
    }

    @Override
    public void registerInstance(final Object instance) {
        closers.createCloseable(instance)
                .ifPresent(autoCloseables::add);
    }

    @Override
    public void closeAll() {
        autoCloseables.forEach(autoCloseable -> {
            try {
                autoCloseable.close();
            } catch (final Exception e) {
                throw injectMaidException(format("exception during closing of object %s", autoCloseable), e);
            }
        });
    }
}
