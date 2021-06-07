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

package de.quantummaid.injectmaid.instantiator;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult;
import de.quantummaid.injectmaid.statemachine.ReusePolicyMapper;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult.result;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomInstantiatorFactory implements StateFactory<InjectMaidTypeScannerResult> {
    private final TypeIdentifier typeIdentifier;
    private final Instantiator instantiator;
    private final ReusePolicyMapper reusePolicyMapper;

    public static CustomInstantiatorFactory customInstantiatorFactory(final TypeIdentifier typeIdentifier,
                                                                      final Instantiator instantiator,
                                                                      final ReusePolicyMapper reusePolicyMapper) {
        return new CustomInstantiatorFactory(typeIdentifier, instantiator, reusePolicyMapper);
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        return typeIdentifier.equals(type);
    }

    @Override
    public void create(@NotNull final TypeIdentifier type,
                       @NotNull final Context<InjectMaidTypeScannerResult> context) {
        final ReusePolicy reusePolicy = reusePolicyMapper.reusePolicyFor(type);
        final InjectMaidTypeScannerResult result = result(typeIdentifier, context.getScope(), instantiator, reusePolicy);
        context.setManuallyConfiguredResult(result);
    }
}
