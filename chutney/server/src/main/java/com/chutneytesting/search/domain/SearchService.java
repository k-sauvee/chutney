/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.search.domain;

import com.chutneytesting.search.api.dto.Hit;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final List<SearchRepository> searchRepositories;

    public SearchService(List<SearchRepository> searchRepositories) {
        this.searchRepositories = searchRepositories;
    }

    public List<Hit> search(String query) {
        return searchRepositories.stream()
            .map(repo -> repo.search(query))
            .flatMap(Collection::stream).toList();
    }
}
