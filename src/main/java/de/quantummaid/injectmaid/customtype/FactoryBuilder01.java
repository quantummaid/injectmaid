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

import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class FactoryBuilder01<X, A> extends FactoryBuilder<Factory01<X, A>> {

    public FactoryBuilder01(final Builder builder) {
        super(builder);
    }

    public <B> FactoryBuilder02<X, A, B> withDependency(final Class<B> type) {
        return withDependency(genericType(type));
    }

    public <B> FactoryBuilder02<X, A, B> withDependency(final GenericType<B> type) {
        builder.addParameter(type);
        return new FactoryBuilder02<>(this.builder);
    }
}
