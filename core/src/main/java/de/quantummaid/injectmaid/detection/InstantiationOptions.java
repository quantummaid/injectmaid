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

package de.quantummaid.injectmaid.detection;

import de.quantummaid.injectmaid.instantiator.ConstructorInstantiator;
import de.quantummaid.injectmaid.instantiator.NonStaticFactoryInstantiator;
import de.quantummaid.injectmaid.instantiator.StaticFactoryInstantiator;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.injectmaid.instantiator.NonStaticFactoryInstantiator.nonStaticFactoryInstantiator;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InstantiationOptions {
    private final List<ConstructorInstantiator> constructors;
    private final List<StaticFactoryInstantiator> staticFactoryMethods;
    private final List<NonStaticFactoryInstantiator> nonStaticFactoryMethods;

    public static InstantiationOptions loadInstantiationOptions(final ResolvedType typeToInstantiate,
                                                                final ClassType creatingClassType) {
        final List<ConstructorInstantiator> constructors;
        final List<NonStaticFactoryInstantiator> nonStaticFactoryMethods;
        if (typeToInstantiate.equals(creatingClassType)) {
            constructors = creatingClassType.constructors().stream()
                    .filter(ResolvedConstructor::isPublic)
                    .map(ConstructorInstantiator::constructorInstantiator)
                    .collect(toList());
            nonStaticFactoryMethods = emptyList();
        } else {
            constructors = emptyList();
            nonStaticFactoryMethods = creatingClassType.methods().stream()
                    .filter(method -> !isStatic(method.getMethod().getModifiers()))
                    .filter(method -> method.returnType()
                            .map(typeToInstantiate::equals)
                            .orElse(false))
                    .map(method -> nonStaticFactoryInstantiator(method, creatingClassType))
                    .collect(toList());
        }
        final List<StaticFactoryInstantiator> factoryMethods = creatingClassType.methods().stream()
                .filter(ResolvedMethod::isPublic)
                .filter(method -> isStatic(method.getMethod().getModifiers()))
                .filter(method -> method.returnType()
                        .map(typeToInstantiate::equals)
                        .orElse(false))
                .map(StaticFactoryInstantiator::staticFactoryInstantiator)
                .collect(toList());
        return new InstantiationOptions(constructors, factoryMethods, nonStaticFactoryMethods);
    }

    public List<ConstructorInstantiator> constructors() {
        return constructors;
    }

    public List<StaticFactoryInstantiator> staticFactoryMethods() {
        return staticFactoryMethods;
    }

    public List<NonStaticFactoryInstantiator> nonStaticFactoryMethods() {
        return nonStaticFactoryMethods;
    }
}
