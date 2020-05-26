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
public final class FactoryBuilder00<X> {
    private final Builder builder;

    public <A> FactoryBuilder01<X, A> withDependency(final Class<A> type) {
        return withDependency(genericType(type));
    }

    public <A> FactoryBuilder01<X, A> withDependency(final GenericType<A> type) {
        builder.addParameter(type);
        return new FactoryBuilder01<>(this.builder);
    }

    public CustomType usingFactory(final Factory00<X> factory) {
        builder.setFactory(factory);
        return builder.build();
    }
}
