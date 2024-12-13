/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MathFunctionsTest {

    @Test
    void abs_minus_2_should_return_2() {
        assertThat(MathFunctions.abs(-2)).isEqualTo(2);
    }

    @Test
    void min_4_and_9_should_return_4() {
        assertThat(MathFunctions.min(4, 9)).isEqualTo(4);
    }

    @Test
    void max_4_and_9_should_return_9() {
        assertThat(MathFunctions.max(4, 9)).isEqualTo(9);
    }
}
