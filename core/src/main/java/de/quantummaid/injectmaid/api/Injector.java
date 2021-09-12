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

package de.quantummaid.injectmaid.api;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.injectmaid.api.interception.SimpleInterceptor;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;

import java.time.Duration;
import java.util.Optional;

import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

public interface Injector extends AutoCloseable {

    default <T> T getInstance(final Class<T> type) {
        final GenericType<T> genericType = genericType(type);
        return getInstance(genericType);
    }

    default <T> T getInstance(final GenericType<T> type) {
        final TimedInstantiation<T> instanceWithInitializationTime = getInstanceWithInitializationTime(type);
        return instanceWithInitializationTime.instance();
    }

    default <T> T getInstance(ResolvedType type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return getInstance(typeIdentifier);
    }

    <T> T getInstance(TypeIdentifier type);

    default <T> TimedInstantiation<T> getInstanceWithInitializationTime(final Class<T> type) {
        final GenericType<T> genericType = genericType(type);
        return getInstanceWithInitializationTime(genericType);
    }

    <T> TimedInstantiation<T> getInstanceWithInitializationTime(GenericType<T> type);

    default void initializeAllSingletons() {
        initializeAllSingletons(null);
    }

    void initializeAllSingletons(Duration enforcedMaxTime);

    default Injector enterScope(final Object scopeObject) {
        return enterScopeWithTimeout(scopeObject, null);
    }

    default <T> Injector enterScope(final Class<T> scopeType, final T scopeObject) {
        return enterScopeWithTimeout(scopeType, scopeObject, null);
    }

    default <T> Injector enterScope(GenericType<T> scopeType, T scopeObject) {
        return enterScopeWithTimeout(scopeType, scopeObject, null);
    }

    default Injector enterScope(final ResolvedType scopeType, final Object scopeObject) {
        return enterScopeWithTimeout(scopeType, scopeObject, null);
    }

    default Injector enterScope(final TypeIdentifier scopeType, final Object scopeObject) {
        return enterScopeWithTimeout(scopeType, scopeObject, null);
    }

    @SuppressWarnings("unchecked")
    default Injector enterScopeWithTimeout(final Object scopeObject, final Duration enforcedMaxTime) {
        final Class<Object> scopeType = (Class<Object>) scopeObject.getClass();
        return enterScopeWithTimeout(scopeType, scopeObject, enforcedMaxTime);
    }

    default <T> Injector enterScopeWithTimeout(final Class<T> scopeType,
                                    final T scopeObject,
                                    final Duration enforcedMaxTime) {
        final GenericType<T> genericType = genericType(scopeType);
        return enterScopeWithTimeout(genericType, scopeObject, enforcedMaxTime);
    }

    <T> Injector enterScopeWithTimeout(GenericType<T> scopeType, T scopeObject, Duration enforcedMaxTime);

    default Injector enterScopeWithTimeout(final ResolvedType scopeType,
                                final Object scopeObject,
                                final Duration enforcedMaxTime) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(scopeType);
        return enterScopeWithTimeout(typeIdentifier, scopeObject, enforcedMaxTime);
    }

    Injector enterScopeWithTimeout(TypeIdentifier scopeType, Object scopeObject, Duration enforcedMaxTime);

    @SuppressWarnings("unchecked")
    default Optional<Injector> enterScopeIfExists(final Object scopeObject) {
        final Class<Object> scopeType = (Class<Object>) scopeObject.getClass();
        return enterScopeIfExists(scopeType, scopeObject);
    }

    default <T> Optional<Injector> enterScopeIfExists(final Class<T> scopeType, final T scopeObject) {
        final GenericType<T> genericType = genericType(scopeType);
        return enterScopeIfExists(genericType, scopeObject);
    }

    <T> Optional<Injector> enterScopeIfExists(GenericType<T> scopeType, T scopeObject);

    Optional<Injector> enterScopeIfExists(ResolvedType resolvedType, Object scopeObject);

    void addInterceptor(SimpleInterceptor interceptor);

    default void overwriteWith(final InjectorConfiguration injectorConfiguration) {
        final InjectMaidBuilder builder = InjectMaid.anInjectMaid(reflectMaid());
        injectorConfiguration.apply(builder);
        final InjectMaid injectMaid = builder.build();
        overwriteWith(injectMaid);
    }

    void overwriteWith(Injector injector);

    default boolean canInstantiate(final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return canInstantiate(genericType);
    }

    boolean canInstantiate(GenericType<?> type);

    default boolean canInstantiate(final ResolvedType type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return canInstantiate(typeIdentifier);
    }

    boolean canInstantiate(TypeIdentifier type);

    @Override
    void close();

    ReflectMaid reflectMaid();
}
