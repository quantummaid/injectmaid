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

package de.quantummaid.injectmaid.detection;

import de.quantummaid.injectmaid.instantiator.Instantiator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionResult {
    private final Instantiator instantiator;
    private final String errorMessage;

    public static DetectionResult success(final Instantiator instantiator) {
        validateNotNull(instantiator, "instantiator");
        return new DetectionResult(instantiator, null);
    }

    public static DetectionResult fail(final String errorMessage) {
        validateNotNull(errorMessage, "errorMessage");
        return new DetectionResult(null, errorMessage);
    }

    public Instantiator instantiator() {
        return instantiator;
    }

    public boolean isFailure() {
        return errorMessage != null;
    }

    public String errorMessage() {
        return errorMessage;
    }
}
