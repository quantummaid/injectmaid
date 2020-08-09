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

package de.quantummaid.injectmaid.failing;

import de.quantummaid.injectmaid.domain.ZeroArgumentsConstructorType;

public final class TooManyFactoriesType {

    private TooManyFactoriesType() {
    }

    public static TooManyFactoriesType factory1() {
        return new TooManyFactoriesType();
    }

    public static TooManyFactoriesType factory2(final ZeroArgumentsConstructorType argument1) {
        return new TooManyFactoriesType();
    }

    public static TooManyFactoriesType factory3(final ZeroArgumentsConstructorType argument1,
                                                final ZeroArgumentsConstructorType argument2) {
        return new TooManyFactoriesType();
    }
}
