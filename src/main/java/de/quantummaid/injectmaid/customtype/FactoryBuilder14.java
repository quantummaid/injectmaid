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

package de.quantummaid.injectmaid.customtype;

import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class FactoryBuilder14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
    private final Builder builder;

    public <O> FactoryBuilder15<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> withDependency(final Class<O> type) {
        return withDependency(genericType(type));
    }

    public <O> FactoryBuilder15<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> withDependency(final GenericType<O> type) {
        builder.addParameter(type);
        return new FactoryBuilder15<>(this.builder);
    }

    public CustomType usingFactory(final Factory14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> factory) {
        builder.setFactory(factory);
        return builder.build();
    }
}
