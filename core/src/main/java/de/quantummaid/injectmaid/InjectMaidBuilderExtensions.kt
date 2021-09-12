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

import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier
import kotlin.reflect.KClass

fun InjectMaidBuilder.withScope(
    scopeType: KClass<*>,
    configuration: InjectMaidBuilder.() -> Unit
): InjectMaidBuilder {
    val genericType = genericType(scopeType)
    return withScope(genericType, configuration)
}

inline fun <reified T : Any> InjectMaidBuilder.withScope(
    noinline configuration: InjectMaidBuilder.() -> Unit
): InjectMaidBuilder {
    val genericType = genericType<T>()
    return withScope(genericType, configuration)
}

fun InjectMaidBuilder.withScope(
    scopeType: GenericType<*>,
    configuration: InjectMaidBuilder.() -> Unit
): InjectMaidBuilder {
    return withScope(scopeType, configuration)
}

fun InjectMaidBuilder.withScope(
    scopeType: TypeIdentifier,
    configuration: InjectMaidBuilder.() -> Unit
): InjectMaidBuilder {
    return withScope(scopeType, configuration)
}
