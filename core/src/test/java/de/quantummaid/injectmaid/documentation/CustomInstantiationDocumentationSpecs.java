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
import de.quantummaid.injectmaid.api.customtype.api.CustomType;
import de.quantummaid.injectmaid.documentation.domain.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class CustomInstantiationDocumentationSpecs {

    @Test
    public void zeroArgsFactory() {
        //Showcase start zeroArgsFactory
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(RegulatoryDomain.class, () -> RegulatoryDomain.regulatoryDomainFor("European Union"))
                .build();
        final RegulatoryDomain regulatoryDomain = injectMaid.getInstance(RegulatoryDomain.class);
        //Showcase end zeroArgsFactory
        assertThat(regulatoryDomain, notNullValue());
        assertThat(regulatoryDomain.regulatoryDomain, is("European Union"));
    }

    @Test
    public void customTypesInline() {
        //Showcase start customTypesInline
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(AdvancedBookingService.class, FlightStatusService.class, WeatherService.class, UuidService.class,
                        (flightStatusService, weatherService, uuidService) -> {
                            final AdvancedBookingService service = new AdvancedBookingService(flightStatusService, weatherService);
                            service.setUuidService(uuidService);
                            return service;
                        })
                .build();
        final AdvancedBookingService bookingService = injectMaid.getInstance(AdvancedBookingService.class);
        //Showcase end customTypesInline
        assertThat(bookingService, notNullValue());
        assertThat(bookingService.getUuidService(), notNullValue());
    }

    @Test
    public void customTypes() {
        //Showcase start customTypes
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(
                        CustomType.customType(AdvancedBookingService.class)
                                .withDependency(FlightStatusService.class)
                                .withDependency(WeatherService.class)
                                .withDependency(UuidService.class)
                                .usingFactory((flightStatusService, weatherService, uuidService) -> {
                                    final AdvancedBookingService service = new AdvancedBookingService(flightStatusService, weatherService);
                                    service.setUuidService(uuidService);
                                    return service;
                                })
                )
                .build();
        final AdvancedBookingService bookingService = injectMaid.getInstance(AdvancedBookingService.class);
        //Showcase end customTypes
        assertThat(bookingService, notNullValue());
        assertThat(bookingService.getUuidService(), notNullValue());
    }
}
