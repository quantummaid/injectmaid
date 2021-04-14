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

package de.quantummaid.injectmaid.detection.disambiguators;

import de.quantummaid.injectmaid.instantiator.Instantiator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.injectmaid.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DisambiguationResult {
    private final String errorMessage;
    private final String ignoreMessage;
    private final Instantiator instantiator;

    public static DisambiguationResult error(final String errorMessage) {
        validateNotNull(errorMessage, "errorMessage");
        return new DisambiguationResult(errorMessage, null, null);
    }

    public static DisambiguationResult ignore(final String ignoreMessage) {
        validateNotNull(ignoreMessage, "ignoreMessage");
        return new DisambiguationResult(null, ignoreMessage, null);
    }

    public static DisambiguationResult success(final Instantiator instantiator) {
        validateNotNull(instantiator, "instantiator");
        return new DisambiguationResult(null, null, instantiator);
    }

    public boolean isError() {
        return errorMessage != null;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public boolean isIgnore() {
        return ignoreMessage != null;
    }

    public String ignoreMessage() {
        return ignoreMessage;
    }

    public boolean isSuccess() {
        return instantiator != null;
    }

    public Instantiator instantiator() {
        return instantiator;
    }
}
