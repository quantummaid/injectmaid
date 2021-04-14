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

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.quantummaid.injectmaid.validators.NotNullValidator.validateNotNull;
import static java.util.Collections.emptyList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Scope {
    private final List<ResolvedType> scope;

    public static Scope rootScope() {
        return new Scope(emptyList());
    }

    public Scope childScope(final ResolvedType subScope) {
        validateNotNull(subScope, "subScope");
        final ArrayList<ResolvedType> newScope = new ArrayList<>(scope);
        newScope.add(subScope);
        return new Scope(newScope);
    }

    public String render() {
        return scope.stream()
                .map(ResolvedType::simpleDescription)
                .collect(Collectors.joining("/", "/", ""));
    }

    public int size() {
        return scope.size();
    }

    public boolean containsElement(final ResolvedType type) {
        return scope.contains(type);
    }

    public boolean contains(final Scope other) {
        if (size() > other.size()) {
            return false;
        }
        for (int i = 0; i < scope.size(); ++i) {
            final ResolvedType thisPart = scope.get(i);
            final ResolvedType otherPart = other.scope.get(i);
            if (!thisPart.equals(otherPart)) {
                return false;
            }
        }
        return true;
    }
}
