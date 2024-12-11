/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.search.domain;

import com.chutneytesting.search.api.dto.Hit;
import java.util.List;

public interface SearchRepository {
    List<Hit> search(String keyword);
}
