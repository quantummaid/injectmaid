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

package de.quantummaid.injectmaid.api.interception.timing;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.api.interception.InterceptorFactory;
import de.quantummaid.injectmaid.api.interception.ScopeEntryInterceptor;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

import static de.quantummaid.injectmaid.api.interception.timing.EnforcedMaxScopeEntryTimeExceededException.enforcedMaxScopeEntryTimeExceededException;
import static de.quantummaid.injectmaid.api.interception.timing.ScopeEntryTimingInterceptorFactory.scopeEntryTimingInterceptorFactory;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TimingScopeEntryInterceptor implements ScopeEntryInterceptor {
    private final Scope scope;
    private final Duration maxDuration;
    private Instant before;

    public static TimingScopeEntryInterceptor timingScopeEntryInterceptor(final Scope scope,
                                                                          final Duration maxDuration) {
        return new TimingScopeEntryInterceptor(scope, maxDuration);
    }

    @Override
    public InterceptorFactory beforeEnterScope(final TypeIdentifier scopeType,
                                               final Object scopeObject) {
        before = Instant.now();
        return scopeEntryTimingInterceptorFactory(scope.childScope(scopeType), maxDuration);
    }

    @Override
    public void afterEnterScope(final TypeIdentifier scopeType,
                                final Object scopeObject,
                                final InjectMaid scopedInjectMaid) {
        final Instant after = Instant.now();
        final Duration duration = Duration.between(before, after);
        if (duration.compareTo(maxDuration) > 0) {
            throw enforcedMaxScopeEntryTimeExceededException(
                    scope.childScope(scopeType),
                    scopeObject,
                    scopedInjectMaid,
                    maxDuration,
                    duration
            );
        }
    }
}
