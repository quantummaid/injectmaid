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

package de.quantummaid.injectmaid.detection.singleton;

import de.quantummaid.injectmaid.detection.SingletonSwitch;
import de.quantummaid.reflectmaid.ThirdPartyAnnotation;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationSingletonDetector implements SingletonDetector {
    public static final ThirdPartyAnnotation SINGLETON = ThirdPartyAnnotation.thirdPartyAnnotation(
            "javax.inject.Singleton",
            "com.google.inject.Singleton"
    );

    public static AnnotationSingletonDetector annotationSingletonDetector() {
        return new AnnotationSingletonDetector();
    }

    @Override
    public void detect(final ResolvedType type, final SingletonSwitch singletonSwitch) {
        if (SINGLETON.isAnnotatedWith(type)) {
            singletonSwitch.setSingleton();
        }
    }
}
