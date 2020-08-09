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

import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static de.quantummaid.injectmaid.Scope.rootScope;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SingletonStore {
    private final Scope scope;
    private final SingletonStore parent;
    private final Map<ResolvedType, Object> singletons;

    public static SingletonStore singletonStore() {
        final Scope scope = rootScope();
        return new SingletonStore(scope, null, new HashMap<>());
    }

    public SingletonStore child(final ResolvedType scopeElement) {
        final Scope childScope = this.scope.childScope(scopeElement);
        return new SingletonStore(childScope, this, new HashMap<>());
    }

    public boolean contains(final ResolvedType type,
                            final Scope scope) {
        if (!scope.equals(this.scope)) {
            return parent.contains(type, scope);
        }
        return singletons.containsKey(type);
    }

    public Object get(final ResolvedType type,
                      final Scope scope) {
        if (!scope.equals(this.scope)) {
            return parent.get(type, scope);
        }
        return singletons.get(type);
    }

    public void put(final ResolvedType type,
                    final Scope scope,
                    final Object object) {
        if (!scope.equals(this.scope)) {
            parent.put(type, scope, object);
        } else {
            singletons.put(type, object);
        }
    }
}
