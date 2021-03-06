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

package de.quantummaid.injectmaid.api.builder;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.api.customtype.api.*;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.injectmaid.api.ReusePolicy.PROTOTYPE;
import static de.quantummaid.injectmaid.api.customtype.api.CustomType.customType;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@SuppressWarnings({"java:S107", "java:S1448", "java:S103", "java:S104"})
@FunctionalInterface
public interface CustomTypeConfigurators<T extends CustomTypeConfigurators<T>> {

    @SuppressWarnings("unchecked")
    default T withInstance(final Object instance) {
        final Class<?> type = instance.getClass();
        return withInstance((Class<Object>) type, instance);
    }

    default <X> T withInstance(final Class<X> type,
                               final X instance) {
        final GenericType<X> genericType = genericType(type);
        return withInstance(genericType, instance);
    }

    default <X> T withInstance(final GenericType<X> type,
                               final X instance) {
        final CustomType customType = customType(type).usingFactory(() -> instance);
        return withCustomType(customType, ReusePolicy.DEFAULT_SINGLETON);
    }

    T withCustomType(CustomType customType, ReusePolicy reusePolicy);

    default T withCustomType(final CustomType customType) {
        return withCustomType(customType, PROTOTYPE);
    }

    default <X> T withCustomType(final Class<X> type,
                                 final Factory00<X> factory) {
        return withCustomType(type, factory, PROTOTYPE);
    }

    default <X> T withCustomType(final Class<X> type,
                                 final Factory00<X> factory,
                                 final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A> T withCustomType(final Class<X> type,
                                    final Class<A> dependency00,
                                    final Factory01<X, A> factory) {
        return withCustomType(type, dependency00, factory, PROTOTYPE);
    }

    default <X, A> T withCustomType(final Class<X> type,
                                    final Class<A> dependency00,
                                    final Factory01<X, A> factory,
                                    final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B> T withCustomType(final Class<X> type,
                                       final Class<A> dependency00,
                                       final Class<B> dependency01,
                                       final Factory02<X, A, B> factory) {
        return withCustomType(type, dependency00, dependency01, factory, PROTOTYPE);
    }

    default <X, A, B> T withCustomType(final Class<X> type,
                                       final Class<A> dependency00,
                                       final Class<B> dependency01,
                                       final Factory02<X, A, B> factory,
                                       final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C> T withCustomType(final Class<X> type,
                                          final Class<A> dependency00,
                                          final Class<B> dependency01,
                                          final Class<C> dependency02,
                                          final Factory03<X, A, B, C> factory) {
        return withCustomType(type, dependency00, dependency01, dependency02, factory, PROTOTYPE);
    }

    default <X, A, B, C> T withCustomType(final Class<X> type,
                                          final Class<A> dependency00,
                                          final Class<B> dependency01,
                                          final Class<C> dependency02,
                                          final Factory03<X, A, B, C> factory,
                                          final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D> T withCustomType(final Class<X> type,
                                             final Class<A> dependency00,
                                             final Class<B> dependency01,
                                             final Class<C> dependency02,
                                             final Class<D> dependency03,
                                             final Factory04<X, A, B, C, D> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D> T withCustomType(final Class<X> type,
                                             final Class<A> dependency00,
                                             final Class<B> dependency01,
                                             final Class<C> dependency02,
                                             final Class<D> dependency03,
                                             final Factory04<X, A, B, C, D> factory,
                                             final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E> T withCustomType(final Class<X> type,
                                                final Class<A> dependency00,
                                                final Class<B> dependency01,
                                                final Class<C> dependency02,
                                                final Class<D> dependency03,
                                                final Class<E> dependency04,
                                                final Factory05<X, A, B, C, D, E> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E> T withCustomType(final Class<X> type,
                                                final Class<A> dependency00,
                                                final Class<B> dependency01,
                                                final Class<C> dependency02,
                                                final Class<D> dependency03,
                                                final Class<E> dependency04,
                                                final Factory05<X, A, B, C, D, E> factory,
                                                final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F> T withCustomType(final Class<X> type,
                                                   final Class<A> dependency00,
                                                   final Class<B> dependency01,
                                                   final Class<C> dependency02,
                                                   final Class<D> dependency03,
                                                   final Class<E> dependency04,
                                                   final Class<F> dependency05,
                                                   final Factory06<X, A, B, C, D, E, F> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F> T withCustomType(final Class<X> type,
                                                   final Class<A> dependency00,
                                                   final Class<B> dependency01,
                                                   final Class<C> dependency02,
                                                   final Class<D> dependency03,
                                                   final Class<E> dependency04,
                                                   final Class<F> dependency05,
                                                   final Factory06<X, A, B, C, D, E, F> factory,
                                                   final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G> T withCustomType(final Class<X> type,
                                                      final Class<A> dependency00,
                                                      final Class<B> dependency01,
                                                      final Class<C> dependency02,
                                                      final Class<D> dependency03,
                                                      final Class<E> dependency04,
                                                      final Class<F> dependency05,
                                                      final Class<G> dependency06,
                                                      final Factory07<X, A, B, C, D, E, F, G> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G> T withCustomType(final Class<X> type,
                                                      final Class<A> dependency00,
                                                      final Class<B> dependency01,
                                                      final Class<C> dependency02,
                                                      final Class<D> dependency03,
                                                      final Class<E> dependency04,
                                                      final Class<F> dependency05,
                                                      final Class<G> dependency06,
                                                      final Factory07<X, A, B, C, D, E, F, G> factory,
                                                      final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H> T withCustomType(final Class<X> type,
                                                         final Class<A> dependency00,
                                                         final Class<B> dependency01,
                                                         final Class<C> dependency02,
                                                         final Class<D> dependency03,
                                                         final Class<E> dependency04,
                                                         final Class<F> dependency05,
                                                         final Class<G> dependency06,
                                                         final Class<H> dependency07,
                                                         final Factory08<X, A, B, C, D, E, F, G, H> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H> T withCustomType(final Class<X> type,
                                                         final Class<A> dependency00,
                                                         final Class<B> dependency01,
                                                         final Class<C> dependency02,
                                                         final Class<D> dependency03,
                                                         final Class<E> dependency04,
                                                         final Class<F> dependency05,
                                                         final Class<G> dependency06,
                                                         final Class<H> dependency07,
                                                         final Factory08<X, A, B, C, D, E, F, G, H> factory,
                                                         final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I> T withCustomType(final Class<X> type,
                                                            final Class<A> dependency00,
                                                            final Class<B> dependency01,
                                                            final Class<C> dependency02,
                                                            final Class<D> dependency03,
                                                            final Class<E> dependency04,
                                                            final Class<F> dependency05,
                                                            final Class<G> dependency06,
                                                            final Class<H> dependency07,
                                                            final Class<I> dependency08,
                                                            final Factory09<X, A, B, C, D, E, F, G, H, I> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I> T withCustomType(final Class<X> type,
                                                            final Class<A> dependency00,
                                                            final Class<B> dependency01,
                                                            final Class<C> dependency02,
                                                            final Class<D> dependency03,
                                                            final Class<E> dependency04,
                                                            final Class<F> dependency05,
                                                            final Class<G> dependency06,
                                                            final Class<H> dependency07,
                                                            final Class<I> dependency08,
                                                            final Factory09<X, A, B, C, D, E, F, G, H, I> factory,
                                                            final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J> T withCustomType(final Class<X> type,
                                                               final Class<A> dependency00,
                                                               final Class<B> dependency01,
                                                               final Class<C> dependency02,
                                                               final Class<D> dependency03,
                                                               final Class<E> dependency04,
                                                               final Class<F> dependency05,
                                                               final Class<G> dependency06,
                                                               final Class<H> dependency07,
                                                               final Class<I> dependency08,
                                                               final Class<J> dependency09,
                                                               final Factory10<X, A, B, C, D, E, F, G, H, I, J> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J> T withCustomType(final Class<X> type,
                                                               final Class<A> dependency00,
                                                               final Class<B> dependency01,
                                                               final Class<C> dependency02,
                                                               final Class<D> dependency03,
                                                               final Class<E> dependency04,
                                                               final Class<F> dependency05,
                                                               final Class<G> dependency06,
                                                               final Class<H> dependency07,
                                                               final Class<I> dependency08,
                                                               final Class<J> dependency09,
                                                               final Factory10<X, A, B, C, D, E, F, G, H, I, J> factory,
                                                               final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K> T withCustomType(final Class<X> type,
                                                                  final Class<A> dependency00,
                                                                  final Class<B> dependency01,
                                                                  final Class<C> dependency02,
                                                                  final Class<D> dependency03,
                                                                  final Class<E> dependency04,
                                                                  final Class<F> dependency05,
                                                                  final Class<G> dependency06,
                                                                  final Class<H> dependency07,
                                                                  final Class<I> dependency08,
                                                                  final Class<J> dependency09,
                                                                  final Class<K> dependency10,
                                                                  final Factory11<X, A, B, C, D, E, F, G, H, I, J, K> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K> T withCustomType(final Class<X> type,
                                                                  final Class<A> dependency00,
                                                                  final Class<B> dependency01,
                                                                  final Class<C> dependency02,
                                                                  final Class<D> dependency03,
                                                                  final Class<E> dependency04,
                                                                  final Class<F> dependency05,
                                                                  final Class<G> dependency06,
                                                                  final Class<H> dependency07,
                                                                  final Class<I> dependency08,
                                                                  final Class<J> dependency09,
                                                                  final Class<K> dependency10,
                                                                  final Factory11<X, A, B, C, D, E, F, G, H, I, J, K> factory,
                                                                  final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L> T withCustomType(final Class<X> type,
                                                                     final Class<A> dependency00,
                                                                     final Class<B> dependency01,
                                                                     final Class<C> dependency02,
                                                                     final Class<D> dependency03,
                                                                     final Class<E> dependency04,
                                                                     final Class<F> dependency05,
                                                                     final Class<G> dependency06,
                                                                     final Class<H> dependency07,
                                                                     final Class<I> dependency08,
                                                                     final Class<J> dependency09,
                                                                     final Class<K> dependency10,
                                                                     final Class<L> dependency11,
                                                                     final Factory12<X, A, B, C, D, E, F, G, H, I, J, K, L> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                dependency11,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L> T withCustomType(final Class<X> type,
                                                                     final Class<A> dependency00,
                                                                     final Class<B> dependency01,
                                                                     final Class<C> dependency02,
                                                                     final Class<D> dependency03,
                                                                     final Class<E> dependency04,
                                                                     final Class<F> dependency05,
                                                                     final Class<G> dependency06,
                                                                     final Class<H> dependency07,
                                                                     final Class<I> dependency08,
                                                                     final Class<J> dependency09,
                                                                     final Class<K> dependency10,
                                                                     final Class<L> dependency11,
                                                                     final Factory12<X, A, B, C, D, E, F, G, H, I, J, K, L> factory,
                                                                     final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .withDependency(dependency11)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M> T withCustomType(final Class<X> type,
                                                                        final Class<A> dependency00,
                                                                        final Class<B> dependency01,
                                                                        final Class<C> dependency02,
                                                                        final Class<D> dependency03,
                                                                        final Class<E> dependency04,
                                                                        final Class<F> dependency05,
                                                                        final Class<G> dependency06,
                                                                        final Class<H> dependency07,
                                                                        final Class<I> dependency08,
                                                                        final Class<J> dependency09,
                                                                        final Class<K> dependency10,
                                                                        final Class<L> dependency11,
                                                                        final Class<M> dependency12,
                                                                        final Factory13<X, A, B, C, D, E, F, G, H, I, J, K, L, M> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                dependency11,
                dependency12,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M> T withCustomType(final Class<X> type,
                                                                        final Class<A> dependency00,
                                                                        final Class<B> dependency01,
                                                                        final Class<C> dependency02,
                                                                        final Class<D> dependency03,
                                                                        final Class<E> dependency04,
                                                                        final Class<F> dependency05,
                                                                        final Class<G> dependency06,
                                                                        final Class<H> dependency07,
                                                                        final Class<I> dependency08,
                                                                        final Class<J> dependency09,
                                                                        final Class<K> dependency10,
                                                                        final Class<L> dependency11,
                                                                        final Class<M> dependency12,
                                                                        final Factory13<X, A, B, C, D, E, F, G, H, I, J, K, L, M> factory,
                                                                        final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .withDependency(dependency11)
                .withDependency(dependency12)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> T withCustomType(final Class<X> type,
                                                                           final Class<A> dependency00,
                                                                           final Class<B> dependency01,
                                                                           final Class<C> dependency02,
                                                                           final Class<D> dependency03,
                                                                           final Class<E> dependency04,
                                                                           final Class<F> dependency05,
                                                                           final Class<G> dependency06,
                                                                           final Class<H> dependency07,
                                                                           final Class<I> dependency08,
                                                                           final Class<J> dependency09,
                                                                           final Class<K> dependency10,
                                                                           final Class<L> dependency11,
                                                                           final Class<M> dependency12,
                                                                           final Class<N> dependency13,
                                                                           final Factory14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                dependency11,
                dependency12,
                dependency13,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> T withCustomType(final Class<X> type,
                                                                           final Class<A> dependency00,
                                                                           final Class<B> dependency01,
                                                                           final Class<C> dependency02,
                                                                           final Class<D> dependency03,
                                                                           final Class<E> dependency04,
                                                                           final Class<F> dependency05,
                                                                           final Class<G> dependency06,
                                                                           final Class<H> dependency07,
                                                                           final Class<I> dependency08,
                                                                           final Class<J> dependency09,
                                                                           final Class<K> dependency10,
                                                                           final Class<L> dependency11,
                                                                           final Class<M> dependency12,
                                                                           final Class<N> dependency13,
                                                                           final Factory14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> factory,
                                                                           final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .withDependency(dependency11)
                .withDependency(dependency12)
                .withDependency(dependency13)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> T withCustomType(final Class<X> type,
                                                                              final Class<A> dependency00,
                                                                              final Class<B> dependency01,
                                                                              final Class<C> dependency02,
                                                                              final Class<D> dependency03,
                                                                              final Class<E> dependency04,
                                                                              final Class<F> dependency05,
                                                                              final Class<G> dependency06,
                                                                              final Class<H> dependency07,
                                                                              final Class<I> dependency08,
                                                                              final Class<J> dependency09,
                                                                              final Class<K> dependency10,
                                                                              final Class<L> dependency11,
                                                                              final Class<M> dependency12,
                                                                              final Class<N> dependency13,
                                                                              final Class<O> dependency14,
                                                                              final Factory15<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                dependency11,
                dependency12,
                dependency13,
                dependency14,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> T withCustomType(final Class<X> type,
                                                                              final Class<A> dependency00,
                                                                              final Class<B> dependency01,
                                                                              final Class<C> dependency02,
                                                                              final Class<D> dependency03,
                                                                              final Class<E> dependency04,
                                                                              final Class<F> dependency05,
                                                                              final Class<G> dependency06,
                                                                              final Class<H> dependency07,
                                                                              final Class<I> dependency08,
                                                                              final Class<J> dependency09,
                                                                              final Class<K> dependency10,
                                                                              final Class<L> dependency11,
                                                                              final Class<M> dependency12,
                                                                              final Class<N> dependency13,
                                                                              final Class<O> dependency14,
                                                                              final Factory15<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> factory,
                                                                              final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .withDependency(dependency11)
                .withDependency(dependency12)
                .withDependency(dependency13)
                .withDependency(dependency14)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> T withCustomType(final Class<X> type,
                                                                                 final Class<A> dependency00,
                                                                                 final Class<B> dependency01,
                                                                                 final Class<C> dependency02,
                                                                                 final Class<D> dependency03,
                                                                                 final Class<E> dependency04,
                                                                                 final Class<F> dependency05,
                                                                                 final Class<G> dependency06,
                                                                                 final Class<H> dependency07,
                                                                                 final Class<I> dependency08,
                                                                                 final Class<J> dependency09,
                                                                                 final Class<K> dependency10,
                                                                                 final Class<L> dependency11,
                                                                                 final Class<M> dependency12,
                                                                                 final Class<N> dependency13,
                                                                                 final Class<O> dependency14,
                                                                                 final Class<P> dependency15,
                                                                                 final Factory16<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> factory) {
        return withCustomType(
                type,
                dependency00,
                dependency01,
                dependency02,
                dependency03,
                dependency04,
                dependency05,
                dependency06,
                dependency07,
                dependency08,
                dependency09,
                dependency10,
                dependency11,
                dependency12,
                dependency13,
                dependency14,
                dependency15,
                factory,
                PROTOTYPE
        );
    }

    default <X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> T withCustomType(final Class<X> type,
                                                                                 final Class<A> dependency00,
                                                                                 final Class<B> dependency01,
                                                                                 final Class<C> dependency02,
                                                                                 final Class<D> dependency03,
                                                                                 final Class<E> dependency04,
                                                                                 final Class<F> dependency05,
                                                                                 final Class<G> dependency06,
                                                                                 final Class<H> dependency07,
                                                                                 final Class<I> dependency08,
                                                                                 final Class<J> dependency09,
                                                                                 final Class<K> dependency10,
                                                                                 final Class<L> dependency11,
                                                                                 final Class<M> dependency12,
                                                                                 final Class<N> dependency13,
                                                                                 final Class<O> dependency14,
                                                                                 final Class<P> dependency15,
                                                                                 final Factory16<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> factory,
                                                                                 final ReusePolicy reusePolicy) {
        final CustomType customType = customType(type)
                .withDependency(dependency00)
                .withDependency(dependency01)
                .withDependency(dependency02)
                .withDependency(dependency03)
                .withDependency(dependency04)
                .withDependency(dependency05)
                .withDependency(dependency06)
                .withDependency(dependency07)
                .withDependency(dependency08)
                .withDependency(dependency09)
                .withDependency(dependency10)
                .withDependency(dependency11)
                .withDependency(dependency12)
                .withDependency(dependency13)
                .withDependency(dependency14)
                .withDependency(dependency15)
                .usingFactory(factory);
        return withCustomType(customType, reusePolicy);
    }
}
