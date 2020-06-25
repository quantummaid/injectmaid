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

public final class FactoryBuilder08<X, A, B, C, D, E, F, G, H>
        extends FactoryBuilder<Factory08<X, A, B, C, D, E, F, G, H>> {

    public FactoryBuilder08(final Builder builder) {
        super(builder);
    }

    public <I> FactoryBuilder09<X, A, B, C, D, E, F, G, H, I> withDependency(final Class<I> type) {
        return withDependency(genericType(type));
    }

    public <I> FactoryBuilder09<X, A, B, C, D, E, F, G, H, I> withDependency(final GenericType<I> type) {
        builder.addParameter(type);
        return new FactoryBuilder09<>(this.builder);
    }
}
