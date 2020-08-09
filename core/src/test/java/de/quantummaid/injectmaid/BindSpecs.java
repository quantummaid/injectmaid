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

import de.quantummaid.injectmaid.domain.*;
import de.quantummaid.injectmaid.domain.factory.NonStaticFactory;
import de.quantummaid.injectmaid.domain.factory.StaticAndNonStaticFactory;
import de.quantummaid.injectmaid.domain.factory.StaticFactory;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.ReusePolicy.SINGLETON;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class BindSpecs {

    @Test
    public void aTypeCanBeBoundToAStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, StaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("from static factory"));
    }

    @Test
    public void aTypeCanBeBoundToANonStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, NonStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("from non-static factory"));
    }

    @Test
    public void staticFactoryIsPreferredOverNonStaticFactory() {
        final InjectMaid injectMaid = anInjectMaid()
                .withFactory(StringWrapper.class, StaticAndNonStaticFactory.class)
                .build();
        final StringWrapper instance = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("from static factory"));
    }

    @Test
    public void anInterfaceCanBeBoundToAnImplementation() {
        final InjectMaid injectMaid = anInjectMaid()
                .withImplementation(MyInterface.class, MyImplementation.class)
                .build();
        final MyInterface instance = injectMaid.getInstance(MyInterface.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(MyImplementation.class));
        assertThat(instance.perform(), is("the implementation"));
    }

    @Test
    public void anAbstractClassCanBeBoundToAnImplementation() {
        final InjectMaid injectMaid = anInjectMaid()
                .withImplementation(MyAbstractClass.class, MyImplementation.class)
                .build();
        final MyAbstractClass instance = injectMaid.getInstance(MyAbstractClass.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(MyImplementation.class));
    }

    @Test
    public void theImplementationOfAnInterfaceCanBeASingleton() {
        MyNumberedImplementation.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withImplementation(MyInterface.class, MyNumberedImplementation.class)
                .withType(MyNumberedImplementation.class, SINGLETON)
                .build();

        final MyInterface instance1 = injectMaid.getInstance(MyInterface.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1, instanceOf(MyNumberedImplementation.class));
        assertThat(instance1.perform(), is("the numbered implementation nr. 0"));

        final MyInterface instance2 = injectMaid.getInstance(MyInterface.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2, instanceOf(MyNumberedImplementation.class));
        assertThat(instance2.perform(), is("the numbered implementation nr. 0"));

        final MyNumberedImplementation instance3 = injectMaid.getInstance(MyNumberedImplementation.class);
        assertThat(instance3, notNullValue());
        assertThat(instance3, instanceOf(MyNumberedImplementation.class));
        assertThat(instance3.perform(), is("the numbered implementation nr. 0"));
    }

    @Test
    public void aBoundInterfaceCanBeASingleton() {
        MyNumberedImplementation.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withImplementation(MyInterface.class, MyNumberedImplementation.class, SINGLETON)
                .build();

        final MyInterface instance1 = injectMaid.getInstance(MyInterface.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1, instanceOf(MyNumberedImplementation.class));
        assertThat(instance1.perform(), is("the numbered implementation nr. 0"));

        final MyInterface instance2 = injectMaid.getInstance(MyInterface.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2, instanceOf(MyNumberedImplementation.class));
        assertThat(instance2.perform(), is("the numbered implementation nr. 0"));

        final MyNumberedImplementation instance3 = injectMaid.getInstance(MyNumberedImplementation.class);
        assertThat(instance3, notNullValue());
        assertThat(instance3, instanceOf(MyNumberedImplementation.class));
        assertThat(instance3.perform(), is("the numbered implementation nr. 1"));
    }

    @Test
    public void singletonBoundToInterfaceAndAbstractClassIsTheSameInBothCases() {
        final InjectMaid injectMaid = anInjectMaid()
                .withImplementation(MyInterface.class, MyImplementation.class)
                .withImplementation(MyAbstractClass.class, MyImplementation.class)
                .withType(MyImplementation.class, SINGLETON)
                .build();

        final MyInterface myInterface = injectMaid.getInstance(MyInterface.class);
        assertThat(myInterface, notNullValue());
        assertThat(myInterface, instanceOf(MyImplementation.class));
        assertThat(myInterface.perform(), is("the implementation"));

        final MyAbstractClass myAbstractClass = injectMaid.getInstance(MyAbstractClass.class);
        assertThat(myAbstractClass, notNullValue());
        assertThat(myAbstractClass, instanceOf(MyImplementation.class));

        assertThat(myAbstractClass, is(myInterface));
    }
}
