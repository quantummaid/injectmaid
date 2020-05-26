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

package de.quantummaid.injectmaid.statemachine.states;

import de.quantummaid.injectmaid.Scope;
import de.quantummaid.injectmaid.statemachine.Context;
import de.quantummaid.reflectmaid.ResolvedType;

public interface State {

    Context context();

    default boolean matches(final ResolvedType otherType, final Scope otherScope) {
        final Context context = context();
        final ResolvedType type = context.type();
        if (!type.equals(otherType)) {
            return false;
        }
        final Scope scope = context.scope();
        return scope.contains(otherScope);
    }

    default boolean isFinal() {
        return false;
    }

    default State resolvedDependencies() {
        return this;
    }

    default State detectInstantiator() {
        return this;
    }
}
