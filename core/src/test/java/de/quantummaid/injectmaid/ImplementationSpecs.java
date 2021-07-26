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

import de.quantummaid.injectmaid.domain.MyAbstractClass;
import de.quantummaid.injectmaid.domain.MyInterface;
import de.quantummaid.injectmaid.domain.MySpecificImplementation;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ImplementationSpecs {

    @Test
    public void withImplementationRegisteredInterfaceCanBeRegisteredAsCustomType() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(MySpecificImplementation.class, () -> new MySpecificImplementation("foo"))
                .withImplementation(MyInterface.class, MySpecificImplementation.class)
                .build();

        final MyInterface myInterface = injectMaid.getInstance(MyInterface.class);
        assertThat(myInterface, instanceOf(MySpecificImplementation.class));
        assertThat(myInterface.perform(), is("foo"));
    }

    @Test
    public void abstractClassCannotBeAutodetected() {
        InjectMaidException exception = null;
        try {
            anInjectMaid()
                    .withType(MyAbstractClass.class)
                    .build();
        } catch (final InjectMaidException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
    }
}
