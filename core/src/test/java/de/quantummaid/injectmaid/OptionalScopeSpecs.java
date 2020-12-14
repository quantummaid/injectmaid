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

package de.quantummaid.injectmaid;

import de.quantummaid.injectmaid.api.Injector;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class OptionalScopeSpecs {

    @Test
    public void scopeCanBeEnteredOptionally() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(String.class, builder -> {
                })
                .build();
        final Optional<Injector> scope1 = injectMaid.enterScopeIfExists(1);
        assertThat(scope1.isPresent(), is(false));
        final Optional<Injector> scope2 = injectMaid.enterScopeIfExists("1");
        assertThat(scope2.isPresent(), is(true));
    }
}
