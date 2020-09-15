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

import de.quantummaid.injectmaid.domain.NumberedType;
import de.quantummaid.injectmaid.domain.StringWrapper;
import org.junit.jupiter.api.Test;

import static de.quantummaid.injectmaid.customtype.CustomType.customType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class InterceptorSpecs {

    @Test
    public void anAfterInterceptorCanBeConfigured() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(customType(StringWrapper.class)
                        .usingFactory(() -> new StringWrapper("original value"))
                )
                .build();

        final StringWrapper instance1 = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.string, is("original value"));

        injectMaid.addInterceptor(object -> {
            if(object instanceof StringWrapper) {
                ((StringWrapper) object).string = "overwritten value";
            }
            return object;
        });

        final StringWrapper instance2 = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2.string, is("overwritten value"));
    }

    @Test
    public void anInjectMaidCanBeOverwrittenWithAnotherInjectMaid() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(StringWrapper.class, () -> new StringWrapper("original value"))
                .build();

        final StringWrapper instance1 = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.string, is("original value"));

        final InjectMaid overwrite = InjectMaid.anInjectMaid()
                .withCustomType(StringWrapper.class, () -> new StringWrapper("overwritten value"))
                .build();
        injectMaid.overwriteWith(overwrite);

        final StringWrapper instance2 = injectMaid.getInstance(StringWrapper.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2.string, is("overwritten value"));
    }

    @Test
    public void injectMaidDoesNotCreateInstancesWhenOverwritten() {
        NumberedType.counter = 0;

        final NumberedType constantUntrackedNumberedType = new NumberedType();
        assertThat(constantUntrackedNumberedType.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(1));

        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(NumberedType.class)
                .build();

        final NumberedType instance1 = injectMaid.getInstance(NumberedType.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.instanceNumber(), is(1));
        assertThat(NumberedType.counter, is(2));

        final InjectMaid overwrite = InjectMaid.anInjectMaid()
                .withCustomType(NumberedType.class, () -> constantUntrackedNumberedType)
                .build();
        injectMaid.overwriteWith(overwrite);

        final NumberedType instance2 = injectMaid.getInstance(NumberedType.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2.instanceNumber(), is(0));
        assertThat(NumberedType.counter, is(2));
    }
}
