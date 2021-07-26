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
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;

import java.time.Duration;

public final class EnforcedMaxInstantiationTimeExceededException extends RuntimeException {
    private transient final TypeIdentifier type;
    private transient final TypeIdentifier rootType;
    private transient final Object instance;
    private transient final Duration maxTime;
    private transient final Duration actualTime;
    private transient final ReusePolicy reusePolicy;

    public EnforcedMaxInstantiationTimeExceededException(final TypeIdentifier type,
                                                         final TypeIdentifier rootType,
                                                         final Object instance,
                                                         final Duration maxTime,
                                                         final Duration actualTime,
                                                         final ReusePolicy reusePolicy,
                                                         final String message) {
        super(message);
        this.type = type;
        this.rootType = rootType;
        this.instance = instance;
        this.maxTime = maxTime;
        this.actualTime = actualTime;
        this.reusePolicy = reusePolicy;
    }

    public static EnforcedMaxInstantiationTimeExceededException enforcedMaxInstantiationTimeExceededException(
            final TypeIdentifier type,
            final TypeIdentifier rootType,
            final Object instance,
            final Duration maxTime,
            final Duration actualTime,
            final ReusePolicy reusePolicy
    ) {
        final String dependencyString;
        if (!type.equals(rootType)) {
            dependencyString = " as a dependency of type " + rootType.description() + " ";
        } else {
            dependencyString = " ";
        }
        final String message = "took " + actualTime.toMillis() + "ms to instantiate object " +
                "of type " + type.description() + dependencyString + "but only " + maxTime.toMillis() + "ms allowed\n" +
                "created object: " + instance + "\n" +
                "reuse policy: " + reusePolicy.name();
        return new EnforcedMaxInstantiationTimeExceededException(
                type, rootType, instance, maxTime, actualTime, reusePolicy, message
        );
    }

    public TypeIdentifier type() {
        return type;
    }

    public TypeIdentifier rootType() {
        return rootType;
    }

    public Object instance() {
        return instance;
    }

    public Duration maxTime() {
        return maxTime;
    }

    public Duration actualTime() {
        return actualTime;
    }

    public ReusePolicy reusePolicy() {
        return reusePolicy;
    }
}
