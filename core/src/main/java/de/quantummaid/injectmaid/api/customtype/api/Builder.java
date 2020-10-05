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

package de.quantummaid.injectmaid.api.customtype.api;

import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.api.customtype.api.CustomType.customType;
import static de.quantummaid.injectmaid.api.customtype.api.CustomTypeData.customTypeInstantiator;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder {
    private final ResolvedType type;
    private final List<ResolvedType> dependencies;
    private InvocableFactory<?> factory;

    public static Builder builder(final ResolvedType type) {
        return new Builder(type, new ArrayList<>());
    }

    public void addParameter(final GenericType<?> parameter) {
        final ResolvedType resolvedType = parameter.toResolvedType();
        dependencies.add(resolvedType);
    }

    public void setFactory(final InvocableFactory<?> factory) {
        this.factory = factory;
    }

    CustomType build() {
        final CustomTypeData customTypeData = customTypeInstantiator(dependencies, factory);
        return customType(type, customTypeData);
    }
}
