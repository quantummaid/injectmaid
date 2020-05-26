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

import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThirdPartyAnnotation {
    public static final ThirdPartyAnnotation INJECT = thirdPartyAnnotation(
            "javax.inject.Inject",
            "com.google.inject.Inject",
            "org.springframework.beans.factory.annotation.Autowired"
    );
    public static final ThirdPartyAnnotation SINGLETON = thirdPartyAnnotation(
            "javax.inject.Singleton",
            "com.google.inject.Singleton"
    );

    private final List<String> fullyQualifiedNames;

    public static ThirdPartyAnnotation thirdPartyAnnotation(final String... fullyQualifiedNames) {
        return new ThirdPartyAnnotation(asList(fullyQualifiedNames));
    }

    public String describe() {
        return fullyQualifiedNames.stream()
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public boolean isAnnotatedWith(final ResolvedConstructor constructor) {
        final Constructor<?> rawConstructor = constructor.constructor();
        return isAnnotated(rawConstructor);
    }

    public boolean isAnnotatedWith(final ResolvedMethod method) {
        final Method rawMethod = method.method();
        return isAnnotated(rawMethod);
    }

    public boolean isAnnotatedWith(final ResolvedType type) {
        final Class<?> rawType = type.assignableType();
        return isAnnotated(rawType);
    }

    private boolean isAnnotated(final AnnotatedElement annotatedElement) {
        final Annotation[] annotations = annotatedElement.getAnnotations();
        return stream(annotations)
                .map(Annotation::annotationType)
                .map(Class::getName)
                .anyMatch(fullyQualifiedNames::contains);
    }
}
