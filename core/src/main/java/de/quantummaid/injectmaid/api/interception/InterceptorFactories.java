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

package de.quantummaid.injectmaid.api.interception;

import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.injectmaid.api.interception.SingletonInterceptorFactory.singletonInterceptorFactory;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InterceptorFactories {
    private final List<InterceptorFactory> factories;

    public static InterceptorFactories interceptorFactories(final List<InterceptorFactory> factories) {
        return new InterceptorFactories(factories);
    }

    public void addFactory(final InterceptorFactory factory) {
        factories.add(factory);
    }

    public void addInterceptor(final Interceptor interceptor) {
        final SingletonInterceptorFactory factory = singletonInterceptorFactory(interceptor);
        addFactory(factory);
    }

    public Interceptors interceptors() {
        final List<Interceptor> interceptors = factories.stream()
                .map(InterceptorFactory::createInterceptor)
                .collect(toList());
        return Interceptors.interceptors(interceptors);
    }

    public InterceptorFactories enterScope(final TypeIdentifier scopeType,
                                           final Object scopeObject) {
        final List<InterceptorFactory> scopedFactories = factories.stream()
                .map(factory -> factory.enterScope(scopeType, scopeObject))
                .collect(toList());
        return new InterceptorFactories(scopedFactories);
    }
}
