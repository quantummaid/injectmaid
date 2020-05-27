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

import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.interception.Interceptor;
import de.quantummaid.injectmaid.interception.Interceptors;
import de.quantummaid.injectmaid.interception.SimpleInterceptor;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.quantummaid.injectmaid.InjectMaidBuilder.injectionMaidBuilder;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Scope.rootScope;
import static de.quantummaid.injectmaid.ScopeManager.scopeManager;
import static de.quantummaid.injectmaid.SingletonStore.singletonStore;
import static de.quantummaid.injectmaid.circledetector.CircularDependencyDetector.validateNoCircularDependencies;
import static de.quantummaid.injectmaid.interception.Interceptors.interceptors;
import static de.quantummaid.injectmaid.interception.overwrite.OverwritingInterceptor.overwritingInterceptor;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectMaid {
    private final Definitions definitions;
    private final SingletonStore singletonStore;
    private final Scope scope;
    private final ScopeManager scopeManager;
    private final Interceptors interceptors;

    public static InjectMaidBuilder anInjectMaid() {
        return injectionMaidBuilder();
    }

    static InjectMaid injectMaid(final Definitions definitions) {
        validateNoCircularDependencies(definitions);
        final Scope scope = rootScope();
        final ScopeManager scopeManager = scopeManager();
        final Interceptors interceptors = interceptors();
        return initInScope(definitions, scope, scopeManager, interceptors);
    }

    private static InjectMaid initInScope(final Definitions definitions,
                                          final Scope scope,
                                          final ScopeManager scopeManager,
                                          final Interceptors interceptors) {
        final SingletonStore singletonStore = singletonStore();
        final InjectMaid injectMaid = new InjectMaid(definitions, singletonStore, scope, scopeManager, interceptors);
        injectMaid.loadEagerSingletons();
        return injectMaid;
    }

    private void loadEagerSingletons() {
        this.definitions.definitionsOnScope(scope).stream()
                .filter(Definition::isEagerSingleton)
                .forEach(this::internalGetInstance);
    }

    public <T> InjectMaid enterScope(final Class<T> scopeType, final T scopeObject) {
        final GenericType<T> genericType = genericType(scopeType);
        return enterScope(genericType, scopeObject);
    }

    public <T> InjectMaid enterScope(final GenericType<T> scopeType, final T scopeObject) {
        final ResolvedType resolvedType = scopeType.toResolvedType();
        return enterScope(resolvedType, scopeObject);
    }

    public InjectMaid enterScope(final ResolvedType resolvedType, final Object scopeObject) {
        final Scope childScope = this.scope.childScope(resolvedType);
        final List<Scope> scopes = definitions.allScopes();
        if (!scopes.contains(childScope)) {
            final String registeredScopes = scopes.stream()
                    .map(Scope::render)
                    .sorted()
                    .collect(joining(", ", "[", "]"));
            throw injectMaidException(format("Tried to enter unknown scope '%s' with object '%s'. Registered scopes: %s",
                    childScope.render(), scopeObject, registeredScopes));
        }
        final SingletonStore childSingletonStore = this.singletonStore.child(resolvedType);
        final ScopeManager childScopeManager = scopeManager.add(resolvedType, scopeObject);
        final Interceptors childInterceptors = interceptors.enterScope(resolvedType, scopeObject);
        return new InjectMaid(definitions, childSingletonStore, childScope, childScopeManager, childInterceptors);
    }

    public void addInterceptor(final SimpleInterceptor interceptor) {
        interceptors.addInterceptor(interceptor);
    }

    public void overwriteWith(final InjectMaid injectMaid) {
        final Interceptor interceptor = overwritingInterceptor(injectMaid);
        interceptors.addInterceptor(interceptor);
    }

    public <T> T getInstance(final Class<T> type) {
        final GenericType<T> genericType = genericType(type);
        return getInstance(genericType);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final GenericType<T> genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return (T) getInstance(resolvedType);
    }

    public Object getInstance(final ResolvedType type) {
        final Optional<?> intercepted = interceptors.interceptBefore(type);
        if (intercepted.isPresent()) {
            return intercepted.get();
        }
        final Definition definition = definitions.definitionFor(type, scope);
        final Object instance = internalGetInstance(definition);
        return interceptors.interceptAfter(type, instance);
    }

    public boolean canInstantiate(final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return canInstantiate(genericType);
    }

    public boolean canInstantiate(final GenericType<?> type) {
        final ResolvedType resolvedType = type.toResolvedType();
        return canInstantiate(resolvedType);
    }

    public boolean canInstantiate(final ResolvedType resolvedType) {
        return definitions.hasDefinitionFor(resolvedType, scope);
    }

    public String debugInformation() {
        return definitions.dump();
    }

    private Object internalGetInstance(final Definition definition) {
        return createAndRegister(definition, () -> {
            final Instantiator instantiator = definition.instantiator();
            final List<Object> dependencies = instantiator.dependencies().stream()
                    .map(this::getInstance)
                    .collect(toList());
            try {
                return instantiator.instantiate(dependencies, scopeManager);
            } catch (final Exception e) {
                throw injectMaidException(format(
                        "Exception during instantiation of '%s' using %s",
                        definition.type().simpleDescription(), instantiator.description()), e);
            }
        });
    }

    private Object createAndRegister(final Definition definition,
                                     final Supplier<Object> instantiator) {
        final boolean singleton = definition.isSingleton();
        final ResolvedType type = definition.type();
        final Scope definitionScope = definition.scope();
        if (singleton && singletonStore.contains(type, definitionScope)) {
            return singletonStore.get(type, definitionScope);
        }
        final Object instance = instantiator.get();
        if (singleton) {
            singletonStore.put(type, definitionScope, instance);
        }
        return instance;
    }
}
