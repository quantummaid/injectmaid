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
import de.quantummaid.injectmaid.InjectMaidModule;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface ScopeConfigurators {

    default InjectMaidBuilder withScope(final Class<?> scopeType, final InjectMaidModule module) {
        final GenericType<?> genericType = genericType(scopeType);
        return withScope(genericType, module);
    }

    default InjectMaidBuilder withScope(final GenericType<?> scopeType, final InjectMaidModule module) {
        final ResolvedType resolvedType = scopeType.toResolvedType();
        return withScope(resolvedType, module);
    }

    InjectMaidBuilder withScope(ResolvedType scopeType, InjectMaidModule module);
}
