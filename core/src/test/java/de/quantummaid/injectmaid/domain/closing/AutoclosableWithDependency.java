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

package de.quantummaid.injectmaid.domain.closing;

public final class AutoclosableWithDependency implements AutoCloseable {
    public final AutoclosableType dependency;
    public boolean closed = false;
    public boolean dependencyHasBeenClosedFirst = false;

    public AutoclosableWithDependency(final AutoclosableType dependency) {
        this.dependency = dependency;
    }

    @Override
    public void close() {
        dependencyHasBeenClosedFirst = dependency.closed;
        closed = true;
    }
}
