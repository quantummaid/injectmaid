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
import de.quantummaid.injectmaid.instantiator.ScopeInstantiator;
import de.quantummaid.injectmaid.lifecyclemanagement.LifecycleManager;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.CloseFunction;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closer;
import de.quantummaid.injectmaid.lifecyclemanagement.closer.Closers;
import de.quantummaid.injectmaid.statemachine.Context;
import de.quantummaid.injectmaid.statemachine.States;
import de.quantummaid.injectmaid.statemachine.states.ResolvingDependencies;
import de.quantummaid.injectmaid.statemachine.states.State;
import de.quantummaid.injectmaid.statemachine.states.UnresolvedFactory;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.Definitions.definitions;
import static de.quantummaid.injectmaid.InjectMaid.injectMaid;
import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.Scope.rootScope;
import static de.quantummaid.injectmaid.Scopes.scopes;
import static de.quantummaid.injectmaid.api.ReusePolicy.PROTOTYPE;
import static de.quantummaid.injectmaid.api.customtype.CustomTypeInstantiator.customTypeInstantiator;
import static de.quantummaid.injectmaid.instantiator.BindInstantiator.bindInstantiator;
import static de.quantummaid.injectmaid.instantiator.ScopeInstantiator.scopeInstantiator;
import static de.quantummaid.injectmaid.lifecyclemanagement.NoOpLifecycleManager.noOpLifecycleManager;
import static de.quantummaid.injectmaid.lifecyclemanagement.RealLifecycleManager.realLifecycleManager;
import static de.quantummaid.injectmaid.lifecyclemanagement.closer.Closer.closer;
import static de.quantummaid.injectmaid.statemachine.Context.context;
import static de.quantummaid.injectmaid.statemachine.StateMachineRunner.runStateMachine;
import static de.quantummaid.injectmaid.statemachine.States.states;
import static de.quantummaid.injectmaid.statemachine.states.ResolvingDependencies.resolvingDependencies;
import static de.quantummaid.injectmaid.statemachine.states.Unresolved.unresolved;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class InjectMaidBuilder implements AbstractInjectorBuilder<InjectMaidBuilder> {
    private static final ReusePolicy DEFAULT_REUSE_POLICY = PROTOTYPE;

    private boolean registerShutdownHook = false;
    private final States states;
    private final Scope scope;
    private final Scopes scopes;
    private SingletonType defaultSingletonType = SingletonType.LAZY;
    private boolean lifecycleManagement = false;
    private final List<Closer> closers = new ArrayList<>();

    static InjectMaidBuilder injectionMaidBuilder() {
        final States states = states();
        final Scope scope = rootScope();
        final Scopes scopes = scopes();
        scopes.add(scope);
        return new InjectMaidBuilder(states, scope, scopes);
    }

    public InjectMaidBuilder withConfiguration(final InjectorConfiguration configuration) {
        configuration.apply(this);
        return this;
    }

    @Override
    public InjectMaidBuilder withScope(final ResolvedType scopeType,
                                       final InjectorConfiguration configuration) {
        scopes.validateElementNotUsedSomewhereElse(scopeType, scope);
        final Scope subScope = this.scope.childScope(scopeType);
        final InjectMaidBuilder scopedBuilder = new InjectMaidBuilder(states, subScope, scopes);
        scopedBuilder.lifecycleManagement = lifecycleManagement;
        if (!scopes.contains(subScope)) {
            final ScopeInstantiator scopeInstantiator = scopeInstantiator(scopeType);
            scopedBuilder.withInstantiator(scopeType, scopeInstantiator, DEFAULT_REUSE_POLICY);
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
    public InjectMaidBuilder withFactory(final ResolvedType type,
                                         final ResolvedType factory,
                                         final ReusePolicy reusePolicy) {
        final Context context = context(type, scope, states, reusePolicy);
        final UnresolvedFactory state = UnresolvedFactory.unresolvedFactory(context, factory);
        states.addOrFailIfAlreadyPresent(state, false);
        return this;
    }

    @Override
    public InjectMaidBuilder withImplementation(final ResolvedType interfaceType,
                                                final ResolvedType implementationType,
                                                final ReusePolicy reusePolicy) {
        final Context context = context(interfaceType, scope, states, DEFAULT_REUSE_POLICY);
        final BindInstantiator instantiator = bindInstantiator(implementationType);
        context.setInstantiator(instantiator);
        final ResolvingDependencies state = resolvingDependencies(context);
        states.addOrFailIfAlreadyPresent(state, false);
        return withType(implementationType, reusePolicy, true);
    }

    @Override
    public InjectMaidBuilder withType(final ResolvedType resolvedType,
                                      final ReusePolicy reusePolicy) {
        return withType(resolvedType, reusePolicy, false);
    }

    private InjectMaidBuilder withType(final ResolvedType resolvedType,
                                       final ReusePolicy reusePolicy,
                                       final boolean allowDuplicatesIfSame) {
        final Context context = context(resolvedType, scope, states, reusePolicy);
        final State state = unresolved(context);
        states.addOrFailIfAlreadyPresent(state, allowDuplicatesIfSame);
        return this;
    }

    @Override
    public InjectMaidBuilder withCustomType(final CustomType customType,
                                            final ReusePolicy reusePolicy) {
        final ResolvedType type = customType.resolvedType();
        final CustomTypeData customTypeData = customType.instantiator();
        final CustomTypeInstantiator instantiator = customTypeInstantiator(
                customTypeData.dependencies(),
                customTypeData.invocableFactory()
        );
        return withInstantiator(type, instantiator, reusePolicy);
    }

    @Override
    public InjectMaidBuilder usingDefaultSingletonType(final SingletonType singletonType) {
        defaultSingletonType = singletonType;
        return this;
    }

    public InjectMaidBuilder withInstantiator(final ResolvedType resolvedType,
                                              final Instantiator instantiator,
                                              final ReusePolicy reusePolicy) {
        final Context context = context(resolvedType, scope, states, reusePolicy);
        context.setInstantiator(instantiator);
        final ResolvingDependencies state = resolvingDependencies(context);
        states.addOrFailIfAlreadyPresent(state, false);
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

    public InjectMaid build() {
        final Map<ResolvedType, List<Definition>> definitionsMap = runStateMachine(states);
        final Definitions definitions = definitions(scopes.asList(), definitionsMap);
        final LifecycleManager lifecycleManager;
        if (lifecycleManagement || !closers.isEmpty()) {
            closers.add(closer(AutoCloseable.class, AutoCloseable::close));
            lifecycleManager = realLifecycleManager(Closers.closers(this.closers));
        } else {
            lifecycleManager = noOpLifecycleManager();
        }
        final InjectMaid injectMaid = injectMaid(definitions, defaultSingletonType, lifecycleManager);
        if (registerShutdownHook) {
            if (!lifecycleManagement) {
                throw injectMaidException("can only close on JVM shutdown if lifecycle management is activated");
            }
            injectMaid.registerShutdownHook();
        }
        return injectMaid;
    }
}
