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

import de.quantummaid.injectmaid.InjectMaidException;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.OnCollectionError;
import de.quantummaid.reflectmaid.typescanner.Report;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.log.StateLog;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public final class InjectMaidOnCollectionError implements OnCollectionError<InjectMaidTypeScannerResult> {

    public static InjectMaidOnCollectionError injectMaidOnCollectionError() {
        return new InjectMaidOnCollectionError();
    }

    @Override
    public void onCollectionError(@NotNull final Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<InjectMaidTypeScannerResult>>> results,
                                  @NotNull final StateLog<InjectMaidTypeScannerResult> log,
                                  @NotNull final Map<TypeIdentifier, ? extends Map<Scope, Report<InjectMaidTypeScannerResult>>> failures) {
        final String message = failures.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(Report::errorMessage)
                .collect(joining("\n\n"));
        throw InjectMaidException.injectMaidException(message);
    }
}
