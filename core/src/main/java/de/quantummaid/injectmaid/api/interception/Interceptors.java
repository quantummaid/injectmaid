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

import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class Interceptors {
    private final List<Interceptor> interceptors;

    public static Interceptors interceptors() {
        return new Interceptors(new ArrayList<>());
    }

    public Optional<?> interceptBefore(final ResolvedType type) {
        return interceptors.stream()
                .map(interceptor -> interceptor.interceptBeforeInstantiation(type))
                .flatMap(Optional::stream)
                .findFirst();
    }

    public Object interceptAfter(final ResolvedType type,
                                 final Object object) {
        Object current = object;
        for (final Interceptor interceptor : interceptors) {
            current = interceptor.interceptAfterInstantiation(type, current);
        }
        return current;
    }

    public void addInterceptor(final Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public Interceptors enterScope(final ResolvedType scopeType,
                                   final Object scopeObject) {
        final List<Interceptor> scopedInterceptors = interceptors.stream()
                .map(interceptor -> interceptor.enterScope(scopeType, scopeObject))
                .collect(toList());
        return new Interceptors(scopedInterceptors);
    }
}
