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

import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import static de.quantummaid.injectmaid.timing.InstanceAndTimedDependencies.instanceWithNoDependencies;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimedInstantiation<T> {
    private final T object;
    private final InstantiationTime instantiationTime;

    public static <T> TimedInstantiation<T> timeInstantiation(final TypeIdentifier type,
                                                              final TimedInstantiator<T> supplier) {
        final InstantiationTime instantiationTime = InstantiationTime.instantiationTime(type, 0);
        return timeInstantiation(supplier, instantiationTime);
    }

    private static <T> TimedInstantiation<T> timeInstantiation(final TimedInstantiator<T> supplier,
                                                               final InstantiationTime baseValue) {
        final long startTime = System.currentTimeMillis();
        final InstanceAndTimedDependencies<T> instanceAndTimedDependencies = supplier.instantiate();
        final long endTime = System.currentTimeMillis();
        final InstantiationTime instantiationTime = baseValue.addTime(endTime - startTime);
        instanceAndTimedDependencies.dependencies().forEach(instantiationTime::reportDependency);
        return new TimedInstantiation<>(instanceAndTimedDependencies.instance(), instantiationTime);
    }

    public <X> TimedInstantiation<X> modify(final Function<T, X> modifier) {
        return timeInstantiation(() -> instanceWithNoDependencies(modifier.apply(object)), instantiationTime);
    }

    public T instance() {
        return object;
    }

    public InstantiationTime instantiationTime() {
        return instantiationTime;
    }
}
