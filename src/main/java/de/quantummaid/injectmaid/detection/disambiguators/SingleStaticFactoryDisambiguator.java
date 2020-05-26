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

import de.quantummaid.injectmaid.detection.InstantiationOptions;
import de.quantummaid.injectmaid.instantiator.StaticFactoryInstantiator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.injectmaid.detection.disambiguators.DisambiguationResult.ignore;
import static de.quantummaid.injectmaid.detection.disambiguators.DisambiguationResult.success;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SingleStaticFactoryDisambiguator implements Disambiguator {

    public static SingleStaticFactoryDisambiguator singleStaticFactoryDisambiguator() {
        return new SingleStaticFactoryDisambiguator();
    }

    @Override
    public DisambiguationResult disambiguate(final InstantiationOptions instantiationOptions) {
        if (!instantiationOptions.constructors().isEmpty()) {
            return ignore("Static factories are not considered because public constructors have been found");
        }
        final List<StaticFactoryInstantiator> factoryMethods = instantiationOptions.staticFactoryMethods();
        if (factoryMethods.isEmpty()) {
            return ignore("No static factory methods have been found");
        }
        if (factoryMethods.size() > 1) {
            return ignore("More than one factory method has been found");
        }
        final StaticFactoryInstantiator instantiator = factoryMethods.get(0);
        return success(instantiator);
    }
}
