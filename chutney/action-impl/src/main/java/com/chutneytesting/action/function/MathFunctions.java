/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.function;

import com.chutneytesting.action.spi.SpelFunction;

public class MathFunctions {

    @SpelFunction
    public static Integer abs(Integer a) {
        return Math.abs(a);
    }

    @SpelFunction
    public static Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @SpelFunction
    public static Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }
}
