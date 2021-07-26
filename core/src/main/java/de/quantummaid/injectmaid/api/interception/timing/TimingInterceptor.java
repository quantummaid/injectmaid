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

package de.quantummaid.injectmaid.api.interception.timing;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.api.interception.Interceptor;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static de.quantummaid.injectmaid.api.interception.timing.EnforcedMaxInstantiationTimeExceededException.enforcedMaxInstantiationTimeExceededException;
import static java.util.Optional.empty;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimingInterceptor implements Interceptor {
    private final Duration maxDuration;
    private Instant before;

    public static TimingInterceptor timingInterceptor(final Duration maxDuration) {
        return new TimingInterceptor(maxDuration);
    }

    @Override
    public Optional<?> interceptBeforeInstantiation(final TypeIdentifier type,
                                                    final TypeIdentifier rootType) {
        before = Instant.now();
        return empty();
    }

    @Override
    public Object interceptAfterInstantiation(final TypeIdentifier type,
                                              final TypeIdentifier rootType,
                                              final ReusePolicy reusePolicy,
                                              final Object instance) {
        final Instant after = Instant.now();
        final Duration duration = Duration.between(before, after);
        if (duration.compareTo(maxDuration) > 0) {
            throw enforcedMaxInstantiationTimeExceededException(
                    type, rootType, instance, maxDuration, duration, reusePolicy
            );
        }
        return instance;
    }
}
