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

package de.quantummaid.injectmaid.api.customtype;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.ScopeManager;
import de.quantummaid.injectmaid.api.customtype.api.InvocableFactory;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomTypeInstantiator implements Instantiator {
    private final List<ResolvedType> dependencies;
    private final InvocableFactory<?> invocableFactory;

    public static CustomTypeInstantiator customTypeInstantiator(final List<ResolvedType> dependencies,
                                                                final InvocableFactory<?> invocableFactory) {
        return new CustomTypeInstantiator(dependencies, invocableFactory);
    }

    @Override
    public List<ResolvedType> dependencies() {
        return dependencies;
    }

    @Override
    public Object instantiate(final List<Object> dependencies,
                              final ScopeManager scopeManager,
                              final InjectMaid injectMaid) {
        return invocableFactory.invoke(dependencies.toArray());
    }

    @Override
    public String description() {
        return format("custom instantiation via '%s'", invocableFactory);
    }
}
