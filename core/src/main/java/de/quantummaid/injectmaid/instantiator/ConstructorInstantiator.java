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

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.ScopeManager;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstructorInstantiator implements Instantiator {
    private final ResolvedConstructor constructor;

    public static ConstructorInstantiator constructorInstantiator(final ResolvedConstructor constructor) {
        return new ConstructorInstantiator(constructor);
    }

    public ResolvedConstructor constructor() {
        return constructor;
    }

    @Override
    public List<ResolvedType> dependencies() {
        return constructor.getParameters().stream()
                .map(ResolvedParameter::getType)
                .collect(toList());
    }

    @Override
    public Object instantiate(final List<Object> dependencies,
                              final ScopeManager scopeManager,
                              final InjectMaid injectMaid) throws Exception {
        final Constructor<?> rawConstructor = constructor.getConstructor();
        final Object[] parameters = dependencies.toArray();
        return rawConstructor.newInstance(parameters);
    }

    @Override
    public String description() {
        return String.format("constructor '%s'", constructor.describe());
    }
}
