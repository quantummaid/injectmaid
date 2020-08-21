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

import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

@FunctionalInterface
public interface ConstantConfigurators<T extends ConfigurationConfigurators<T>> {

    default <X> T withConstant(final Class<X> type, final X instance) {
        final GenericType<X> genericType = genericType(type);
        return withConstant(genericType, instance);
    }

    default <X> T withConstant(final GenericType<X> genericType, final X instance) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return withConstant(resolvedType, instance);
    }

    T withConstant(ResolvedType resolvedType, Object instance);
}
