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

import de.quantummaid.injectmaid.failing.TooManyConstructorsType;
import de.quantummaid.injectmaid.failing.TooManyFactoriesType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ErrorAggregationSpecs {

    @Test
    public void errorsAreAggregated() {
        final Exception exception = catchException(() -> InjectMaid.anInjectMaid()
                .withType(TooManyConstructorsType.class)
                .withType(TooManyFactoriesType.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "Cannot decide how to instantiate type 'de.quantummaid.injectmaid.failing.TooManyConstructorsType':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found\n" +
                "\n" +
                "Cannot decide how to instantiate type 'de.quantummaid.injectmaid.failing.TooManyFactoriesType':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "No public constructors found\n" +
                "More than one factory method has been found"));
    }
}
