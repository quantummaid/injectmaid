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

package de.quantummaid.injectmaid.statemachine.states;

import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.statemachine.Context;
import de.quantummaid.injectmaid.statemachine.States;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.injectmaid.ReusePolicy.PROTOTYPE;
import static de.quantummaid.injectmaid.statemachine.states.Resolved.resolved;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResolvingDependencies implements State {
    private final Context context;

    public static ResolvingDependencies resolvingDependencies(final Context context) {
        return new ResolvingDependencies(context);
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public State resolvedDependencies() {
        final Scope scope = context.scope();
        final States states = context.states();
        final Instantiator instantiator = context.instantiator().orElseThrow();
        instantiator.dependencies().stream()
                .map(type -> Context.context(type, scope, states, PROTOTYPE))
                .map(Unresolved::unresolved)
                .forEach(states::addIfNotPresent);
        return resolved(context);
    }
}
