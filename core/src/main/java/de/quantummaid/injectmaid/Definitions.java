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

package de.quantummaid.injectmaid;

import de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Definitions {
    private final List<Scope> scopes;
    private final Map<TypeIdentifier, List<Definition>> definitions;

    public static Definitions definitions(final List<Scope> scopes,
                                          final Map<TypeIdentifier, Map<Scope, CollectionResult<InjectMaidTypeScannerResult>>> definitions) {
        final Map<TypeIdentifier, List<Definition>> mapOfLists = definitions.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> {
                    final Map<Scope, CollectionResult<InjectMaidTypeScannerResult>> byScope = entry.getValue();
                    return byScope.values().stream()
                            .map(CollectionResult::getDefinition)
                            .map(InjectMaidTypeScannerResult::toDefinition)
                            .collect(toList());
                }));
        return new Definitions(scopes, mapOfLists);
    }

    public boolean hasDefinitionFor(final TypeIdentifier type, final Scope scope) {
        if (!definitions.containsKey(type)) {
            return false;
        }
        return definitions.get(type).stream()
                .filter(definition -> definition.scope().contains(scope))
                .max(comparing(definition -> definition.scope().size()))
                .isPresent();
    }

    public Definition definitionFor(final TypeIdentifier type, final Scope scope) {
        if (!definitions.containsKey(type)) {
            throw injectMaidException(format("Cannot instantiate unregistered type '%s'", type.description()));
        }
        return definitions.get(type).stream()
                .filter(definition -> definition.scope().contains(scope))
                .max(comparing(definition -> definition.scope().size()))
                .orElseThrow(() -> injectMaidException(format(
                        "Tried to instantiate unregistered type '%s'", type.description())));
    }

    public List<Definition> definitionsOnScope(final Scope scope) {
        return definitions.values().stream()
                .flatMap(Collection::stream)
                .filter(definition -> definition.scope().equals(scope))
                .collect(toList());
    }

    public List<Scope> allScopes() {
        return scopes;
    }

    public String dump() {
        return definitions.values().stream()
                .flatMap(Collection::stream)
                .map(definition -> String.format("%s %s (%s)",
                        definition.scope().render(),
                        definition.type().description(),
                        definition.reusePolicy()))
                .sorted()
                .collect(joining("\n"));
    }
}
