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

import de.quantummaid.injectmaid.api.interception.Interceptor;
import de.quantummaid.injectmaid.api.interception.InterceptorFactory;
import de.quantummaid.injectmaid.api.interception.ScopeEntryInterceptor;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static de.quantummaid.injectmaid.api.interception.NoOpInterceptor.NO_OP_INTERCEPTOR;
import static de.quantummaid.injectmaid.api.interception.timing.TimingScopeEntryInterceptor.timingScopeEntryInterceptor;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScopeEntryTimingInterceptorFactory implements InterceptorFactory {
    private final Scope scope;
    private final Duration maxDuration;

    public static ScopeEntryTimingInterceptorFactory scopeEntryTimingInterceptorFactory(final Duration maxDuration) {
        return new ScopeEntryTimingInterceptorFactory(rootScope(), maxDuration);
    }

    static ScopeEntryTimingInterceptorFactory scopeEntryTimingInterceptorFactory(final Scope scope,
                                                                                 final Duration maxDuration) {
        return new ScopeEntryTimingInterceptorFactory(scope, maxDuration);
    }

    @Override
    public Interceptor createInterceptor() {
        return NO_OP_INTERCEPTOR;
    }

    @Override
    public ScopeEntryInterceptor createScopeEntryInterceptor() {
        return timingScopeEntryInterceptor(scope, maxDuration);
    }
}
