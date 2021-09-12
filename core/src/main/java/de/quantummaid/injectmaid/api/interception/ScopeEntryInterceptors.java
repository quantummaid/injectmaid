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

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class ScopeEntryInterceptors {
    private final List<ScopeEntryInterceptor> interceptors;

    public static ScopeEntryInterceptors scopeEntryInterceptors(final List<ScopeEntryInterceptor> interceptors) {
        return new ScopeEntryInterceptors(interceptors);
    }

    public List<InterceptorFactory> interceptBefore(final TypeIdentifier scopeType, final Object scopeObject) {
        return interceptors.stream()
                .map(interceptor -> interceptor.beforeEnterScope(scopeType, scopeObject))
                .collect(toList());
    }

    public void interceptAfter(final TypeIdentifier scopeType,
                               final Object scopeObject,
                               final InjectMaid scopedInjectMaid) {
        interceptors.forEach(scopeEntryInterceptor -> scopeEntryInterceptor.afterEnterScope(scopeType, scopeObject, scopedInjectMaid));
    }
}
