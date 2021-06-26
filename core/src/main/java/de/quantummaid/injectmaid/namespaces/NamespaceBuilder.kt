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
package de.quantummaid.injectmaid.namespaces

import de.quantummaid.injectmaid.InjectMaidBuilder
import de.quantummaid.injectmaid.api.ReusePolicy
import de.quantummaid.injectmaid.api.ReusePolicy.PROTOTYPE
import de.quantummaid.injectmaid.api.customtype.api.Builder
import de.quantummaid.injectmaid.api.customtype.api.CustomType
import de.quantummaid.injectmaid.api.customtype.api.CustomType.customType
import de.quantummaid.injectmaid.api.customtype.api.FactoryBuilder00
import de.quantummaid.reflectmaid.GenericType
import de.quantummaid.reflectmaid.GenericType.Companion.genericType

class NamespaceBuilder<Namespace>(val namespace: GenericType<Namespace>, val injectMaidBuilder: InjectMaidBuilder) {

    companion object {
        inline fun <reified Namespace : Any> InjectMaidBuilder.namespace(builder: NamespaceBuilder<Namespace>.() -> Unit): InjectMaidBuilder {
            val namespace = genericType<Namespace>()
            val namespaceBuilder = NamespaceBuilder(namespace, this)
            builder(namespaceBuilder)
            return this
        }
    }

    inline fun <reified Type : Any> importing(): NamespaceBuilder<Namespace> {
        val genericType = genericType<Type>()
        injectMaidBuilder.withCustomType(
            customType(genericType<Any>(NamespacedType::class.java, genericType, namespace))
                .withDependency(genericType)
                .usingFactory { NamespacedType<Any, Any>(it) }
        )
        return this
    }

    inline fun <reified Type : Any> exporting(): NamespaceBuilder<Namespace> {
        val genericType = genericType<Type>()
        injectMaidBuilder.withCustomType(
            customType(genericType)
                .withDependency(genericType<Any>(NamespacedType::class.java, genericType, namespace))
                .usingFactory {
                    @Suppress("UNCHECKED_CAST")
                    (it as NamespacedType<Type, Any>).dependency()
                }
        )
        return this
    }

    inline fun <reified Type : Any> customType(reusePolicy: ReusePolicy = PROTOTYPE, builder: (FactoryBuilder00<Type>) -> CustomType): NamespaceBuilder<Namespace> {
        val genericType = genericType<Any>(NamespacedType::class.java, genericType<Type>(), namespace)
        val internalBuilder = Builder.builder(genericType, namespace)
        val entry: FactoryBuilder00<Type> = FactoryBuilder00<Type>(internalBuilder)
        val customType = builder(entry)
        injectMaidBuilder.withCustomType(customType, reusePolicy)
        return this
    }
}
