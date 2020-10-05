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

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.domain.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.api.customtype.api.CustomType.customType;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CustomTypeSpecs {

    @Test
    public void aFactoryCanBeConfigured() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(customType(StringWrapper.class)
                        .usingFactory(() -> new StringWrapper("foo"))
                )
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("foo"));
    }

    @Test
    public void aCustomTypeCanBeASingleton() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> UUID.randomUUID().toString(), ReusePolicy.SINGLETON)
                .build();
        final String instance1 = injectMaid.getInstance(String.class);
        final String instance2 = injectMaid.getInstance(String.class);
        assertThat(instance1, is(instance2));
    }

    @Test
    public void customTypeFactoryCanReturnNull() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> null)
                .build();
        final String instance = injectMaid.getInstance(String.class);
        assertThat(instance, nullValue());
    }

    @Test
    public void exceptionInCustomTypeIsThrownProperly() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> {
                    throw new UnsupportedOperationException("foo");
                })
                .build();

        final Exception exception = catchException(() -> injectMaid.getInstance(String.class));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), containsString("Exception during instantiation of 'String' using custom instantiation " +
                "via 'de.quantummaid.injectmaid.CustomTypeSpecs$$Lambda$"));

        final Throwable cause = exception.getCause();
        assertThat(cause, notNullValue());
        assertThat(cause, instanceOf(UnsupportedOperationException.class));
        assertThat(cause.getMessage(), is("foo"));
    }

    @Test
    public void manyFieldsCanBeRegistered() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(
                        customType(CustomTypeWithManyFields.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .withDependency(ZeroArgumentsConstructorType.class)
                                .usingFactory(CustomTypeWithManyFields::new)
                )
                .build();
        final CustomTypeWithManyFields instance = injectMaid.getInstance(CustomTypeWithManyFields.class);

        assertThat(instance, notNullValue());
        assertThat(instance.field00, notNullValue());
        assertThat(instance.field01, notNullValue());
        assertThat(instance.field02, notNullValue());
        assertThat(instance.field03, notNullValue());
        assertThat(instance.field04, notNullValue());
        assertThat(instance.field05, notNullValue());
        assertThat(instance.field06, notNullValue());
        assertThat(instance.field07, notNullValue());
        assertThat(instance.field08, notNullValue());
        assertThat(instance.field09, notNullValue());
        assertThat(instance.field10, notNullValue());
        assertThat(instance.field11, notNullValue());
        assertThat(instance.field12, notNullValue());
        assertThat(instance.field13, notNullValue());
        assertThat(instance.field14, notNullValue());
        assertThat(instance.field15, notNullValue());
    }

    @Test
    public void customTypeWith0DependenciesCanBeInlined() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(StringWrapper.class, () -> new StringWrapper("foo"))
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("foo"));
    }

    @Test
    public void customTypeWith1DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class, NumberedType.class, NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0]"));
    }

    @Test
    public void customTypeWith2DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1]"));
    }

    @Test
    public void customTypeWith3DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2]"));
    }

    @Test
    public void customTypeWith4DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3]"));
    }

    @Test
    public void customTypeWith5DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4]"));
    }

    @Test
    public void customTypeWith6DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5]"));
    }

    @Test
    public void customTypeWith7DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6]"));
    }

    @Test
    public void customTypeWith8DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7]"));
    }

    @Test
    public void customTypeWith9DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8]"));
    }

    @Test
    public void customTypeWith10DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9]"));
    }

    @Test
    public void customTypeWith11DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10]"));
    }

    @Test
    public void customTypeWith12DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10/11]"));
    }

    @Test
    public void customTypeWith13DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10/11/12]"));
    }

    @Test
    public void customTypeWith14DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10/11/12/13]"));
    }

    @Test
    public void customTypeWith15DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10/11/12/13/14]"));
    }

    @Test
    public void customTypeWith16DependencyCanBeInlined() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(NumberCollector.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberedType.class,
                        NumberCollector::numberCollector)
                .build();
        final NumberCollector instance = injectMaid.getInstance(NumberCollector.class);
        assertThat(instance, notNullValue());
        assertThat(instance.collectedString, is("[0/1/2/3/4/5/6/7/8/9/10/11/12/13/14/15]"));
    }
}
