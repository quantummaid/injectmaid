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

import java.util.Optional;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface Injector extends AutoCloseable {

    default <T> T getInstance(final Class<T> type) {
        final GenericType<T> genericType = genericType(type);
        return getInstance(genericType);
    }

    default <T> T getInstance(final GenericType<T> type) {
        final TimedInstantiation<T> instanceWithInitializationTime = getInstanceWithInitializationTime(type);
        return instanceWithInitializationTime.instance();
    }

    <T> T getInstance(ResolvedType type);

    default <T> TimedInstantiation<T> getInstanceWithInitializationTime(final Class<T> type) {
        final GenericType<T> genericType = genericType(type);
        return getInstanceWithInitializationTime(genericType);
    }

    <T> TimedInstantiation<T> getInstanceWithInitializationTime(GenericType<T> type);

    void initializeAllSingletons();

    @SuppressWarnings("unchecked")
    default Injector enterScope(final Object scopeObject) {
        final Class<Object> scopeType = (Class<Object>) scopeObject.getClass();
        return enterScope(scopeType, scopeObject);
    }

    default <T> Injector enterScope(final Class<T> scopeType, final T scopeObject) {
        final GenericType<T> genericType = genericType(scopeType);
        return enterScope(genericType, scopeObject);
    }

    <T> Injector enterScope(GenericType<T> scopeType, T scopeObject);

    Injector enterScope(ResolvedType resolvedType, Object scopeObject);

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

    boolean canInstantiate(ResolvedType resolvedType);

    @Override
    void close();

    ReflectMaid reflectMaid();
}
