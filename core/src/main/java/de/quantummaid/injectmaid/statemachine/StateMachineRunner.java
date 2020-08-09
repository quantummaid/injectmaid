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
import de.quantummaid.injectmaid.ReusePolicy;
import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.statemachine.states.State;
import de.quantummaid.reflectmaid.ResolvedType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.Definition.definition;

public final class StateMachineRunner {

    private StateMachineRunner() {
    }

    public static Map<ResolvedType, List<Definition>> runStateMachine(final States states) {
        while (!states.allFinal()) {
            states.update(State::detectInstantiator);
            states.update(State::resolvedDependencies);
        }
        final Map<ResolvedType, List<Definition>> definitionsMap = new HashMap<>();
        states.collect(State::context)
                .map(context -> {
                    final ResolvedType type = context.type();
                    final Scope scopeOfType = context.scope();
                    final Instantiator instantiator = context.instantiator().orElseThrow();
                    final ReusePolicy reusePolicy = context.reusePolicy();
                    return definition(type, scopeOfType, instantiator, reusePolicy);
                })
                .forEach(definition -> {
                    final ResolvedType type = definition.type();
                    if (!definitionsMap.containsKey(type)) {
                        definitionsMap.put(type, new ArrayList<>(1));
                    }
                    definitionsMap.get(type).add(definition);
                });
        return definitionsMap;
    }
}
