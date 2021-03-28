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
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.InstantiationTimes;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@SuppressWarnings("java:S2925")
public final class InitializationReportingSpecs {

    @Test
    public void instantiationTimesCanBeReportedOnAClassLevel() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(1000);
                    return "foo";
                }, ReusePolicy.EAGER_SINGLETON)
                .build();

        final InstantiationTimes instantiationTimes = injectMaid.instantiationTimes();
        final InstantiationTime instantiationTime = instantiationTimes.initializationTimeFor(String.class);
        final long time = instantiationTime.timeInMilliseconds();
        assertThat(time, is(greaterThan(900L)));

        assertThat(instantiationTimes.render(), containsString("ms String"));
        assertThat(instantiationTime.render(), containsString("ms String"));
    }

    @Test
    public void instantiationTimesCanBeIterated() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(Integer.class, () -> {
                    sleep(250);
                    return 42;
                }, ReusePolicy.EAGER_SINGLETON)
                .withCustomType(Boolean.class, () -> {
                    sleep(250);
                    return false;
                }, ReusePolicy.EAGER_SINGLETON)
                .withCustomType(Character.class, () -> {
                    sleep(250);
                    return 'Q';
                }, ReusePolicy.EAGER_SINGLETON)
                .withCustomType(Long.class, () -> {
                    sleep(250);
                    return 1337L;
                }, ReusePolicy.EAGER_SINGLETON)
                .build();

        final InstantiationTimes instantiationTimes = injectMaid.instantiationTimes();
        final List<InstantiationTime> allInstantiationTimes = instantiationTimes.allInstantiationTimes();
        assertThat(allInstantiationTimes.size(), is(4));

        assertThat(instantiationTimes.render(), containsString("ms Long"));
        assertThat(instantiationTimes.render(), containsString("ms Character"));
        assertThat(instantiationTimes.render(), containsString("ms Integer"));
        assertThat(instantiationTimes.render(), containsString("ms Boolean"));
    }

    @Test
    public void instantiationTimesCanBeReportedPerInstantiation() {
        final InjectMaid injectMaid = InjectMaid.anInjectMaid()
                .withCustomType(String.class, () -> {
                    sleep(1000);
                    return "foo";
                })
                .build();

        final TimedInstantiation<String> instanceWithInitializationTime =
                injectMaid.getInstanceWithInitializationTime(String.class);
        final InstantiationTime instantiationTime = instanceWithInitializationTime.instantiationTime();
        final long time = instantiationTime.timeInMilliseconds();
        assertThat(time, is(greaterThan(900L)));

        assertThat(instantiationTime.render(), containsString("ms String"));
    }

    @Test
    public void instantiationTimesCanBeSplitUpIntoDependencies() {
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
        assertThat(firstDependency.type().simpleDescription(), is("Integer"));
        assertThat(firstDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime secondDependency = dependencies.get(1);
        assertThat(secondDependency.type().simpleDescription(), is("Boolean"));
        assertThat(secondDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime thirdDependency = dependencies.get(2);
        assertThat(thirdDependency.type().simpleDescription(), is("Character"));
        assertThat(thirdDependency.timeInMilliseconds(), is(greaterThan(200L)));

        final InstantiationTime fourthDependency = dependencies.get(3);
        assertThat(fourthDependency.type().simpleDescription(), is("Long"));
        assertThat(fourthDependency.timeInMilliseconds(), is(greaterThan(200L)));

        assertThat(instantiationTime.render(), containsString("ms String"));
        assertThat(instantiationTime.render(), containsString("ms Integer"));
        assertThat(instantiationTime.render(), containsString("ms Boolean"));
        assertThat(instantiationTime.render(), containsString("ms Character"));
        assertThat(instantiationTime.render(), containsString("ms Long"));
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
