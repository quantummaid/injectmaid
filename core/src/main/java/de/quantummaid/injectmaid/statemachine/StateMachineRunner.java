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

import de.quantummaid.injectmaid.Definition;
import de.quantummaid.injectmaid.InjectMaidException;
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.statemachine.states.State;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.Definition.definition;
import static java.lang.String.join;

public final class StateMachineRunner {

    private StateMachineRunner() {
    }

    public static Map<ResolvedType, List<Definition>> runStateMachine(final States states) {
        while (!states.allFinal()) {
            states.update(State::detectInstantiator);
            states.update(State::resolvedDependencies);
        }
        final Map<ResolvedType, List<Definition>> definitionsMap = new HashMap<>();
        final List<String> errors = new ArrayList<>();
        states.collect(State::context)
                .forEach(context -> {
                    final ResolvedType type = context.type();
                    final Scope scopeOfType = context.scope();
                    context.instantiator().ifPresentOrElse(instantiator -> {
                        final ReusePolicy reusePolicy = context.reusePolicy();
                        final Definition definition = definition(type, scopeOfType, instantiator, reusePolicy);
                        if (!definitionsMap.containsKey(type)) {
                            definitionsMap.put(type, new ArrayList<>(1));
                        }
                        definitionsMap.get(type).add(definition);
                    }, () -> {
                        final String errorMessage = context.errorMessage();
                        errors.add(errorMessage);
                    });
                });
        if (!errors.isEmpty()) {
            final String joinedErrors = join("\n\n", errors);
            throw InjectMaidException.injectMaidException(joinedErrors);
        }
        return definitionsMap;
    }
}
