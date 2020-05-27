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

package de.quantummaid.injectmaid.statemachine;

import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.statemachine.states.State;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class States {
    private final List<State> states;
    private final List<State> newStates;

    public static States states() {
        return new States(new ArrayList<>(), new ArrayList<>());
    }

    public void addIfNotPresent(final State state) {
        final Context context = state.context();
        if (!contains(context.type(), context.scope())) {
            newStates.add(state);
        }
    }

    public void addOrFailIfAlreadyPresent(final State state) {
        final Context context = state.context();
        final ResolvedType type = context.type();
        final Scope scope = context.scope();
        if (containsExactly(type, scope)) {
            throw injectMaidException(format("Type '%s' has already been registered in scope '%s'", type.description(), scope.render()));
        }
        newStates.add(state);
    }

    public void update(final UnaryOperator<State> signal) {
        states.stream()
                .map(signal)
                .forEach(newStates::add);
        states.clear();
        states.addAll(newStates);
        newStates.clear();
    }

    public boolean allFinal() {
        if (!newStates.isEmpty()) {
            return false;
        }
        return states.stream().allMatch(State::isFinal);
    }

    public <T> Stream<T> collect(final Function<State, T> mapper) {
        return states.stream()
                .map(mapper);
    }

    private boolean containsExactly(final ResolvedType type,
                                    final Scope scope) {
        final boolean contains = states.stream()
                .map(State::context)
                .filter(context -> context.type().equals(type))
                .map(Context::scope)
                .anyMatch(scope::equals);
        if (contains) {
            return true;
        }
        return newStates.stream()
                .map(State::context)
                .filter(context -> context.type().equals(type))
                .map(Context::scope)
                .anyMatch(scope::equals);
    }

    private boolean contains(final ResolvedType type,
                             final Scope scope) {
        final boolean contains = states.stream()
                .anyMatch(state -> state.matches(type, scope));
        if (contains) {
            return true;
        }
        return newStates.stream()
                .anyMatch(state -> state.matches(type, scope));
    }
}
