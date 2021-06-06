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

import de.quantummaid.injectmaid.domain.MyInterface;
import de.quantummaid.injectmaid.failing.TooManyConstructorsType;
import de.quantummaid.injectmaid.failing.TooManyFactoriesType;
import de.quantummaid.injectmaid.failing.UninstantiableType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ExceptionSpecs {

    @Test
    public void unregisteredTypeCannotBeInstantiated() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid().build();
        final Exception exception = catchException(() -> injectMaid.getInstance(String.class));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Cannot instantiate unregistered type 'java.lang.String'"));
    }

    @Test
    public void interfaceWithNoFactoriesCannotBeRegisteredDirectly() {
        final Exception exception = catchException(() -> InjectMaid.anInjectMaid()
                .withType(MyInterface.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("unable to detect registered:\nno registered detected:\n" +
                "[Cannot decide how to instantiate type 'de.quantummaid.injectmaid.domain.MyInterface':\n" +
                "No public constructors or static factory methods found\n" +
                "No annotations have been detected\n" +
                "No public constructors found\n" +
                "No static factory methods have been found]"));
    }

    @Test
    public void typeWithMoreThanOnePublicConstructorCannotBeAutodetected() {
        final Exception exception = catchException(() -> InjectMaid.anInjectMaid()
                .withType(TooManyConstructorsType.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("unable to detect registered:\nno registered detected:\n" +
                "[Cannot decide how to instantiate type 'de.quantummaid.injectmaid.failing.TooManyConstructorsType':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]"));
    }

    @Test
    public void typeWithNoPublicConstructorAndMoreThanOnePublicFactoryMethodCannotBeAutodetected() {
        final Exception exception = catchException(() -> InjectMaid.anInjectMaid()
                .withType(TooManyFactoriesType.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("unable to detect registered:\nno registered detected:\n" +
                "[Cannot decide how to instantiate type 'de.quantummaid.injectmaid.failing.TooManyFactoriesType':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "No public constructors found\n" +
                "More than one factory method has been found]"));
    }

    @Test
    public void typeWithNoConstructorsAndNoFactoriesCannotBeAutodetected() {
        final Exception exception = catchException(() -> InjectMaid.anInjectMaid()
                .withType(UninstantiableType.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("unable to detect registered:\nno registered detected:\n" +
                "[Cannot decide how to instantiate type 'de.quantummaid.injectmaid.failing.UninstantiableType':\n" +
                "No public constructors or static factory methods found\n" +
                "No annotations have been detected\n" +
                "No public constructors found\n" +
                "No static factory methods have been found]"));
    }
}
