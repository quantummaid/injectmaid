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

package de.quantummaid.injectmaid.instantiator;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.ScopeManager;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SelfInstantiator implements Instantiator {

    public static SelfInstantiator selfInstantiator() {
        return new SelfInstantiator();
    }

    @Override
    public List<TypeIdentifier> dependencies() {
        return emptyList();
    }

    @Override
    public Object instantiate(final List<Object> dependencies,
                              final ScopeManager scopeManager,
                              final InjectMaid injectMaid) {
        return injectMaid;
    }

    @Override
    public String description() {
        return "injector";
    }
}
