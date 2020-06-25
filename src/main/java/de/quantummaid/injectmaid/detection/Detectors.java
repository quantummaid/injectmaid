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

import de.quantummaid.injectmaid.InjectMaidException;
import de.quantummaid.injectmaid.detection.disambiguators.DisambiguationResult;
import de.quantummaid.injectmaid.detection.disambiguators.Disambiguator;
import de.quantummaid.injectmaid.detection.singleton.SingletonDetector;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.ResolvedType;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.InjectMaidException.injectMaidException;
import static de.quantummaid.injectmaid.detection.InstantiationOptions.loadInstantiationOptions;
import static de.quantummaid.injectmaid.detection.disambiguators.AnnotationDisambiguator.annotationDisambiguator;
import static de.quantummaid.injectmaid.detection.disambiguators.SingleChoiceDisambiguator.singleChoiceDisambiguator;
import static de.quantummaid.injectmaid.detection.disambiguators.SingleConstructorDisambiguator.singleConstructorDisambiguator;
import static de.quantummaid.injectmaid.detection.disambiguators.SingleStaticFactoryDisambiguator.singleStaticFactoryDisambiguator;
import static de.quantummaid.injectmaid.detection.singleton.AnnotationSingletonDetector.annotationSingletonDetector;
import static java.lang.String.format;
import static java.lang.String.join;

public final class Detectors {

    private static final List<Disambiguator> DISAMBIGUATORS = List.of(
            singleChoiceDisambiguator(),
            annotationDisambiguator(),
            singleConstructorDisambiguator(),
            singleStaticFactoryDisambiguator()
    );

    private static final List<SingletonDetector> SINGLETON_DETECTORS = List.of(
            annotationSingletonDetector()
    );

    private Detectors() {
    }

    public static Instantiator detect(final ResolvedType typeToInstantiate,
                                      final SingletonSwitch singletonSwitch) {
        return detect(typeToInstantiate, typeToInstantiate, singletonSwitch);
    }

    public static Instantiator detect(final ResolvedType typeToInstantiate,
                                      final ResolvedType creatingType,
                                      final SingletonSwitch singletonSwitch) {
        if (!(creatingType instanceof ClassType)) {
            throw new IllegalArgumentException();
        }
        final ClassType creatingClassType = (ClassType) creatingType;

        SINGLETON_DETECTORS.forEach(singletonDetector -> singletonDetector.detect(typeToInstantiate, singletonSwitch));

        final InstantiationOptions instantiationOptions = loadInstantiationOptions(
                typeToInstantiate, creatingClassType);

        final List<String> ignoreReasons = new ArrayList<>();
        for (final Disambiguator disambiguator : DISAMBIGUATORS) {
            final DisambiguationResult result = disambiguator.disambiguate(instantiationOptions);
            if (result.isSuccess()) {
                return result.instantiator();
            }
            if (result.isIgnore()) {
                ignoreReasons.add(result.ignoreMessage());
            }
            if (result.isError()) {
                throw fail(typeToInstantiate, creatingType, result.errorMessage());
            }
        }
        final String combinedIgnoreReasons = join("\n", ignoreReasons);
        throw fail(typeToInstantiate, creatingType, combinedIgnoreReasons);
    }

    private static InjectMaidException fail(final ResolvedType typeToInstantiate,
                                            final ResolvedType creatingType,
                                            final String message) {
        final String factoryQualifier;
        if (typeToInstantiate.equals(creatingType)) {
            factoryQualifier = "";
        } else {
            factoryQualifier = format(" from factory '%s'", creatingType.description());
        }
        final String errorMessage = format("Cannot decide how to instantiate type '%s'%s:%n%s",
                typeToInstantiate.description(), factoryQualifier, message);
        return injectMaidException(errorMessage);
    }
}
