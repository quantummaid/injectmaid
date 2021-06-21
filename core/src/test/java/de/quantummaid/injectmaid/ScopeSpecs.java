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

import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.api.customtype.api.CustomType;
import de.quantummaid.injectmaid.domain.*;
import de.quantummaid.injectmaid.domain.dependency.*;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.injectmaid.api.ReusePolicy.DEFAULT_SINGLETON;
import static de.quantummaid.injectmaid.api.ReusePolicy.EAGER_SINGLETON;
import static de.quantummaid.injectmaid.domain.Request.request;
import static de.quantummaid.injectmaid.testsupport.TestSupport.catchException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ScopeSpecs {

    @Test
    public void scopeObjectCanBeInjectedDirectly() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, builder -> {
                })
                .build();
        final Request request = request("foo");
        final Injector scopedInjectMaid = injectMaid.enterScope(Request.class, request);

        final Request instance = scopedInjectMaid.getInstance(Request.class);
        assertThat(instance, notNullValue());
        assertThat(instance.username, is("foo"));
    }

    @Test
    public void scopeObjectCanBeInjectedIndirectly() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, builder -> {
                    builder.withCustomType(
                            CustomType.customType(StringWrapper.class)
                                    .withDependency(Request.class)
                                    .usingFactory(request -> new StringWrapper(request.username))
                    );
                })
                .build();
        final Request request = request("foo");
        final Injector scopedInjectMaid = injectMaid.enterScope(Request.class, request);

        final StringWrapper instance = scopedInjectMaid.getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance.string, is("foo"));
    }

    @Test
    public void scopeObjectCannotBeInjectedOutsideOfScope() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, builder -> {
                })
                .build();
        final Request request = request("foo");
        final Injector scopedInjectMaid = injectMaid.enterScope(Request.class, request);
        assertThat(scopedInjectMaid, notNullValue());

        final Exception exception = catchException(() -> injectMaid.getInstance(Request.class));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Tried to instantiate unregistered type 'de.quantummaid.injectmaid.domain.Request'"));
    }

    @Test
    public void scopeObjectCanBeNull() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, builder -> {
                })
                .build();
        final Injector scopedInjectMaid = injectMaid.enterScope(Request.class, null);

        final Request instance = scopedInjectMaid.getInstance(Request.class);
        assertThat(instance, nullValue());
    }

    @Test
    public void nonRegisteredScopeCannotBeEntered() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> "foo")
                .withScope(InputStream.class, builder -> {
                })
                .withScope(ByteBuffer.class, builder ->
                        builder.withScope(Throwable.class, builder1 -> {
                        }))
                .build();
        final Exception exception = catchException(() -> injectMaid.enterScope(Object.class, null));
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Tried to enter unknown scope '/Object' with object 'null'. " +
                "Registered scopes: [/, /ByteBuffer, /ByteBuffer/Throwable, /InputStream]"));
    }

    @Test
    public void singletonsInDifferentScopesAreDifferent() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, injectMaidBuilder -> injectMaidBuilder.withType(NumberedType.class, DEFAULT_SINGLETON))
                .build();

        final Injector requestScoped1 = injectMaid.enterScope(Request.class, request("foo"));
        final NumberedType instance1A = requestScoped1.getInstance(NumberedType.class);
        assertThat(instance1A.instanceNumber(), is(0));
        final NumberedType instance1B = requestScoped1.getInstance(NumberedType.class);
        assertThat(instance1B.instanceNumber(), is(0));

        final Injector requestScoped2 = injectMaid.enterScope(Request.class, request("bar"));
        final NumberedType instance2A = requestScoped2.getInstance(NumberedType.class);
        assertThat(instance2A.instanceNumber(), is(1));
        final NumberedType instance2B = requestScoped2.getInstance(NumberedType.class);
        assertThat(instance2B.instanceNumber(), is(1));
    }

    @Test
    public void typesCannotBeInstantiatedOutsideTheScopeTheyWereRegisteredIn() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, scope -> scope.withType(ZeroArgumentsConstructorType.class))
                .build();

        final Exception exception = catchException(() -> injectMaid.getInstance(ZeroArgumentsConstructorType.class));
        assertThat(exception.getMessage(), is("Tried to instantiate unregistered type 'de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType'"));

        final ZeroArgumentsConstructorType instance = injectMaid.enterScope(Request.class, request("foo")).getInstance(ZeroArgumentsConstructorType.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(ZeroArgumentsConstructorType.class));
    }

    @Test
    public void typeCanBeRegisteredTwiceInSameScope() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Request.class, scope -> {
                    scope.withType(NumberedType.class);
                    scope.withType(NumberedType.class);
                })
                .build();
        final NumberedType instance = injectMaid.enterScope(request("foo")).getInstance(NumberedType.class);
        assertThat(instance, is(notNullValue()));
    }

    @Test
    public void sameScopeCanBeConfiguredInTwoSeparateStatements() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Object.class, builder -> builder.withCustomType(String.class, () -> "foo"))
                .withScope(Object.class, builder -> builder.withCustomType(StringWrapper.class, String.class, StringWrapper::new))
                .build();
        final StringWrapper instance = injectMaid.enterScope(Object.class, null).getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("foo"));
    }

    @Test
    public void nestedScopesCanBeConfiguredInTwoSeparateStatements() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Object.class, x ->
                        x.withScope(Integer.class, builder ->
                                builder.withCustomType(String.class, () -> "foo")
                        )
                )
                .withScope(Object.class, x ->
                        x.withScope(Integer.class, builder ->
                                builder.withCustomType(StringWrapper.class, String.class, StringWrapper::new)
                        )
                )
                .build();
        final StringWrapper instance = injectMaid
                .enterScope(Object.class, null)
                .enterScope(Integer.class, null)
                .getInstance(StringWrapper.class);
        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(StringWrapper.class));
        assertThat(instance.string, is("foo"));
    }

    @Test
    public void cannotRegisterSameScopeTwiceAtDifferentLevels() {
        final Exception exception = catchException(() -> anInjectMaid()
                .withScope(Object.class, builder -> builder.withScope(Object.class, innerBuilder -> {
                    innerBuilder.withCustomType(String.class, () -> "foo");
                }))
                .build());
        assertThat(exception, instanceOf(InjectMaidException.class));
        assertThat(exception.getMessage(), is("Scope type 'Object' is already used in scope '/Object'"));
    }

    @Test
    public void aTypeThatHasBeenRegisteredAsAScopeCanBeRegisteredNormallyOutsideOfTheScope() {
        final InjectMaid injectMaid = anInjectMaid()
                .withCustomType(String.class, () -> "foo")
                .withScope(String.class, builder -> {
                })
                .build();
        final String instance1 = injectMaid.getInstance(String.class);
        assertThat(instance1, is("foo"));

        final String instance2 = injectMaid.enterScope(String.class, "bar").getInstance(String.class);
        assertThat(instance2, is("bar"));
    }

    @Test
    public void typeThatIsDependedOnFromTwoDifferentScopes() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Integer.class, builder -> builder.withType(DependentA.class))
                .withScope(Long.class, builder -> builder.withType(DependentB.class))
                .build();

        final Exception exception1 = catchException(() -> injectMaid.getInstance(Dependency.class));
        assertThat(exception1, instanceOf(InjectMaidException.class));
        assertThat(exception1.getMessage(), is("Tried to instantiate unregistered type 'de.quantummaid.injectmaid.domain.dependency.Dependency'"));

        final Dependency instance1 = injectMaid.enterScope(Integer.class, null).getInstance(Dependency.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1, instanceOf(Dependency.class));

        final Dependency instance2 = injectMaid.enterScope(Long.class, null).getInstance(Dependency.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2, instanceOf(Dependency.class));
    }

    @Test
    public void typeThatIsDependedOnFromRootAndNestedScope() {
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(Integer.class, builder -> builder.withType(DependentA.class))
                .withType(DependentB.class)
                .withType(Dependency.class, DEFAULT_SINGLETON)
                .build();

        final Dependency instance1 = injectMaid.enterScope(Integer.class, null).getInstance(Dependency.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1, instanceOf(Dependency.class));

        final Dependency instance2 = injectMaid.getInstance(Dependency.class);
        assertThat(instance2, notNullValue());
        assertThat(instance2, instanceOf(Dependency.class));
    }

    @Test
    public void typesAreNotRegisteredTwiceIfNotNecessary() {
        final InjectMaid injectMaid = anInjectMaid()
                .withType(Layer3.class)
                .withScope(String.class, builder -> builder.withType(Layer1.class))
                .build();
        final String debugInformation = injectMaid.debugInformation();
        assertThat(debugInformation, is("" +
                "/ de.quantummaid.injectmaid.domain.dependency.Dependency (PROTOTYPE)\n" +
                "/ de.quantummaid.injectmaid.domain.dependency.Layer1 (PROTOTYPE)\n" +
                "/ de.quantummaid.injectmaid.domain.dependency.Layer2 (PROTOTYPE)\n" +
                "/ de.quantummaid.injectmaid.domain.dependency.Layer3 (PROTOTYPE)\n" +
                "/String de.quantummaid.injectmaid.domain.dependency.Dependency (PROTOTYPE)\n" +
                "/String de.quantummaid.injectmaid.domain.dependency.Layer1 (PROTOTYPE)\n" +
                "/String java.lang.String (PROTOTYPE)"));
    }

    @Test
    public void eagerSingletonsOnScopesAreInitializedOnScopeEntry() {
        NumberedType.counter = 0;
        final InjectMaid injectMaid = anInjectMaid()
                .withScope(String.class, builder -> builder.withType(NumberedType.class, EAGER_SINGLETON))
                .build();
        assertThat(NumberedType.counter, is(0));
        final Injector scope = injectMaid.enterScope("");
        assertThat(NumberedType.counter, is(1));

        final NumberedType instance0 = scope.getInstance(NumberedType.class);
        assertThat(instance0, notNullValue());
        assertThat(instance0.instanceNumber(), is(0));

        final NumberedType instance1 = scope.getInstance(NumberedType.class);
        assertThat(instance1, notNullValue());
        assertThat(instance1.instanceNumber(), is(0));

        assertThat(NumberedType.counter, is(1));
    }
}
