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

import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Scopes {
    private final List<Scope> scopes;

    public static Scopes scopes() {
        return new Scopes(new ArrayList<>());
    }

    public void validateElementNotUsedSomewhereElse(final ResolvedType element,
                                                    final Scope currentScope) {
        final Scope ignoredScope = currentScope.childScope(element);
        scopes.stream()
                .filter(scope -> !scope.equals(ignoredScope))
                .filter(scope -> scope.containsElement(element))
                .findFirst()
                .ifPresent(scope -> {
                    throw InjectMaidException.injectMaidException(format(
                            "Scope type '%s' is already used in scope '%s'",
                            element.simpleDescription(), scope.render()));
                });
    }

    public void add(final Scope scope) {
        validateNotNull(scope, "scope");
        scopes.add(scope);
    }

    public boolean contains(final Scope scope) {
        return scopes.contains(scope);
    }

    public List<Scope> asList() {
        return scopes;
    }
}
