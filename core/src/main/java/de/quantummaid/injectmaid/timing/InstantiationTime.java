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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class InstantiationTime {
    private static final String INDENT = "\t";

    private final TypeIdentifier type;
    private final long timeInMilliseconds;
    private final List<InstantiationTime> dependencies;

    public static InstantiationTime instantiationTime(final TypeIdentifier type,
                                                      final long timeInMilliseconds) {
        return new InstantiationTime(type, timeInMilliseconds, new ArrayList<>());
    }

    public InstantiationTime addTime(final long time) {
        return new InstantiationTime(type, timeInMilliseconds + time, dependencies);
    }

    public void reportDependency(final InstantiationTime instantiationTime) {
        dependencies.add(instantiationTime);
    }

    public long timeInMilliseconds() {
        return timeInMilliseconds;
    }

    public TypeIdentifier type() {
        return type;
    }

    public List<InstantiationTime> dependencies() {
        return dependencies;
    }

    public String render() {
        final StringJoiner stringJoiner = new StringJoiner("\n");
        render("", stringJoiner);
        return stringJoiner.toString();
    }

    private void render(final String indentation,
                        final StringJoiner stringJoiner) {
        final String line = String.format("%s%dms %s", indentation, timeInMilliseconds, type.simpleDescription());
        stringJoiner.add(line);
        final String childIndentation = indentation + INDENT;
        dependencies.forEach(dependency -> dependency.render(childIndentation, stringJoiner));
    }
}
