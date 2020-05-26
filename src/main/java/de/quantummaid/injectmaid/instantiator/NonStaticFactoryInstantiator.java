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

import de.quantummaid.injectmaid.ScopeManager;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NonStaticFactoryInstantiator implements Instantiator {
    private final ResolvedMethod method;
    private final ResolvedType type;

    public static NonStaticFactoryInstantiator nonStaticFactoryInstantiator(final ResolvedMethod method,
                                                                            final ResolvedType type) {
        return new NonStaticFactoryInstantiator(method, type);
    }

    public ResolvedMethod method() {
        return method;
    }

    @Override
    public List<ResolvedType> dependencies() {
        final List<ResolvedType> dependencies = new ArrayList<>();
        dependencies.add(type);
        method.parameters().stream()
                .map(ResolvedParameter::type)
                .forEach(dependencies::add);
        return dependencies;
    }

    @Override
    public Object instantiate(final List<Object> dependencies,
                              final ScopeManager scopeManager) throws Exception {
        final Method rawMethod = this.method.method();
        final Object instance = dependencies.get(0);
        final Object[] parameters = dependencies.subList(1, dependencies.size()).toArray();
        return rawMethod.invoke(instance, parameters);
    }

    @Override
    public String description() {
        return String.format("method '%s'", method.describe());
    }
}
