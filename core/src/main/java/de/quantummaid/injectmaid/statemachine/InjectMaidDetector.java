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

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.detection.Detectors;
import de.quantummaid.injectmaid.detection.SingletonSwitch;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import de.quantummaid.reflectmaid.typescanner.states.Detector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.quantummaid.injectmaid.detection.SingletonSwitch.singletonSwitch;
import static de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult.result;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectMaidDetector implements Detector<InjectMaidTypeScannerResult> {
    private final FactoryMapper factoryMapper;
    private final ReusePolicyMapper reusePolicyMapper;

    public static InjectMaidDetector injectMaidDetector(final FactoryMapper factoryMapper,
                                                        final ReusePolicyMapper reusePolicyMapper) {
        return new InjectMaidDetector(factoryMapper, reusePolicyMapper);
    }

    @NotNull
    @Override
    public DetectionResult<InjectMaidTypeScannerResult> detect(@NotNull final TypeIdentifier type,
                                                               @NotNull final Scope scope,
                                                               @NotNull final DetectionRequirements detectionRequirements) {
        final ReusePolicy oldReusePolicy = reusePolicyMapper.reusePolicyFor(type);
        final SingletonSwitch singletonSwitch = singletonSwitch(oldReusePolicy);
        final ResolvedType factory = factoryMapper.factoryFor(type).orElse(type.realType());
        final DetectionResult<Instantiator> result = Detectors.detect(type, factory, singletonSwitch);
        return result.mapWithNull(instantiator -> {
            final ReusePolicy reusePolicy = singletonSwitch.getReusePolicy();
            return result(type, scope, instantiator, reusePolicy);
        });
    }
}
