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

package de.quantummaid.injectmaid.documentation;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.documentation.domain.BookingRepository;
import de.quantummaid.injectmaid.documentation.domain.InMemoryBookingRepository;
import de.quantummaid.injectmaid.documentation.domain.MockedBookingRepository;
import de.quantummaid.injectmaid.documentation.domain.WrappedBookingRepository;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class TestSupportDocumentationSpecs {

    @Test
    public void overwriting() {
        //Showcase start overwriting
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
                .build();

        final BookingRepository notOverwritten = injectMaid.getInstance(BookingRepository.class);

        final InjectMaid overwriteInjectMaid = InjectMaid.anInjectMaid()
                .withImplementation(BookingRepository.class, MockedBookingRepository.class)
                .build();
        injectMaid.overwriteWith(overwriteInjectMaid);

        final BookingRepository overwritten = injectMaid.getInstance(BookingRepository.class);
        //Showcase end overwriting
        assertThat(notOverwritten, notNullValue());
        assertThat(notOverwritten, instanceOf(InMemoryBookingRepository.class));

        assertThat(overwritten, notNullValue());
        assertThat(overwritten, instanceOf(MockedBookingRepository.class));
    }

    @Test
    public void interception() {
        //Showcase start interception
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
                .build();

        final BookingRepository notIntercepted = injectMaid.getInstance(BookingRepository.class);

        injectMaid.addInterceptor(object -> {
            if (object instanceof InMemoryBookingRepository) {
                return new WrappedBookingRepository((BookingRepository) object);
            }
            return object;
        });

        final BookingRepository intercepted = injectMaid.getInstance(BookingRepository.class);
        //Showcase end interception

        assertThat(notIntercepted, notNullValue());
        assertThat(notIntercepted, instanceOf(InMemoryBookingRepository.class));

        assertThat(intercepted, notNullValue());
        assertThat(intercepted, instanceOf(WrappedBookingRepository.class));
    }
}
