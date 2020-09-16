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
import de.quantummaid.injectmaid.lifecyclemanagement.ExceptionDuringClose;
import de.quantummaid.injectmaid.lifecyclemanagement.LifecycleManager;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

import static de.quantummaid.injectmaid.InjectMaidBuilder.injectionMaidBuilder;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Scope.rootScope;
import static de.quantummaid.injectmaid.ScopeManager.scopeManager;
import static de.quantummaid.injectmaid.SingletonStore.singletonStore;
import static de.quantummaid.injectmaid.circledetector.CircularDependencyDetector.validateNoCircularDependencies;
import static de.quantummaid.injectmaid.interception.Interceptors.interceptors;
import static de.quantummaid.injectmaid.interception.overwrite.OverwritingInterceptor.overwritingInterceptor;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class InjectMaid implements Injector {
    private final Definitions definitions;
    private final SingletonType defaultSingletonType;
    private final SingletonStore singletonStore;
    private final Scope scope;
    private final ScopeManager scopeManager;
    private final Interceptors interceptors;
    private final List<InjectMaid> children = new ArrayList<>();
    private final LifecycleManager lifecycleManager;

    public static InjectMaidBuilder anInjectMaid() {
        return injectionMaidBuilder();
    }

    static InjectMaid injectMaid(final Definitions definitions,
                                 final SingletonType defaultSingletonType,
                                 final LifecycleManager lifecycleManager) {
        validateNoCircularDependencies(definitions);
        final Scope scope = rootScope();
        final ScopeManager scopeManager = scopeManager();
        final Interceptors interceptors = interceptors();
        return initInScope(
                definitions,
                defaultSingletonType,
                scope,
                scopeManager,
                interceptors,
                lifecycleManager
        );
    }

    private static InjectMaid initInScope(final Definitions definitions,
                                          final SingletonType defaultSingletonType,
                                          final Scope scope,
                                          final ScopeManager scopeManager,
                                          final Interceptors interceptors,
                                          final LifecycleManager lifecycleManager) {
        final SingletonStore singletonStore = singletonStore();
        final InjectMaid injectMaid = new InjectMaid(
                definitions,
                defaultSingletonType,
                singletonStore,
                scope,
                scopeManager,
                interceptors,
                lifecycleManager
        );
        injectMaid.loadEagerSingletons();
        return injectMaid;
    }

    @Override
    public void initializeAllSingletons() {
        this.definitions.definitionsOnScope(scope).stream()
                .filter(Definition::isSingleton)
                .forEach(this::internalGetInstance);
    }

    private void loadEagerSingletons() {
        this.definitions.definitionsOnScope(scope).stream()
                .filter(this::isEagerSingleton)
                .forEach(this::internalGetInstance);
    }

    private boolean isEagerSingleton(final Definition definition) {
        final ReusePolicy reusePolicy = definition.reusePolicy();
        if (reusePolicy == ReusePolicy.SINGLETON) {
            return defaultSingletonType == SingletonType.EAGER;
        }
        return reusePolicy == ReusePolicy.EAGER_SINGLETON;
    }

    @Override
    public InjectMaid enterScope(final ResolvedType resolvedType, final Object scopeObject) {
        final Scope childScope = this.scope.childScope(resolvedType);
        final List<Scope> scopes = definitions.allScopes();
        if (!scopes.contains(childScope)) {
            final String registeredScopes = scopes.stream()
                    .map(Scope::render)
                    .sorted()
                    .collect(joining(", ", "[", "]"));
            throw injectMaidException(format("Tried to enter unknown scope '%s' with object '%s'. " +
                            "Registered scopes: %s",
                    childScope.render(), scopeObject, registeredScopes));
        }
        final SingletonStore childSingletonStore = this.singletonStore.child(resolvedType);
        final ScopeManager childScopeManager = scopeManager.add(resolvedType, scopeObject);
        final Interceptors childInterceptors = interceptors.enterScope(resolvedType, scopeObject);
        final InjectMaid scopedInjectMaid = new InjectMaid(
                definitions,
                defaultSingletonType,
                childSingletonStore,
                childScope,
                childScopeManager,
                childInterceptors,
                lifecycleManager.newInstance()
        );
        children.add(scopedInjectMaid);
        return scopedInjectMaid;
    }

    @Override
    public void addInterceptor(final SimpleInterceptor interceptor) {
        interceptors.addInterceptor(interceptor);
    }

    @Override
    public void overwriteWith(final Injector injector) {
        final Interceptor interceptor = overwritingInterceptor(injector);
        interceptors.addInterceptor(interceptor);
    }

    @Override
    public Object getInstance(final ResolvedType type) {
        final Optional<?> intercepted = interceptors.interceptBefore(type);
        if (intercepted.isPresent()) {
            return intercepted.get();
        }
        final Definition definition = definitions.definitionFor(type, scope);
        final Object instance = internalGetInstance(definition);
        final Object interceptedInstance = interceptors.interceptAfter(type, instance);
        lifecycleManager.registerInstance(interceptedInstance);
        return interceptedInstance;
    }

    @Override
    public boolean canInstantiate(final ResolvedType resolvedType) {
        return definitions.hasDefinitionFor(resolvedType, scope);
    }

    public String debugInformation() {
        return definitions.dump();
    }

    private Object internalGetInstance(final Definition definition) {
        return createAndRegister(definition, () -> instantiate(definition));
    }

    private Object instantiate(final Definition definition) {
        final Instantiator instantiator = definition.instantiator();
        final List<Object> dependencies = instantiateDependencies(instantiator);
        try {
            return instantiator.instantiate(dependencies, scopeManager, this);
        } catch (final Exception e) {
            throw injectMaidException(format("Exception during instantiation of '%s' using %s",
                    definition.type().simpleDescription(), instantiator.description()), e);
        }
    }

    private List<Object> instantiateDependencies(final Instantiator instantiator) {
        return instantiator.dependencies().stream()
                .map(this::getInstance)
                .collect(toList());
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

    @Override
    public void close() {
        final List<ExceptionDuringClose> exceptions = new ArrayList<>();
        close(exceptions);
        if (!exceptions.isEmpty()) {
            final StringJoiner stringJoiner = new StringJoiner("\n", "exception(s) during close:\n", "");
            exceptions.forEach(exceptionDuringClose -> stringJoiner.add(exceptionDuringClose.buildMessage()));
            final InjectMaidException exception = injectMaidException(stringJoiner.toString());
            exceptions.forEach(exceptionDuringClose -> exception.addSuppressed(exceptionDuringClose.exception()));
            throw exception;
        }
    }

    private void close(final List<ExceptionDuringClose> exceptions) {
        children.forEach(injectMaid -> injectMaid.close(exceptions));
        lifecycleManager.closeAll(exceptions);
    }
}
