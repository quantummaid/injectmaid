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

import de.quantummaid.injectmaid.domain.StringWrapper;
import de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public final class DebugInformationSpecs {

    @Test
    public void debugInformationCanBeQueried() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(ZeroArgumentsConstructorType.class)
                .withScope(String.class, builder -> builder.withCustomType(StringWrapper.class, () -> new StringWrapper("foo")))
                .build();
        final String debugInformation = injectMaid.debugInformation();
        assertThat(debugInformation, containsString("/ de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType (PROTOTYPE)"));
        assertThat(debugInformation, containsString("/ de.quantummaid.reflectmaid.ReflectMaid (DEFAULT_SINGLETON)"));
        assertThat(debugInformation, containsString("/String de.quantummaid.injectmaid.domain.StringWrapper (PROTOTYPE)"));
        assertThat(debugInformation, containsString("/String de.quantummaid.injectmaid.domain.StringWrapper (PROTOTYPE)"));
    }
}
