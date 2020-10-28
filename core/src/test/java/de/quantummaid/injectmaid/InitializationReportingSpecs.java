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
import de.quantummaid.injectmaid.timing.InitializationTimes;
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public final class InitializationReportingSpecs {

    @Test
    public void initializationTimeCanBeReportedOnAClassLevel() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(1000);
                    return "foo";
                }, ReusePolicy.EAGER_SINGLETON)
                .build();

        final InitializationTimes initializationTimes = injectMaid.initializationTimes();
        final long time = initializationTimes.initializationTimeFor(String.class).timeInMilliseconds();
        assertThat(time, is(greaterThan(900L)));
    }

    @Test
    public void initializationTimeCanBeReportedPerInstantiation() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(1000);
                    return "foo";
                })
                .build();

        final TimedInstantiation<String> instanceWithInitializationTime =
                injectMaid.getInstanceWithInitializationTime(String.class);
        final long time = instanceWithInitializationTime.instantiationTime().timeInMilliseconds();
        assertThat(time, is(greaterThan(900L)));
    }

    @Test
    public void initializationTimeCanBeSplitUpIntoDependencies() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(Integer.class, () -> {
                    sleep(250);
                    return 42;
                })
                .withCustomType(Boolean.class, () -> {
                    sleep(250);
                    return false;
                })
                .withCustomType(Character.class, () -> {
                    sleep(250);
                    return 'Q';
                })
                .withCustomType(Long.class, () -> {
                    sleep(250);
                    return 1337L;
                })
                .withCustomType(String.class,
                        Integer.class,
                        Boolean.class,
                        Character.class,
                        Long.class,
                        (integer, aBoolean, character, aLong) -> "" + integer + aBoolean + character + aLong)
                .build();

        final TimedInstantiation<String> instanceWithInitializationTime = injectMaid.getInstanceWithInitializationTime(String.class);
        assertThat(instanceWithInitializationTime.instance(), is("42falseQ1337"));

        final InstantiationTime instantiationTime = instanceWithInitializationTime.instantiationTime();
        assertThat(instantiationTime.timeInMilliseconds(), is(greaterThan(900L)));

        final List<InstantiationTime> dependencies = instantiationTime.dependencies();
        assertThat(dependencies.size(), is(4));

        final InstantiationTime firstDependency = dependencies.get(0);
        assertThat(firstDependency.type().toResolvedType().simpleDescription(), is("Integer"));
        assertThat(firstDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime secondDependency = dependencies.get(1);
        assertThat(secondDependency.type().toResolvedType().simpleDescription(), is("Boolean"));
        assertThat(secondDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime thirdDependency = dependencies.get(2);
        assertThat(thirdDependency.type().toResolvedType().simpleDescription(), is("Character"));
        assertThat(thirdDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime fourthDependency = dependencies.get(3);
        assertThat(fourthDependency.type().toResolvedType().simpleDescription(), is("Long"));
        assertThat(fourthDependency.timeInMilliseconds(), is(greaterThan(200L)));
    }

    private static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }
}
