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

package de.quantummaid.injectmaid.interception.overwrite;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.interception.Interceptor;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverwritingInterceptor implements Interceptor {
    private final InjectMaid injectMaid;

    public static OverwritingInterceptor overwritingInterceptor(final InjectMaid injectMaid) {
        return new OverwritingInterceptor(injectMaid);
    }

    @Override
    public Optional<?> interceptBeforeInstantiation(final ResolvedType type) {
        if (!injectMaid.canInstantiate(type)) {
            return Optional.empty();
        }
        final Object instance = injectMaid.getInstance(type);
        return Optional.of(instance);
    }

    @Override
    public Object interceptAfterInstantiation(final ResolvedType type,
                                              final Object instance) {
        return instance;
    }

    @Override
    public Interceptor enterScope(final ResolvedType scopeType,
                                  final Object scopeObject) {
        final InjectMaid scopedInjectMaid = injectMaid.enterScope(scopeType, scopeObject);
        return new OverwritingInterceptor(scopedInjectMaid);
    }
}
