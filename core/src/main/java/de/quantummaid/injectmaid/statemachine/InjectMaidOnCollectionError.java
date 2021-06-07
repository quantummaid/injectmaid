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

import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.OnCollectionError;
import de.quantummaid.reflectmaid.typescanner.Report;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.log.StateLog;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.StringJoiner;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Requirements.REGISTERED;
import static de.quantummaid.injectmaid.statemachine.RootCauseAnalysisCreator.rootCauseAnalysisCreator;

public final class InjectMaidOnCollectionError implements OnCollectionError<InjectMaidTypeScannerResult> {

    public static InjectMaidOnCollectionError injectMaidOnCollectionError() {
        return new InjectMaidOnCollectionError();
    }

    @Override
    public void onCollectionError(@NotNull final Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<InjectMaidTypeScannerResult>>> results,
                                  @NotNull final StateLog<InjectMaidTypeScannerResult> log,
                                  @NotNull final Map<TypeIdentifier, ? extends Map<Scope, Report<InjectMaidTypeScannerResult>>> failures) {
        final RootCauseAnalysisCreator<InjectMaidTypeScannerResult> rootCauseAnalysisCreator = rootCauseAnalysisCreator(results);
        final StringJoiner joiner = new StringJoiner("\n\n");
        failures.forEach((typeIdentifier, scopeReportMap) ->
                scopeReportMap.forEach((scope, report) -> {
                    final String message = createMessage(typeIdentifier, scope, report, rootCauseAnalysisCreator);
                    joiner.add(message);
                }));
        throw injectMaidException(joiner.toString());
    }

    private String createMessage(final TypeIdentifier typeIdentifier,
                                 final Scope scope,
                                 final Report<?> report,
                                 final RootCauseAnalysisCreator<?> rootCauseAnalysisCreator) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(typeIdentifier.description());
        stringBuilder.append(":\n");
        stringBuilder.append(report.errorMessage());
        stringBuilder.append("\nWhy it has been registered:\n");
        final RootCauseAnalysis rootCauseAnalysis = rootCauseAnalysisCreator.rootCauseAnalysisFor(typeIdentifier, scope, REGISTERED);
        stringBuilder.append(rootCauseAnalysis.render());
        return stringBuilder.toString();
    }
}
