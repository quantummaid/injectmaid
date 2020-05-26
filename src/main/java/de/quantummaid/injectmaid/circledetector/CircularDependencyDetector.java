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

package de.quantummaid.injectmaid.circledetector;

import de.quantummaid.injectmaid.Definition;
import de.quantummaid.injectmaid.Definitions;
import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.ResolvedType;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public final class CircularDependencyDetector {

    private CircularDependencyDetector() {
    }

    public static void validateNoCircularDependencies(final Definitions definitions) {
        definitions.allScopes()
                .forEach(scope -> validateNoCircularDependenciesInScope(definitions, scope));
    }

    private static void validateNoCircularDependenciesInScope(final Definitions definitions, final Scope scope) {
        final List<Definition> definitionList = definitions.definitionsOnScope(scope);
        definitionList.forEach(definition -> {
            final List<Definition> alreadyVisited = new ArrayList<>();
            detectCircle(definition, alreadyVisited, definitions, scope);
        });
    }

    private static void detectCircle(final Definition definition,
                                     final List<Definition> alreadyVisited,
                                     final Definitions definitions,
                                     final Scope scope) {
        if (alreadyVisited.contains(definition)) {
            alreadyVisited.add(definition);
            final String circle = alreadyVisited.stream()
                    .map(Definition::type)
                    .map(ResolvedType::simpleDescription)
                    .collect(joining(" -> "));
            throw injectMaidException(format("Illegal circular dependency in scope '%s' detected: %s", scope.render(), circle));
        } else {
            alreadyVisited.add(definition);
            final Instantiator instantiator = definition.instantiator();
            for (final ResolvedType dependencyType : instantiator.dependencies()) {
                final Definition dependency = definitions.definitionFor(dependencyType, scope);
                detectCircle(dependency, new ArrayList<>(alreadyVisited), definitions, scope);
            }
        }
    }
}
