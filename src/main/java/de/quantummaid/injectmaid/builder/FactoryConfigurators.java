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

package de.quantummaid.injectmaid.builder;

import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.injectmaid.ReusePolicy;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;

import static de.quantummaid.injectmaid.ReusePolicy.PROTOTYPE;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface FactoryConfigurators {

    default InjectMaidBuilder withFactory(final Class<?> type,
                                          final Class<?> factory) {
        final GenericType<?> genericType = genericType(type);
        final GenericType<?> genericFactory = genericType(factory);
        return withFactory(genericType, genericFactory);
    }

    default InjectMaidBuilder withFactory(final GenericType<?> type, final GenericType<?> factory) {
        return withFactory(type, factory, PROTOTYPE);
    }

    default InjectMaidBuilder withFactory(final Class<?> type,
                                          final Class<?> factory,
                                          final ReusePolicy reusePolicy) {
        final GenericType<?> genericType = genericType(type);
        final GenericType<?> genericFactory = genericType(factory);
        return withFactory(genericType, genericFactory, reusePolicy);
    }

    default InjectMaidBuilder withFactory(final GenericType<?> type,
                                          final GenericType<?> factory,
                                          final ReusePolicy reusePolicy) {
        final ResolvedType resolvedType = type.toResolvedType();
        final ResolvedType resolvedFactory = factory.toResolvedType();
        return withFactory(resolvedType, resolvedFactory, reusePolicy);
    }

    InjectMaidBuilder withFactory(ResolvedType type, ResolvedType factory, ReusePolicy reusePolicy);
}
