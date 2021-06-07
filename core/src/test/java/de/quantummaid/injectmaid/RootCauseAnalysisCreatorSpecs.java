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

import de.quantummaid.injectmaid.domain.MyTypeWithString;
import de.quantummaid.reflectmaid.TypeToken;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class RootCauseAnalysisCreatorSpecs {

    @Test
    public void rootCauseAnalysisWorksOnDirectDependency() {
        final Exception exception = catchException(
                () -> anInjectMaid()
                        .withType(MyTypeWithString.class)
                        .build()
        );

        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "java.lang.String:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'java.lang.String':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]\n" +
                "Why it has been registered:\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString -> manually added"));
    }

    @Test
    public void rootCauseAnalysisWorksOnIndirectDependency() {
        final Exception exception = catchException(
                () -> anInjectMaid()
                        .withType(genericType(new TypeToken<MyTypeWithDependencies<MyTypeWithString>>() {
                        }))
                        .build()
        );

        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "java.lang.String:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'java.lang.String':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]\n" +
                "Why it has been registered:\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString " +
                "-> because of de.quantummaid.injectmaid.MyTypeWithDependencies<de.quantummaid.injectmaid.domain.MyTypeWithString> " +
                "-> manually added"));
    }

    @Test
    public void rootCauseAnalysisSupportsMultiplePaths() {
        final Exception exception = catchException(
                () -> anInjectMaid()
                        .withType(String.class)
                        .withType(MyTypeWithString.class)
                        .withType(genericType(new TypeToken<MyTypeWithDependencies<MyTypeWithString>>() {
                        }))
                        .build()
        );

        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "java.lang.String:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'java.lang.String':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]\n" +
                "Why it has been registered:\n" +
                "manually added\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString -> manually added\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString " +
                "-> because of de.quantummaid.injectmaid.MyTypeWithDependencies<de.quantummaid.injectmaid.domain.MyTypeWithString> " +
                "-> manually added"));
    }

    @Test
    public void rootCauseAnalysisWorksInScopes() {
        final Exception exception = catchException(
                () -> anInjectMaid()
                        .withScope(Integer.class, builder -> builder.withType(MyTypeWithString.class))
                        .build()
        );

        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "java.lang.String:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'java.lang.String':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]\n" +
                "Why it has been registered:\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString -> manually added"));
    }

    @Test
    public void rootCauseAnalysisWorksAcrossScopes() {
        final Exception exception = catchException(
                () -> anInjectMaid()
                        .withType(String.class)
                        .withScope(Integer.class, builder -> builder.withType(MyTypeWithString.class))
                        .build()
        );

        exception.printStackTrace();

        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "java.lang.String:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'java.lang.String':\n" +
                "More than one public constructors or factory methods found\n" +
                "No annotations have been detected\n" +
                "More than one public constructors found\n" +
                "Static factories are not considered because public constructors have been found]\n" +
                "Why it has been registered:\n" +
                "manually added\n" +
                "because of de.quantummaid.injectmaid.domain.MyTypeWithString -> manually added"));
    }
}
