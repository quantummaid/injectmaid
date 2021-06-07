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

import de.quantummaid.injectmaid.annotated.MultipleAnnotations;
import de.quantummaid.injectmaid.annotated.guice.*;
import de.quantummaid.injectmaid.annotated.jsr330.*;
import de.quantummaid.injectmaid.annotated.spring.SpringAnnotatedConstructorType;
import de.quantummaid.injectmaid.annotated.spring.SpringAnnotatedFactoryType;
import de.quantummaid.injectmaid.annotated.spring.SpringAnnotatedNonStaticFactory;
import de.quantummaid.injectmaid.annotated.spring.SpringAnnotatedStaticFactory;
import de.quantummaid.injectmaid.domain.NumberedType;
import de.quantummaid.injectmaid.domain.StringWrapper;
import de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class AnnotationSpecs {

    @Test
    public void injectMaidRespectsJsr330InjectAnnotationsOnConstructor() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(Jsr330AnnotatedConstructorType.class)
                .build();
        final Jsr330AnnotatedConstructorType instance = injectMaid.getInstance(Jsr330AnnotatedConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(Jsr330AnnotatedConstructorType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsGuiceInjectAnnotationsOnConstructor() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(GuiceAnnotatedConstructorType.class)
                .build();
        final GuiceAnnotatedConstructorType instance = injectMaid.getInstance(GuiceAnnotatedConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(GuiceAnnotatedConstructorType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsSpringAutowiredAnnotationsOnConstructor() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(SpringAnnotatedConstructorType.class)
                .build();
        final SpringAnnotatedConstructorType instance = injectMaid.getInstance(SpringAnnotatedConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(SpringAnnotatedConstructorType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsJsr330InjectAnnotationsOnFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(Jsr330AnnotatedFactoryType.class)
                .build();
        final Jsr330AnnotatedFactoryType instance = injectMaid.getInstance(Jsr330AnnotatedFactoryType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(Jsr330AnnotatedFactoryType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsGuiceInjectAnnotationsOnFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(GuiceAnnotatedFactoryType.class)
                .build();
        final GuiceAnnotatedFactoryType instance = injectMaid.getInstance(GuiceAnnotatedFactoryType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(GuiceAnnotatedFactoryType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsSpringAutowiredAnnotationsOnFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(SpringAnnotatedFactoryType.class)
                .build();
        final SpringAnnotatedFactoryType instance = injectMaid.getInstance(SpringAnnotatedFactoryType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(SpringAnnotatedFactoryType.class));
        assertThat(instance.zeroArgumentsConstructorType, notNullValue());
        assertThat(instance.zeroArgumentsConstructorType, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void injectMaidRespectsJsr330InjectAnnotationsOnExternalStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, Jsr330AnnotatedStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from jsr330 annotated static factory"));
    }

    @Test
    public void injectMaidRespectsGuiceInjectAnnotationsOnExternalStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, GuiceAnnotatedStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from guice annotated static factory"));
    }

    @Test
    public void injectMaidRespectsSpringAutowiredAnnotationsOnExternalStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, SpringAnnotatedStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from spring annotated static factory"));
    }

    @Test
    public void injectMaidRespectsJsr330InjectAnnotationsOnExternalNonStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, Jsr330AnnotatedNonStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from jsr330 annotated non-static factory"));
    }

    @Test
    public void injectMaidRespectsGuiceInjectAnnotationsOnExternalNonStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, GuiceAnnotatedNonStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from guice annotated non-static factory"));
    }

    @Test
    public void injectMaidRespectsSpringInjectAnnotationsOnExternalNonStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, SpringAnnotatedNonStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("from spring annotated non-static factory"));
    }

    @Test
    public void injectMaidRespectsJsr330SingletonAnnotationsOnClass() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(Jsr330SingletonAnnotationOnClassType.class)
                .build();
        final Jsr330SingletonAnnotationOnClassType instance1 = injectMaid.getInstance(Jsr330SingletonAnnotationOnClassType.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.numberedType.instanceNumber(), is(0));

        final Jsr330SingletonAnnotationOnClassType instance2 = injectMaid.getInstance(Jsr330SingletonAnnotationOnClassType.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2.numberedType.instanceNumber(), is(0));
    }

    @Test
    public void injectMaidRespectsGuiceSingletonAnnotationsOnClass() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withType(GuiceSingletonAnnotationOnClassType.class)
                .build();
        final GuiceSingletonAnnotationOnClassType instance1 = injectMaid.getInstance(GuiceSingletonAnnotationOnClassType.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.numberedType.instanceNumber(), is(0));

        final GuiceSingletonAnnotationOnClassType instance2 = injectMaid.getInstance(GuiceSingletonAnnotationOnClassType.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2.numberedType.instanceNumber(), is(0));
    }

    @Test
    public void multipleAnnotations() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withType(MultipleAnnotations.class)
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("" +
                "de.quantummaid.injectmaid.annotated.MultipleAnnotations:\n" +
                "unable to detect registered:\n" +
                "no registered detected:\n" +
                "[Cannot decide how to instantiate type 'de.quantummaid.injectmaid.annotated.MultipleAnnotations':\n" +
                "More than one constructor or factory method has been annotated for injection (considered are " +
                "[javax.inject.Inject, com.google.inject.Inject, org.springframework.beans.factory.annotation.Autowired])]\n" +
                "Why it has been registered:\n" +
                "manually added"));
    }
}
