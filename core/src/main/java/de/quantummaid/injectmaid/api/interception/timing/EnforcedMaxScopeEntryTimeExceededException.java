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
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;

import java.time.Duration;

public final class EnforcedMaxScopeEntryTimeExceededException extends RuntimeException {
    private final transient Scope scope;
    private final transient Object scopeObject;
    private final transient InjectMaid scopedInjectMaid;
    private final transient Duration maxTime;
    private final transient Duration actualTime;

    public EnforcedMaxScopeEntryTimeExceededException(final Scope scope,
                                                      final Object scopeObject,
                                                      final InjectMaid scopedInjectMaid,
                                                      final Duration maxTime,
                                                      final Duration actualTime,
                                                      final String message) {
        super(message);
        this.scope = scope;
        this.scopeObject = scopeObject;
        this.scopedInjectMaid = scopedInjectMaid;
        this.maxTime = maxTime;
        this.actualTime = actualTime;
    }

    public static EnforcedMaxScopeEntryTimeExceededException enforcedMaxScopeEntryTimeExceededException(
            final Scope scope,
            final Object scopeObject,
            final InjectMaid scopedInjectMaid,
            final Duration maxTime,
            final Duration actualTime
    ) {
        final String message = "took " + actualTime.toMillis() + "ms to enter scope " + scope.render() +
                " with object " + scopeObject + "but only " + maxTime.toMillis() + "ms allowed\n" +
                "instantiation times: " + scopedInjectMaid.instantiationTimes().render();
        return new EnforcedMaxScopeEntryTimeExceededException(
                scope, scopeObject, scopedInjectMaid, maxTime, actualTime, message
        );
    }

    public Scope scope() {
        return scope;
    }

    public Object scopeObject() {
        return scopeObject;
    }

    public InjectMaid scopedInjectMaid() {
        return scopedInjectMaid;
    }

    public Duration maxTime() {
        return maxTime;
    }

    public Duration actualTime() {
        return actualTime;
    }
}
