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

package de.quantummaid.injectmaid.documentation.domain;

import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.injectmaid.InjectMaidModule;
import de.quantummaid.injectmaid.ReusePolicy;

//Showcase start module
public final class BookingModule implements InjectMaidModule {

    @Override
    public void apply(final InjectMaidBuilder builder) {
        builder
                .withType(BookFlightService.class)
                .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
                .withType(BookingPolicies.class, ReusePolicy.SINGLETON);
    }
}
//Showcase end module
