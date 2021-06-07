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

package de.quantummaid.injectmaid.api.interception.overwrite;

import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.api.interception.Interceptor;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverwritingInterceptor implements Interceptor {
    private final Injector injector;

    public static OverwritingInterceptor overwritingInterceptor(final Injector injector) {
        return new OverwritingInterceptor(injector);
    }

    @Override
    public Optional<?> interceptBeforeInstantiation(final TypeIdentifier type) {
        if (!injector.canInstantiate(type)) {
            return Optional.empty();
        }
        final Object instance = injector.getInstance(type);
        return Optional.of(instance);
    }

    @Override
    public Object interceptAfterInstantiation(final TypeIdentifier type,
                                              final Object instance) {
        return instance;
    }

    @Override
    public Interceptor enterScope(final TypeIdentifier scopeType,
                                  final Object scopeObject) {
        final Injector scopedInjector = injector.enterScope(scopeType, scopeObject);
        return new OverwritingInterceptor(scopedInjector);
    }
}
