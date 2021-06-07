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
import de.quantummaid.reflectmaid.typescanner.Reason;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.requirements.RequirementName;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.SignalTarget;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.statemachine.RootCauseAnalysis.rootCauseAnalysis;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RootCauseAnalysisCreator<T> {
    private final Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<T>>> results;

    public static <T> RootCauseAnalysisCreator<T> rootCauseAnalysisCreator(
            final Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<T>>> results
    ) {
        return new RootCauseAnalysisCreator<>(results);
    }

    public RootCauseAnalysis rootCauseAnalysisFor(final TypeIdentifier typeIdentifier,
                                                  final Scope scope,
                                                  final RequirementName requirementName) {
        final List<RootCauseAnalysisPath> paths = rootCauseAnalysisFor(typeIdentifier, scope, requirementName, emptyList()).stream()
                .map(RootCauseAnalysisPath::rootCauseAnalysisPath)
                .collect(toList());
        return rootCauseAnalysis(paths);
    }

    private List<List<Reason>> rootCauseAnalysisFor(final TypeIdentifier typeIdentifier,
                                                    final Scope scope,
                                                    final RequirementName requirementName,
                                                    final List<Reason> currentReasons) {
        final CollectionResult<T> result = findResult(typeIdentifier, scope);
        final DetectionRequirements detectionRequirements = result.getDetectionRequirements();
        final List<Reason> reasons = detectionRequirements.reasonsFor(requirementName);
        return reasons.stream()
                .map(reason -> {
                    final SignalTarget parent = reason.getParent();
                    if (parent == null) {
                        return List.of(append(currentReasons, reason));
                    } else {
                        return rootCauseAnalysisFor(
                                parent.getTypeIdentifier(),
                                parent.getScope(),
                                requirementName,
                                append(currentReasons, reason)
                        );
                    }
                })
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private CollectionResult<T> findResult(final TypeIdentifier typeIdentifier, final Scope scope) {
        final Map<Scope, CollectionResult<T>> resultsByScope = results.get(typeIdentifier);
        return resultsByScope.get(scope);
    }

    private static <T> List<T> append(final List<T> list, final T element) {
        final List<T> copy = new ArrayList<>(list);
        copy.add(element);
        return copy;
    }
}
