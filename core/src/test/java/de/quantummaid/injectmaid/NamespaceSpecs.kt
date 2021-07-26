/**
 * Copyright (c) 2021 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.quantummaid.injectmaid

import de.quantummaid.injectmaid.InjectMaid.anInjectMaid
import de.quantummaid.injectmaid.namespaces.NamespaceBuilder.Companion.withNamespace
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

data class MapMaid(val string: String)
class QueueMaid
data class MySerializer(val mapMaid: MapMaid)

class NamespaceSpecs {

    @Test
    fun namespacesWork() {
        val injectMaid = anInjectMaid()
            .withCustomType(String::class.java) { "an injected string" }
            .withNamespace<QueueMaid> {
                importing<String>()
                exporting<MySerializer>()
                customType<MySerializer> {
                    it
                        .withDependency(MapMaid::class.java)
                        .usingFactory { MySerializer(it) }
                }
                customType<MapMaid> {
                    it
                        .withDependency(String::class.java)
                        .usingFactory { MapMaid(it) }
                }
            }
            .build()

        val instance = injectMaid.getInstance(MySerializer::class.java)
        assertThat(instance.mapMaid.string, `is`("an injected string"))
    }
}