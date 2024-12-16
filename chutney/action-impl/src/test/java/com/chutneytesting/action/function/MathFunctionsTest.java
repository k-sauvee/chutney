/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MathFunctionsTest {

    @Test
    void abs_unexpected_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                short a = 2;
                MathFunctions.abs(a);
            }
        );
    }

    @Test
    void abs_minus_2_should_return_2() {
        assertThat(MathFunctions.abs(-2d)).isEqualTo(2d);
        assertThat(MathFunctions.abs(-2f)).isEqualTo(2f);
        assertThat(MathFunctions.abs(-2)).isEqualTo(2);
        assertThat(MathFunctions.abs(-2L)).isEqualTo(2L);
    }

    @Test
    void min_unexpected_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                MathFunctions.min(4f, 9);
            }
        );
    }

    @Test
    void min_4_and_9_should_return_4() {
        assertThat(MathFunctions.min(4d, 9d)).isEqualTo(4d);
        assertThat(MathFunctions.min(4f, 9f)).isEqualTo(4f);
        assertThat(MathFunctions.min(4, 9)).isEqualTo(4);
        assertThat(MathFunctions.min(4L, 9L)).isEqualTo(4L);
    }

    @Test
    void max_unexpected_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                MathFunctions.max(4f, 9);
            }
        );
    }


    @Test
    void max_4_and_9_should_return_9() {
        assertThat(MathFunctions.max(4d, 9d)).isEqualTo(9d);
        assertThat(MathFunctions.max(4f, 9f)).isEqualTo(9f);
        assertThat(MathFunctions.max(4, 9)).isEqualTo(9);
        assertThat(MathFunctions.max(4L, 9L)).isEqualTo(9L);
    }
}
