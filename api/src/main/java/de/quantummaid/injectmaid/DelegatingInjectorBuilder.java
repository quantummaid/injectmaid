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

package de.quantummaid.injectmaid;

import de.quantummaid.injectmaid.customtype.CustomType;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DelegatingInjectorBuilder implements InjectorBuilder {
    private final AbstractInjectorBuilder<?> delegate;

    public static InjectorBuilder delegatingInjectorBuilder(final AbstractInjectorBuilder<?> delegate) {
        return new DelegatingInjectorBuilder(delegate);
    }

    @Override
    public InjectorBuilder withConstant(final ResolvedType resolvedType,
                                                  final Object instance) {
        delegate.withConstant(resolvedType, instance);
        return this;
    }

    @Override
    public InjectorBuilder withCustomType(final CustomType customType,
                                                    final ReusePolicy reusePolicy) {
        delegate.withCustomType(customType, reusePolicy);
        return this;
    }

    @Override
    public InjectorBuilder withFactory(final ResolvedType type,
                                                 final ResolvedType factory,
                                                 final ReusePolicy reusePolicy) {
        delegate.withFactory(type, factory, reusePolicy);
        return this;
    }

    @Override
    public InjectorBuilder withImplementation(final ResolvedType type,
                                                        final ResolvedType implementation,
                                                        final ReusePolicy reusePolicy) {
        delegate.withImplementation(type, implementation, reusePolicy);
        return this;
    }

    @Override
    public InjectorBuilder withScope(final ResolvedType scopeType,
                                               final InjectorConfiguration configuration) {
        delegate.withScope(scopeType, configuration);
        return this;
    }

    @Override
    public InjectorBuilder usingDefaultSingletonType(final SingletonType singletonType) {
        delegate.usingDefaultSingletonType(singletonType);
        return this;
    }

    @Override
    public InjectorBuilder withType(final ResolvedType resolvedType,
                                              final ReusePolicy reusePolicy) {
        delegate.withType(resolvedType, reusePolicy);
        return this;
    }
}
