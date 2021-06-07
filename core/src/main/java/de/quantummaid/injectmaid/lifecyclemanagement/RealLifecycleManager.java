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

import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closeable;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closers;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RealLifecycleManager implements LifecycleManager {
    private final Closers closers;
    private final List<Closeable> closeables = new ArrayList<>();
    private final Scope scope;
    private final LifecycleManager parent;

    public static LifecycleManager realLifecycleManager(final Closers closers,
                                                        final Scope scope) {
        return new RealLifecycleManager(closers, scope, null);
    }

    @Override
    public LifecycleManager newInstance(final Scope scope) {
        return new RealLifecycleManager(closers, scope, this);
    }

    @Override
    public void registerInstance(final Object instance, final Scope scope) {
        if (!this.scope.equals(scope)) {
            if (parent == null) {
                throw injectMaidException(
                        "unable to register autoclosable in scope '" + scope.render() + "' - this should never happen");
            }
            parent.registerInstance(instance, scope);
        } else {
            closers.createCloseable(instance)
                    .ifPresent(closeables::add);
        }
    }

    @Override
    public void closeAll(final List<ExceptionDuringClose> exceptions) {
        closeables.forEach(autoCloseable ->
                autoCloseable.close()
                        .ifPresent(exceptions::add)
        );
    }

    @Override
    public LifecycleManager child() {
        return this;
    }
}
