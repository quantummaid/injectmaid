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
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.api.SingletonType;
import de.quantummaid.injectmaid.api.interception.*;
import de.quantummaid.injectmaid.closing.Closer;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.lifecyclemanagement.ExceptionDuringClose;
import de.quantummaid.injectmaid.lifecyclemanagement.LifecycleManager;
import de.quantummaid.injectmaid.timing.InstanceAndTimedDependencies;
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.InstantiationTimes;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.quantummaid.injectmaid.InjectMaidBuilder.injectMaidBuilder;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.ScopeManager.scopeManager;
import static de.quantummaid.injectmaid.ShutdownHook.shutdownHook;
import static de.quantummaid.injectmaid.SingletonStore.singletonStore;
import static de.quantummaid.injectmaid.api.interception.InterceptorFactories.interceptorFactories;
import static de.quantummaid.injectmaid.api.interception.overwrite.OverwritingInterceptor.overwritingInterceptor;
import static de.quantummaid.injectmaid.circledetector.CircularDependencyDetector.validateNoCircularDependencies;
import static de.quantummaid.injectmaid.timing.InstanceAndTimedDependencies.instanceWithNoDependencies;
import static de.quantummaid.injectmaid.timing.TimedInstantiation.timeInstantiation;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class InjectMaid implements Injector {
    private final ReflectMaid reflectMaid;
    private final Definitions definitions;
    private final SingletonType defaultSingletonType;
    private final SingletonStore singletonStore;
    private final Scope scope;
    private final ScopeManager scopeManager;
    private final InterceptorFactories interceptorFactories;
    private final List<InjectMaid> children = new ArrayList<>();
    private final LifecycleManager lifecycleManager;
    private final InjectMaid parent;
    private final InstantiationTimes instantiationTimes;

    public static InjectMaidBuilder anInjectMaid() {
        final ReflectMaid reflectMaid = ReflectMaid.aReflectMaid();
        return anInjectMaid(reflectMaid);
    }

    public static InjectMaidBuilder anInjectMaid(final ReflectMaid reflectMaid) {
        return injectMaidBuilder(reflectMaid);
    }

    static InjectMaid injectMaid(final ReflectMaid reflectMaid,
                                 final Definitions definitions,
                                 final SingletonType defaultSingletonType,
                                 final LifecycleManager lifecycleManager,
                                 final List<InterceptorFactory> preConfiguredInterceptorFactories) {
        validateNoCircularDependencies(definitions);
        final Scope scope = rootScope();
        final ScopeManager scopeManager = scopeManager();
        final InterceptorFactories interceptorFactories = interceptorFactories(preConfiguredInterceptorFactories);
        final InjectMaid injectMaid = new InjectMaid(
                reflectMaid,
                definitions,
                defaultSingletonType,
                singletonStore(),
                scope,
                scopeManager,
                interceptorFactories,
                lifecycleManager,
                null,
                InstantiationTimes.instantiationTimes(reflectMaid)
        );
        injectMaid.loadEagerSingletons();
        return injectMaid;
    }

    @Override
    public void initializeAllSingletons(final Duration enforcedMaxTime) {
        initializeDefinitionsThat(Definition::isSingleton, enforcedMaxTime);
    }

    private void loadEagerSingletons() {
        initializeDefinitionsThat(definition -> definition.isEagerSingleton(defaultSingletonType), null);
    }

    private void initializeDefinitionsThat(final Predicate<Definition> predicate,
                                           final Duration enforcedMaxTime) {
        final Instant before = Instant.now();
        definitions.definitionsOnScope(scope).stream()
                .filter(predicate)
                .forEach(definition -> {
                    final TypeIdentifier type = definition.type();
                    final TimedInstantiation<Object> timedInstantiation = getInstanceWithInitializationTime(type, type);
                    final InstantiationTime time = timedInstantiation.instantiationTime();
                    instantiationTimes.addInitializationTime(type, time);
                });
        final Instant after = Instant.now();
        final Duration duration = Duration.between(before, after);
        if (enforcedMaxTime != null && enforcedMaxTime.compareTo(duration) < 0) {
            throw injectMaidException("" +
                    "initializing all singletons " +
                    "must not take longer than " + enforcedMaxTime.toMillis() + "ms but took " + duration.toMillis() + "ms.\n" +
                    "Individual instantion times:\n" + instantiationTimes.render()
            );
        }
    }

    @Override
    public <T> Injector enterScopeWithTimeout(final GenericType<T> type, final T scopeObject, final Duration enforcedMaxTime) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return enterScopeWithTimeout(resolvedType, scopeObject, enforcedMaxTime);
    }

    @Override
    public Injector enterScopeWithTimeout(final ResolvedType resolvedType, final Object scopeObject, final Duration enforcedMaxTime) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return enterScopeWithTimeout(typeIdentifier, scopeObject, enforcedMaxTime);
    }

    @Override
    public Injector enterScopeWithTimeout(final TypeIdentifier typeIdentifier,
                                          final Object scopeObject,
                                          final Duration enforcedMaxTime) {
        final Instant before = Instant.now();
        final Injector scopedInjector = enterScopeIfExists(typeIdentifier, scopeObject).orElseThrow(() -> {
            final Scope childScope = this.scope.childScope(typeIdentifier);
            final String registeredScopes = definitions.allScopes().stream()
                    .map(Scope::render)
                    .sorted()
                    .collect(joining(", ", "[", "]"));
            throw injectMaidException(format("Tried to enter unknown scope '%s' with object '%s'. " +
                            "Registered scopes: %s",
                    childScope.render(), scopeObject, registeredScopes));
        });
        final Instant after = Instant.now();
        final Duration duration = Duration.between(before, after);
        if (enforcedMaxTime != null && enforcedMaxTime.compareTo(duration) < 0) {
            final InjectMaid scopedInjectMaid = (InjectMaid) scopedInjector;
            throw injectMaidException("" +
                    "entering scope " + typeIdentifier.description() + " " +
                    "must not take longer than " + enforcedMaxTime.toMillis() + "ms but took " + duration.toMillis() + "ms.\n" +
                    "Individual instantion times:\n" + scopedInjectMaid.instantiationTimes.render()
            );
        }
        return scopedInjector;
    }

    @Override
    public <T> Optional<Injector> enterScopeIfExists(final GenericType<T> type, final T scopeObject) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return enterScopeIfExists(resolvedType, scopeObject);
    }

    @Override
    public Optional<Injector> enterScopeIfExists(final ResolvedType resolvedType, final Object scopeObject) {
        final TypeIdentifier typeIdentifier = TypeIdentifier.typeIdentifierFor(resolvedType);
        return enterScopeIfExists(typeIdentifier, scopeObject);
    }

    public Optional<Injector> enterScopeIfExists(final TypeIdentifier typeIdentifier, final Object scopeObject) {
        final Scope childScope = scope.childScope(typeIdentifier);
        final List<Scope> scopes = definitions.allScopes();
        if (!scopes.contains(childScope)) {
            return Optional.empty();
        }
        final SingletonStore childSingletonStore = singletonStore.child(typeIdentifier);
        final ScopeManager childScopeManager = scopeManager.add(typeIdentifier, scopeObject);
        final ScopeEntryInterceptors scopeEntryInterceptors = interceptorFactories.scopeEntryInterceptors();
        final List<InterceptorFactory> childInterceptorFactories = scopeEntryInterceptors.interceptBefore(typeIdentifier, scopeObject);
        final InjectMaid scopedInjectMaid = new InjectMaid(
                reflectMaid,
                definitions,
                defaultSingletonType,
                childSingletonStore,
                childScope,
                childScopeManager,
                interceptorFactories(childInterceptorFactories),
                lifecycleManager.newInstance(childScope),
                this,
                InstantiationTimes.instantiationTimes(reflectMaid)
        );
        children.add(scopedInjectMaid);
        scopedInjectMaid.loadEagerSingletons();
        scopeEntryInterceptors.interceptAfter(typeIdentifier, scopeObject, scopedInjectMaid);
        return Optional.of(scopedInjectMaid);
    }

    @Override
    public void addInterceptor(final SimpleInterceptor interceptor) {
        interceptorFactories.addInterceptor(interceptor);
    }

    @Override
    public void overwriteWith(final Injector injector) {
        final Interceptor interceptor = overwritingInterceptor(injector);
        interceptorFactories.addInterceptor(interceptor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(final TypeIdentifier type) {
        final TimedInstantiation<Object> instanceWithInitializationTime = getInstanceWithInitializationTime(type, type);
        return (T) instanceWithInitializationTime.instance();
    }

    @Override
    public <T> TimedInstantiation<T> getInstanceWithInitializationTime(final GenericType<T> type) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return getInstanceWithInitializationTime(resolvedType);
    }

    public <T> TimedInstantiation<T> getInstanceWithInitializationTime(final ResolvedType type) {
        final TypeIdentifier typeIdentifier = TypeIdentifier.typeIdentifierFor(type);
        return getInstanceWithInitializationTime(typeIdentifier, typeIdentifier);
    }

    @SuppressWarnings("unchecked")
    public <T> TimedInstantiation<T> getInstanceWithInitializationTime(final TypeIdentifier type,
                                                                       final TypeIdentifier rootType) {
        final Interceptors interceptors = interceptorFactories.interceptors();
        final Optional<?> intercepted = interceptors.interceptBefore(type, rootType);
        if (intercepted.isPresent()) {
            return timeInstantiation(type, () -> (InstanceAndTimedDependencies<T>) instanceWithNoDependencies(intercepted.get()));
        }
        final Definition definition = definitions.definitionFor(type, scope);
        final ReusePolicy reusePolicy = definition.reusePolicy();
        final TimedInstantiation<Object> timedInstantiation = internalGetInstance(definition, rootType);
        return (TimedInstantiation<T>) timedInstantiation.modify(instance -> interceptors.interceptAfter(type, rootType, reusePolicy, instance));
    }

    @Override
    public boolean canInstantiate(final GenericType<?> type) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return canInstantiate(resolvedType);
    }

    @Override
    public boolean canInstantiate(final TypeIdentifier type) {
        return definitions.hasDefinitionFor(type, scope);
    }

    public String debugInformation() {
        return definitions.dump();
    }

    private TimedInstantiation<Object> internalGetInstance(final Definition definition, final TypeIdentifier rootType) {
        return createAndRegister(definition, rootType);
    }

    private TimedInstantiation<Object> instantiate(final Definition definition, final TypeIdentifier rootType) {
        final Instantiator instantiator = definition.instantiator();
        return timeInstantiation(definition.type(), () -> {
            final List<TimedInstantiation<?>> timedDependencies = instantiateDependencies(instantiator, rootType);
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

    private List<TimedInstantiation<?>> instantiateDependencies(final Instantiator instantiator, final TypeIdentifier rootType) {
        return instantiator.dependencies().stream()
                .map(dependency -> getInstanceWithInitializationTime(dependency, rootType))
                .collect(toList());
    }

    private TimedInstantiation<Object> createAndRegister(final Definition definition, final TypeIdentifier rootType) {
        final boolean singleton = definition.isSingleton();
        final TypeIdentifier type = definition.type();
        final Scope definitionScope = definition.scope();
        if (singleton && singletonStore.contains(type, definitionScope)) {
            return timeInstantiation(definition.type(),
                    () -> instanceWithNoDependencies(singletonStore.get(type, definitionScope)));
        }
        final TimedInstantiation<Object> instance = instantiate(definition, rootType);
        lifecycleManager.registerInstance(instance.instance(), definitionScope);
        if (singleton) {
            singletonStore.put(type, definitionScope, instance.instance());
        }
        return instance;
    }

    public InstantiationTimes instantiationTimes() {
        return instantiationTimes;
    }

    void registerShutdownHook() {
        final ShutdownHook shutdownHook = shutdownHook(this);
        getRuntime().addShutdownHook(shutdownHook);
        lifecycleManager.registerInstance(shutdownHook, scope);
    }

    public void registerExternalObjectToLifecycleManagement(final Object object) {
        lifecycleManager.registerInstance(object, scope);
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

    @Override
    public ReflectMaid reflectMaid() {
        return reflectMaid;
    }
}
