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

import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.AddReasonSignal;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;
import de.quantummaid.reflectmaid.typescanner.states.Resolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.quantummaid.injectmaid.Requirements.REGISTERED;
import static de.quantummaid.reflectmaid.typescanner.Reason.becauseOf;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectMaidResolver implements Resolver<InjectMaidTypeScannerResult> {

    public static InjectMaidResolver injectMaidResolver() {
        return new InjectMaidResolver();
    }

    @NotNull
    @Override
    public List<Signal<InjectMaidTypeScannerResult>> resolve(final InjectMaidTypeScannerResult result,
                                                             final @NotNull TypeIdentifier type,
                                                             final @NotNull Scope scope,
                                                             final @NotNull DetectionRequirements detectionRequirements) {
        return result.toDefinition()
                .instantiator()
                .dependencies().stream()
                .map(dependency -> AddReasonSignal.<InjectMaidTypeScannerResult>addReasonSignal(
                        dependency, scope, REGISTERED, becauseOf(type)))
                .collect(toList());
    }
}
