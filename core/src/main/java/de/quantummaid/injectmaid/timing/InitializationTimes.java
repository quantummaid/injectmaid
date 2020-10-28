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

package de.quantummaid.injectmaid.timing;

import de.quantummaid.reflectmaid.GenericType;

import java.util.HashMap;
import java.util.Map;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static java.lang.String.format;

public final class InitializationTimes {
    private final Map<GenericType<?>, InstantiationTime> initializationTimesInMilliseconds = new HashMap<>();

    public static InitializationTimes initializationTimes() {
        return new InitializationTimes();
    }

    public void addInitializationTime(final GenericType<?> type,
                                      final InstantiationTime time) {
        initializationTimesInMilliseconds.put(type, time);
    }

    public InstantiationTime initializationTimeFor(final Class<?> type) {
        final GenericType<?> genericType = GenericType.genericType(type);
        return initializationTimeFor(genericType);
    }

    public InstantiationTime initializationTimeFor(final GenericType<?> genericType) {
        if (!initializationTimesInMilliseconds.containsKey(genericType)) {
            throw injectMaidException(format("no initialization time available for %s", genericType.toResolvedType().description()));
        }
        return initializationTimesInMilliseconds.get(genericType);
    }
}
