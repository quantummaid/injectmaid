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

import de.quantummaid.injectmaid.ReusePolicy;
import de.quantummaid.injectmaid.detection.DetectionResult;
import de.quantummaid.injectmaid.detection.SingletonSwitch;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.statemachine.Context;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.injectmaid.detection.Detectors.detect;
import static de.quantummaid.injectmaid.detection.SingletonSwitch.singletonSwitch;
import static de.quantummaid.injectmaid.statemachine.states.Failed.failed;
import static de.quantummaid.injectmaid.statemachine.states.ResolvingDependencies.resolvingDependencies;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Unresolved implements State {
    private final Context context;

    public static State unresolved(final Context context) {
        return new Unresolved(context);
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public State detectInstantiator() {
        final ResolvedType type = context.type();
        final ReusePolicy oldReusePolicy = context.reusePolicy();
        final SingletonSwitch singletonSwitch = singletonSwitch(oldReusePolicy);
        final DetectionResult result = detect(type, singletonSwitch);
        if (result.isFailure()) {
            context.setErrorMessage(result.errorMessage());
            return failed(context);
        }
        final Instantiator instantiator = result.instantiator();
        context.setInstantiator(instantiator);
        final ReusePolicy newReusePolicy = singletonSwitch.getReusePolicy();
        context.setReusePolicy(newReusePolicy);
        return resolvingDependencies(context);
    }
}
