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

import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.api.SingletonType;
import de.quantummaid.injectmaid.api.interception.Interceptor;
import de.quantummaid.injectmaid.api.interception.Interceptors;
import de.quantummaid.injectmaid.api.interception.SimpleInterceptor;
import de.quantummaid.injectmaid.closing.Closer;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.lifecyclemanagement.ExceptionDuringClose;
import de.quantummaid.injectmaid.lifecyclemanagement.LifecycleManager;
import de.quantummaid.injectmaid.timing.InitializationTimes;
import de.quantummaid.injectmaid.timing.InstanceAndTimedDependencies;
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.quantummaid.injectmaid.InjectMaidBuilder.injectionMaidBuilder;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Scope.rootScope;
import static de.quantummaid.injectmaid.ScopeManager.scopeManager;
import static de.quantummaid.injectmaid.SingletonStore.singletonStore;
import static de.quantummaid.injectmaid.api.interception.Interceptors.interceptors;
import static de.quantummaid.injectmaid.api.interception.overwrite.OverwritingInterceptor.overwritingInterceptor;
import static de.quantummaid.injectmaid.circledetector.CircularDependencyDetector.validateNoCircularDependencies;
import static de.quantummaid.injectmaid.timing.InstanceAndTimedDependencies.instanceWithNoDependencies;
import static de.quantummaid.injectmaid.timing.TimedInstantiation.timeInstantiation;
import static de.quantummaid.reflectmaid.GenericType.fromResolvedType;
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
    private final InjectMaid parent;
    private final InitializationTimes initializationTimes = InitializationTimes.initializationTimes();

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
                lifecycleManager,
                null
        );
    }

    private static InjectMaid initInScope(final Definitions definitions,
                                          final SingletonType defaultSingletonType,
                                          final Scope scope,
                                          final ScopeManager scopeManager,
                                          final Interceptors interceptors,
                                          final LifecycleManager lifecycleManager,
                                          final InjectMaid parent) {
        final SingletonStore singletonStore = singletonStore();
        final InjectMaid injectMaid = new InjectMaid(
                definitions,
                defaultSingletonType,
                singletonStore,
                scope,
                scopeManager,
                interceptors,
                lifecycleManager,
                parent
        );
        injectMaid.loadEagerSingletons();
        return injectMaid;
    }

    @Override
    public void initializeAllSingletons() {
        initializeDefinitionsThat(Definition::isSingleton);
    }

    private void loadEagerSingletons() {
        initializeDefinitionsThat(definition -> definition.isEagerSingleton(defaultSingletonType));
    }

    private void initializeDefinitionsThat(final Predicate<Definition> predicate) {
        this.definitions.definitionsOnScope(scope).stream()
                .filter(predicate)
                .forEach(definition -> {
                    final TimedInstantiation<Object> timedInstantiation = internalGetInstance(definition);
                    final InstantiationTime time = timedInstantiation.instantiationTime();
                    final ResolvedType type = definition.type();
                    initializationTimes.addInitializationTime(fromResolvedType(type), time);
                });
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
                lifecycleManager.newInstance(),
                this
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
    public TimedInstantiation<Object> getInstanceWithInitializationTime(final ResolvedType type) {
        final Optional<?> intercepted = interceptors.interceptBefore(type);
        if (intercepted.isPresent()) {
            return timeInstantiation(fromResolvedType(type), () -> instanceWithNoDependencies(intercepted.get()));
        }
        final Definition definition = definitions.definitionFor(type, scope);
        final TimedInstantiation<Object> timedInstantiation = internalGetInstance(definition);
        return timedInstantiation.modify(instance -> {
            final Object interceptedInstance = interceptors.interceptAfter(type, instance);
            lifecycleManager.registerInstance(interceptedInstance);
            return interceptedInstance;
        });
    }

    @Override
    public boolean canInstantiate(final ResolvedType resolvedType) {
        return definitions.hasDefinitionFor(resolvedType, scope);
    }

    public String debugInformation() {
        return definitions.dump();
    }

    private TimedInstantiation<Object> internalGetInstance(final Definition definition) {
        return createAndRegister(definition);
    }

    private TimedInstantiation<Object> instantiate(final Definition definition) {
        final Instantiator instantiator = definition.instantiator();
        return timeInstantiation(fromResolvedType(definition.type()), () -> {
            final List<TimedInstantiation<?>> timedDependencies = instantiateDependencies(instantiator);
            final List<Object> dependencies = timedDependencies.stream()
                    .map(TimedInstantiation::instance)
                    .collect(toList());
            final List<InstantiationTime> dependenciesInstantiationTimes = timedDependencies.stream()
                    .map(TimedInstantiation::instantiationTime)
                    .collect(toList());
            try {
                final Object instance = instantiator.instantiate(dependencies, scopeManager, this);
                return InstanceAndTimedDependencies.instanceAndTimedDependencies(instance, dependenciesInstantiationTimes);
            } catch (final Exception e) {
                throw injectMaidException(format("Exception during instantiation of '%s' using %s",
                        definition.type().simpleDescription(), instantiator.description()), e);
            }
        });
    }

    private List<TimedInstantiation<?>> instantiateDependencies(final Instantiator instantiator) {
        return instantiator.dependencies().stream()
                .map(this::getInstanceWithInitializationTime)
                .collect(toList());
    }

    private TimedInstantiation<Object> createAndRegister(final Definition definition) {
        final boolean singleton = definition.isSingleton();
        final ResolvedType type = definition.type();
        final Scope definitionScope = definition.scope();
        if (singleton && singletonStore.contains(type, definitionScope)) {
            return timeInstantiation(fromResolvedType(definition.type()),
                    () -> instanceWithNoDependencies(singletonStore.get(type, definitionScope)));
        }
        final TimedInstantiation<Object> instance = instantiate(definition);
        if (singleton) {
            singletonStore.put(type, definitionScope, instance.instance());
        }
        return instance;
    }

    public InitializationTimes initializationTimes() {
        return initializationTimes;
    }

    @Override
    public void close() {
        Closer.close(this::close);
    }

    private void close(final List<ExceptionDuringClose> exceptions) {
        final List<InjectMaid> childrenToClose = new ArrayList<>(children);
        childrenToClose.forEach(injectMaid -> injectMaid.close(exceptions));
        lifecycleManager.closeAll(exceptions);
        if (parent != null) {
            parent.children.remove(this);
        }
    }
}
