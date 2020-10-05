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
import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.documentation.domain.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ScopeDocumentationSpecs {

    @Test
    public void scopes() {
        //Showcase start scopeDefinition
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withScope(Request.class, scope -> {
                    scope.withType(BookFlightService.class);
                    scope.withImplementation(BookingRepository.class, InMemoryBookingRepository.class);
                })
                .build();
        //Showcase end scopeDefinition

        //Showcase start scopes
        final Request request1 = new Request("elsa");
        final Injector request1Injector = injectMaid.enterScope(Request.class, request1);
        final BookFlightService bookFlightService1 = request1Injector.getInstance(BookFlightService.class);

        final Request request2 = new Request("olaf");
        final Injector request2Injector = injectMaid.enterScope(Request.class, request2);
        final BookFlightService bookFlightService2 = request2Injector.getInstance(BookFlightService.class);
        //Showcase end scopes
        assertThat(bookFlightService1, notNullValue());
        assertThat(bookFlightService1, instanceOf(BookFlightService.class));
        assertThat(bookFlightService2, notNullValue());
        assertThat(bookFlightService2, instanceOf(BookFlightService.class));
    }

    @Test
    public void scopedSingletons() {
        //Showcase start scopedSingletons
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                // global singleton:
                .withType(UuidService.class, ReusePolicy.SINGLETON)
                .withScope(Request.class, scope -> {
                    // request-scoped singleton:
                    scope.withType(BookingPolicies.class, ReusePolicy.SINGLETON);
                })
                .build();

        final Request request1 = new Request("elsa");
        final Injector request1Injector = injectMaid.enterScope(Request.class, request1);
        final UuidService uuidService1 = request1Injector.getInstance(UuidService.class);
        final BookingPolicies bookingPolicies1 = request1Injector.getInstance(BookingPolicies.class);

        final Request request2 = new Request("olaf");
        final Injector request2Injector = injectMaid.enterScope(Request.class, request2);
        final UuidService uuidService2 = request1Injector.getInstance(UuidService.class);
        final BookingPolicies bookingPolicies2 = request2Injector.getInstance(BookingPolicies.class);

        // global singletons are the same in both scopes
        assert uuidService1 == uuidService2;

        // request-scoped singletons are different in each scope
        assert bookingPolicies1 != bookingPolicies2;
        //Showcase end scopedSingletons

        assertThat(uuidService1, is(uuidService2));
        assertThat(bookingPolicies1, is(not(bookingPolicies2)));
    }
}
