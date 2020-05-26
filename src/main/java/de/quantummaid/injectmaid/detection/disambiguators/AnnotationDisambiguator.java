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
import de.quantummaid.injectmaid.detection.ThirdPartyAnnotation;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.detection.ThirdPartyAnnotation.INJECT;
import static de.quantummaid.injectmaid.detection.disambiguators.DisambiguationResult.*;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationDisambiguator implements Disambiguator {

    public static AnnotationDisambiguator annotationDisambiguator() {
        return new AnnotationDisambiguator();
    }

    @Override
    public DisambiguationResult disambiguate(final InstantiationOptions instantiationOptions) {
        final List<Instantiator> annotatedInstantiators = annotatedWith(instantiationOptions, INJECT);
        if (annotatedInstantiators.size() == 1) {
            final Instantiator instantiator = annotatedInstantiators.get(0);
            return success(instantiator);
        }
        if (annotatedInstantiators.size() > 1) {
            final String errorMessage = String.format(
                    "More than one constructor or factory method has been annotated for injection (considered are %s)",
                    INJECT.describe()
            );
            return error(errorMessage);
        }
        return ignore("No annotations have been detected");
    }

    private List<Instantiator> annotatedWith(final InstantiationOptions options,
                                             final ThirdPartyAnnotation annotation) {
        final List<Instantiator> annotatedInstantiators = new ArrayList<>();
        options.constructors().stream()
                .filter(constructorInstantiator -> {
                    final ResolvedConstructor constructor = constructorInstantiator.constructor();
                    return annotation.isAnnotatedWith(constructor);
                })
                .forEach(annotatedInstantiators::add);
        options.staticFactoryMethods().stream()
                .filter(factoryInstantiator -> {
                    final ResolvedMethod factory = factoryInstantiator.method();
                    return annotation.isAnnotatedWith(factory);
                })
                .forEach(annotatedInstantiators::add);
        options.nonStaticFactoryMethods().stream()
                .filter(factoryInstantiator -> {
                    final ResolvedMethod factory = factoryInstantiator.method();
                    return annotation.isAnnotatedWith(factory);
                })
                .forEach(annotatedInstantiators::add);
        return annotatedInstantiators;
    }
}
