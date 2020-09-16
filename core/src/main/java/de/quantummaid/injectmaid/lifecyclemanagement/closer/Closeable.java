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

package de.quantummaid.injectmaid.lifecyclemanagement.closer;

import de.quantummaid.injectmaid.lifecyclemanagement.ExceptionDuringClose;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static de.quantummaid.injectmaid.lifecyclemanagement.ExceptionDuringClose.exceptionDuringClose;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Closeable {
    private final Object instance;
    private final Closer closer;
    private boolean closed;

    public static Closeable closeable(final Object instance,
                                      final Closer closer) {
        return new Closeable(instance, closer, false);
    }

    public Optional<ExceptionDuringClose> close() {
        if (closed) {
            return Optional.empty();
        }
        try {
            closer.close(instance);
            closed = true;
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(exceptionDuringClose(e, instance));
        }
    }
}
