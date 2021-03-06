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

import de.quantummaid.injectmaid.namespaces.NamespacedType;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.quantummaid.injectmaid.api.customtype.api.CustomType.customType;
import static de.quantummaid.injectmaid.api.customtype.api.CustomTypeData.customTypeInstantiator;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder {
    private final GenericType<?> type;
    private final List<GenericType<?>> dependencies;
    private InvocableFactory<?> factory;
    private final GenericType<?> namespace;

    public static Builder builder(final GenericType<?> type) {
        return builder(type, null);
    }

    public static Builder builder(final GenericType<?> type, final GenericType<?> namespace) {
        return new Builder(type, new ArrayList<>(), namespace);
    }

    public void addParameter(final GenericType<?> parameter) {
        dependencies.add(parameter);
    }

    public void setFactory(final InvocableFactory<?> factory) {
        this.factory = factory;
    }

    @SuppressWarnings("rawtypes")
    CustomType build() {
        final CustomTypeData customTypeData;
        if (namespace != null) {
            final List<GenericType<?>> namespacedDependencies = dependencies.stream()
                    .map(genericType -> genericType(NamespacedType.class, genericType, namespace))
                    .collect(toList());
            final InvocableFactory<?> namespacedFactory = namespaced -> {
                final Object[] unnamespaced = Arrays.stream(namespaced)
                        .map(NamespacedType.class::cast)
                        .map(NamespacedType::dependency)
                        .toArray();
                final Object originalResult = factory.invoke(unnamespaced);
                return new NamespacedType<>(originalResult);
            };
            customTypeData = customTypeInstantiator(namespacedDependencies, namespacedFactory);
        } else {
            customTypeData = customTypeInstantiator(dependencies, factory);
        }
        return customType(type, customTypeData);
    }
}
