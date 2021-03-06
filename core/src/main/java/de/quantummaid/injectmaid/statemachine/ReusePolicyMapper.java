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

package de.quantummaid.injectmaid.statemachine;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.injectmaid.statemachine.TypeAndScope.typeAndScope;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReusePolicyMapper {
    private final ReusePolicy defaultReusePolicy;
    private final Map<TypeAndScope, ReusePolicy> reusePolicyMap;

    public static ReusePolicyMapper reusePolicyMapper(final ReusePolicy reusePolicy) {
        return new ReusePolicyMapper(reusePolicy, new LinkedHashMap<>());
    }

    public void registerReusePolicy(final TypeIdentifier type,
                                    final Scope scope,
                                    final ReusePolicy reusePolicy) {
        reusePolicyMap.put(typeAndScope(type, scope), reusePolicy);
    }

    public ReusePolicy reusePolicyFor(final TypeIdentifier type, final Scope scope) {
        return reusePolicyMap.getOrDefault(typeAndScope(type, scope), defaultReusePolicy);
    }
}
