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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.injectmaid.api.customtype.api.Builder.builder;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomType {
    private final GenericType<?> resolvedType;
    private final CustomTypeData customTypeData;

    static CustomType customType(final GenericType<?> resolvedType,
                                 final CustomTypeData customTypeData) {
        return new CustomType(resolvedType, customTypeData);
    }

    public static <X> FactoryBuilder00<X> customType(final Class<X> type) {
        final GenericType<X> genericType = genericType(type);
        return customType(genericType);
    }

    public static <X> FactoryBuilder00<X> customType(final GenericType<X> genericType) {
        final Builder builder = builder(genericType);
        return new FactoryBuilder00<>(builder);
    }

    public GenericType<?> type() {
        return resolvedType;
    }

    public CustomTypeData instantiator() {
        return customTypeData;
    }
}
