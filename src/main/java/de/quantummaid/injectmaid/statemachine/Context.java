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

import de.quantummaid.injectmaid.ReusePolicy;
import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Context {
    private final ResolvedType type;
    private final Scope scope;
    private final States states;
    private ReusePolicy reusePolicy;
    private Instantiator instantiator;

    public static Context context(final ResolvedType type,
                                  final Scope scope,
                                  final States states,
                                  final ReusePolicy reusePolicy) {
        return new Context(type, scope, states, reusePolicy, null);
    }

    public ResolvedType type() {
        return type;
    }

    public Scope scope() {
        return scope;
    }

    public States states() {
        return states;
    }

    public void setReusePolicy(final ReusePolicy reusePolicy) {
        this.reusePolicy = reusePolicy;
    }

    public ReusePolicy reusePolicy() {
        return reusePolicy;
    }

    public Optional<Instantiator> instantiator() {
        return Optional.ofNullable(instantiator);
    }

    public void setInstantiator(final Instantiator instantiator) {
        this.instantiator = instantiator;
    }
}
