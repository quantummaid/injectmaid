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

package de.quantummaid.injectmaid.transparentscopeentrance

import de.quantummaid.injectmaid.InjectMaid
import de.quantummaid.injectmaid.api.Injector
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.GenericType.Companion.genericType
import kotlin.reflect.KClass

fun <T: Any> InjectMaid.enterOptionalScope(scopeObject: T): Injector {
    val kClass = scopeObject::class
    return enterOptionalScope(kClass, scopeObject)
}

fun <T : Any> InjectMaid.enterOptionalScope(type: KClass<out T>,
                                            scopeObject: T): Injector {
    return enterOptionalScope(type.java, scopeObject)
}

fun <T> InjectMaid.enterOptionalScope(type: Class<out T>,
                                      scopeObject: T): Injector {
    val genericType = genericType(type)
    return enterOptionalScope(genericType, scopeObject)
}

fun <T> InjectMaid.enterOptionalScope(genericType: GenericType<out T>,
                                      scopeObject: T): Injector {
    val resolvedType = reflectMaid().resolve(genericType)
    return enterOptionalScope(resolvedType, scopeObject as Any)
}

fun InjectMaid.enterOptionalScope(resolvedType: ResolvedType,
                                  scopeObject: Any): Injector {
    return enterScopeIfExists(resolvedType, scopeObject)
            .orElseGet { InjectMaidWrapperThatBehavesLikeNormalInjectMaidButDoesNotDoAnythingOnClose(this) }
}

class InjectMaidWrapperThatBehavesLikeNormalInjectMaidButDoesNotDoAnythingOnClose(
        val delegate: InjectMaid
) : Injector by delegate {

    override fun close() {
        // do nothing
    }
}
