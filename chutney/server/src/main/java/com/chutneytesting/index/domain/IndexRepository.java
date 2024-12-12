/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.domain;

import com.chutneytesting.index.api.dto.Hit;
import java.util.List;

public interface IndexRepository {
    List<Hit> search(String keyword);
}
