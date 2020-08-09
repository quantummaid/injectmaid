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
import de.quantummaid.injectmaid.ReusePolicy;
import de.quantummaid.injectmaid.SingletonType;
import de.quantummaid.injectmaid.documentation.domain.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class UsageDocumentationSpecs {

    @Test
    public void basicUsage() {
        //Showcase start basicUsage
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(FlightStatusService.class)
                .build();
        final FlightStatusService flightStatusService = injectMaid.getInstance(FlightStatusService.class);
        //Showcase end basicUsage
        assertThat(flightStatusService, notNullValue());
        assertThat(flightStatusService, instanceOf(FlightStatusService.class));
    }

    @Test
    public void bindInterface() {
        //Showcase start bindInterface
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
                .build();
        final BookingRepository bookingRepository = injectMaid.getInstance(BookingRepository.class);
        //Showcase end bindInterface
        assertThat(bookingRepository, notNullValue());
        assertThat(bookingRepository, instanceOf(InMemoryBookingRepository.class));
    }

    @Test
    public void singletons() {
        //Showcase start singletons
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(BookingPolicies.class, ReusePolicy.SINGLETON)
                .build();
        final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
        //Showcase end singletons
        assertThat(bookingPolicies, notNullValue());
        assertThat(bookingPolicies, instanceOf(BookingPolicies.class));
    }

    @Test
    public void lazySingletons() {
        //Showcase start lazySingletons
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(BookingPolicies.class, ReusePolicy.LAZY_SINGLETON)
                .build();
        final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
        //Showcase end lazySingletons
        assertThat(bookingPolicies, notNullValue());
        assertThat(bookingPolicies, instanceOf(BookingPolicies.class));
    }

    @Test
    public void eagerSingletons() {
        //Showcase start eagerSingletons
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(BookingPolicies.class, ReusePolicy.EAGER_SINGLETON)
                .build();
        final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
        //Showcase end eagerSingletons
        assertThat(bookingPolicies, notNullValue());
        assertThat(bookingPolicies, instanceOf(BookingPolicies.class));
    }

    @Test
    public void defaultEagerSingletons() {
        //Showcase start defaultEagerSingletons
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withType(BookingPolicies.class, ReusePolicy.SINGLETON)
                .usingDefaultSingletonType(SingletonType.EAGER)
                .build();
        final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
        //Showcase end defaultEagerSingletons
        assertThat(bookingPolicies, notNullValue());
        assertThat(bookingPolicies, instanceOf(BookingPolicies.class));
    }

    @Test
    public void factory() {
        //Showcase start factory
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withFactory(UuidService.class, UuidServiceFactory.class)
                .build();
        final UuidService uuidService = injectMaid.getInstance(UuidService.class);
        //Showcase end factory
        assertThat(uuidService, notNullValue());
        assertThat(uuidService, instanceOf(UuidService.class));
    }

    @Test
    public void constants() {
        final InMemoryBookingRepository BOOKING_REPOSITORY = new InMemoryBookingRepository();
        //Showcase start constants
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withConstant(BookingRepository.class, BOOKING_REPOSITORY)
                .build();
        final BookingRepository bookingRepository = injectMaid.getInstance(BookingRepository.class);
        //Showcase end constants
        assertThat(bookingRepository, notNullValue());
        assertThat(bookingRepository, instanceOf(InMemoryBookingRepository.class));
    }

    @Test
    public void modules() {
        //Showcase start moduleUsage
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withModule(new BookingModule())
                .build();
        final BookFlightService bookFlightService = injectMaid.getInstance(BookFlightService.class);
        //Showcase end moduleUsage
        assertThat(bookFlightService, notNullValue());
        assertThat(bookFlightService, instanceOf(BookFlightService.class));
    }
}
