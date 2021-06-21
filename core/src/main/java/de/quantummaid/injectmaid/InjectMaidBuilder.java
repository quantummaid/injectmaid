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

import de.quantummaid.injectmaid.api.AbstractInjectorBuilder;
import de.quantummaid.injectmaid.api.InjectorConfiguration;
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.api.SingletonType;
import de.quantummaid.injectmaid.api.customtype.CustomTypeInstantiator;
import de.quantummaid.injectmaid.api.customtype.api.CustomType;
import de.quantummaid.injectmaid.api.customtype.api.CustomTypeData;
import de.quantummaid.injectmaid.instantiator.BindInstantiator;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.lifecyclemanagement.LifecycleManager;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.CloseFunction;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closer;
import de.quantummaid.injectmaid.statemachine.*;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.OnCollectionError;
import de.quantummaid.reflectmaid.typescanner.Processor;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactories;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import de.quantummaid.reflectmaid.typescanner.factories.UndetectedFactory;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;
import de.quantummaid.reflectmaid.typescanner.states.RequirementsDescriber;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.Definitions.definitions;
import static de.quantummaid.injectmaid.InjectMaid.injectMaid;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Requirements.REGISTERED;
import static de.quantummaid.injectmaid.Scopes.scopes;
import static de.quantummaid.injectmaid.api.ReusePolicy.PROTOTYPE;
import static de.quantummaid.injectmaid.api.customtype.CustomTypeInstantiator.customTypeInstantiator;
import static de.quantummaid.injectmaid.instantiator.BindInstantiator.bindInstantiator;
import static de.quantummaid.injectmaid.instantiator.CustomInstantiatorFactory.customInstantiatorFactory;
import static de.quantummaid.injectmaid.instantiator.ScopeInstantiator.scopeInstantiator;
import static de.quantummaid.injectmaid.lifecyclemanagement.NoOpLifecycleManager.noOpLifecycleManager;
import static de.quantummaid.injectmaid.lifecyclemanagement.RealLifecycleManager.realLifecycleManager;
import static de.quantummaid.injectmaid.lifecyclemanagement.closer.Closer.closer;
import static de.quantummaid.injectmaid.lifecyclemanagement.closer.Closers.closers;
import static de.quantummaid.injectmaid.statemachine.FactoryMapper.factoryMapper;
import static de.quantummaid.injectmaid.statemachine.InjectMaidDetector.injectMaidDetector;
import static de.quantummaid.injectmaid.statemachine.InjectMaidOnCollectionError.injectMaidOnCollectionError;
import static de.quantummaid.injectmaid.statemachine.InjectMaidResolver.injectMaidResolver;
import static de.quantummaid.injectmaid.statemachine.ReusePolicyMapper.reusePolicyMapper;
import static de.quantummaid.reflectmaid.typescanner.Processor.processor;
import static de.quantummaid.reflectmaid.typescanner.Reason.manuallyAdded;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;
import static de.quantummaid.reflectmaid.typescanner.signals.AddReasonSignal.addReasonSignal;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class InjectMaidBuilder implements AbstractInjectorBuilder<InjectMaidBuilder> {
    private static final ReusePolicy DEFAULT_REUSE_POLICY = PROTOTYPE;

    private final ReflectMaid reflectMaid;
    private boolean registerShutdownHook = false;
    private final List<Signal<InjectMaidTypeScannerResult>> signals;
    private final Map<Scope, List<StateFactory<InjectMaidTypeScannerResult>>> stateFactoryMap;
    private final FactoryMapper factoryMapper;
    private final ReusePolicyMapper reusePolicyMapper;
    private final Scope scope;
    private final Scopes scopes;
    private SingletonType defaultSingletonType = SingletonType.LAZY;
    private boolean lifecycleManagement = false;
    private final List<Closer> closers = new ArrayList<>();

    static InjectMaidBuilder injectMaidBuilder(final ReflectMaid reflectMaid) {
        final Scope scope = rootScope();
        final Scopes scopes = scopes();
        scopes.add(scope);
        final Map<Scope, List<StateFactory<InjectMaidTypeScannerResult>>> stateFactoryMap = new LinkedHashMap<>();
        final List<Signal<InjectMaidTypeScannerResult>> signals = new ArrayList<>();
        final FactoryMapper factoryMapper = factoryMapper();
        final ReusePolicyMapper reusePolicyMapper = reusePolicyMapper(DEFAULT_REUSE_POLICY);
        return new InjectMaidBuilder(reflectMaid, signals, stateFactoryMap, factoryMapper, reusePolicyMapper, scope, scopes);
    }

    public InjectMaidBuilder withConfiguration(final InjectorConfiguration configuration) {
        configuration.apply(this);
        return this;
    }

    @Override
    public InjectMaidBuilder withScope(final GenericType<?> scopeType,
                                       final InjectorConfiguration configuration) {
        final ResolvedType resolvedScopeType = reflectMaid.resolve(scopeType);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedScopeType);
        return withScope(typeIdentifier, configuration);
    }

    public InjectMaidBuilder withScope(final TypeIdentifier scopeType,
                                       final InjectorConfiguration configuration) {
        final Scope subScope = scope.childScope(scopeType);
        if (!scopes.contains(subScope)) {
            scopes.validateElementNotUsedSomewhereElse(scopeType);
        }
        final InjectMaidBuilder scopedBuilder = new InjectMaidBuilder(
                reflectMaid, signals, stateFactoryMap, factoryMapper, reusePolicyMapper, subScope, scopes);
        scopedBuilder.lifecycleManagement = lifecycleManagement;
        if (!scopes.contains(subScope)) {
            scopedBuilder.withInstantiator(scopeType, scopeInstantiator(scopeType), DEFAULT_REUSE_POLICY);
        }
        scopes.add(subScope);
        configuration.apply(scopedBuilder);
        return this;
    }

    public InjectMaidBuilder withLifecycleManagement() {
        this.lifecycleManagement = true;
        return this;
    }

    @Override
    public InjectMaidBuilder withFactory(final GenericType<?> type,
                                         final GenericType<?> factory,
                                         final ReusePolicy reusePolicy) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        final ResolvedType resolvedFactory = reflectMaid.resolve(factory);
        factoryMapper.registerFactory(typeIdentifier, resolvedFactory);
        return withType(resolvedType, reusePolicy);
    }

    @Override
    public <X> InjectMaidBuilder withImplementation(final GenericType<X> interfaceType,
                                                    final GenericType<? extends X> implementationType,
                                                    final ReusePolicy reusePolicy) {
        final ResolvedType resolvedInterfaceType = reflectMaid.resolve(interfaceType);
        final ResolvedType resolvedImplementationType = reflectMaid.resolve(implementationType);
        final BindInstantiator instantiator = bindInstantiator(typeIdentifierFor(resolvedImplementationType));
        withInstantiator(typeIdentifierFor(resolvedInterfaceType), instantiator, DEFAULT_REUSE_POLICY);
        return withType(typeIdentifierFor(resolvedImplementationType), reusePolicy);
    }

    @Override
    public InjectMaidBuilder withType(final GenericType<?> type,
                                      final ReusePolicy reusePolicy) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return withType(resolvedType, reusePolicy);
    }

    public InjectMaidBuilder withType(final ResolvedType type,
                                      final ReusePolicy reusePolicy) {
        final TypeIdentifier typeIdentifier = TypeIdentifier.typeIdentifierFor(type);
        return withType(typeIdentifier, reusePolicy);
    }

    private InjectMaidBuilder withType(final TypeIdentifier type,
                                       final ReusePolicy reusePolicy) {
        reusePolicyMapper.registerReusePolicy(type, scope, reusePolicy);
        signals.add(addReasonSignal(type, scope, REGISTERED, manuallyAdded()));
        return this;
    }

    @Override
    public InjectMaidBuilder withCustomType(final CustomType customType,
                                            final ReusePolicy reusePolicy) {
        final GenericType<?> type = customType.type();
        final CustomTypeData customTypeData = customType.instantiator();
        final List<TypeIdentifier> dependencies = customTypeData.dependencies().stream()
                .map(reflectMaid::resolve)
                .map(TypeIdentifier::typeIdentifierFor)
                .collect(toList());
        final CustomTypeInstantiator instantiator = customTypeInstantiator(
                dependencies,
                customTypeData.invocableFactory()
        );
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return withInstantiator(resolvedType, instantiator, reusePolicy);
    }

    @Override
    public InjectMaidBuilder usingDefaultSingletonType(final SingletonType singletonType) {
        defaultSingletonType = singletonType;
        return this;
    }

    public InjectMaidBuilder withInstantiator(final ResolvedType resolvedType,
                                              final Instantiator instantiator,
                                              final ReusePolicy reusePolicy) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return withInstantiator(typeIdentifier, instantiator, reusePolicy);
    }

    public InjectMaidBuilder withInstantiator(final TypeIdentifier typeIdentifier,
                                              final Instantiator instantiator,
                                              final ReusePolicy reusePolicy) {
        withStateFactory(customInstantiatorFactory(typeIdentifier, instantiator, reusePolicyMapper));
        return withType(typeIdentifier, reusePolicy);
    }

    public InjectMaidBuilder withStateFactory(final StateFactory<InjectMaidTypeScannerResult> stateFactory) {
        final List<StateFactory<InjectMaidTypeScannerResult>> list = stateFactoryMap.computeIfAbsent(scope, x -> new ArrayList<>());
        list.add(stateFactory);
        return this;
    }

    public <T> InjectMaidBuilder closingInstancesOfType(final Class<T> type,
                                                        final CloseFunction<T> closeFunction) {
        closers.add(Closer.closer(type, closeFunction));
        return this;
    }

    public InjectMaidBuilder closeOnJvmShutdown() {
        registerShutdownHook = true;
        return this;
    }

    public ReflectMaid reflectMaid() {
        return reflectMaid;
    }

    public InjectMaid build() {
        final StateFactories<InjectMaidTypeScannerResult> stateFactories = new StateFactories<>(stateFactoryMap, new UndetectedFactory<>());
        final Processor<InjectMaidTypeScannerResult> processor = processor(
                stateFactories,
                List.of(REGISTERED),
                emptyList()
        );
        final InjectMaidDetector detector = injectMaidDetector(factoryMapper, reusePolicyMapper);
        final InjectMaidResolver resolver = injectMaidResolver();
        final OnCollectionError<InjectMaidTypeScannerResult> onCollectionError = injectMaidOnCollectionError();
        final RequirementsDescriber requirementsDescriber = detectionRequirements -> "registered";
        signals.forEach(processor::dispatch);
        final Map<TypeIdentifier, Map<Scope, CollectionResult<InjectMaidTypeScannerResult>>> definitionsMap =
                processor.collect(detector, resolver, onCollectionError, requirementsDescriber);

        final Definitions definitions = definitions(scopes.asList(), definitionsMap);
        final LifecycleManager lifecycleManager;
        if (lifecycleManagement || !closers.isEmpty()) {
            closers.add(closer(AutoCloseable.class, AutoCloseable::close));
            lifecycleManager = realLifecycleManager(closers(this.closers), scope);
        } else {
            lifecycleManager = noOpLifecycleManager();
        }
        final InjectMaid injectMaid = injectMaid(reflectMaid, definitions, defaultSingletonType, lifecycleManager);
        if (registerShutdownHook) {
            if (!lifecycleManagement) {
                throw injectMaidException("can only close on JVM shutdown if lifecycle management is activated");
            }
            injectMaid.registerShutdownHook();
        }
        return injectMaid;
    }
}
