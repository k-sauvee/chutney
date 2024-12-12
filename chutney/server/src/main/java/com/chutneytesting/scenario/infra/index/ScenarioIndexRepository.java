/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.scenario.infra.index;

import static org.apache.lucene.search.BooleanClause.Occur;

import com.chutneytesting.index.api.dto.Hit;
import com.chutneytesting.index.domain.IndexRepository;
import com.chutneytesting.index.infra.lucene.LuceneIndexRepository;
import com.chutneytesting.scenario.infra.jpa.ScenarioEntity;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Repository;

@Repository
public class ScenarioIndexRepository implements IndexRepository {
    private final String WHAT_VALUE = "scenario";
    public final String WHAT = "what";
    private final String ID = "id";
    private final String TITLE = "title";
    private final String DESCRIPTION = "description";
    private final String CONTENT = "content";
    private final String TAGS = "tags";
    private final LuceneIndexRepository luceneIndexRepository;

    public ScenarioIndexRepository(LuceneIndexRepository luceneIndexRepository) {
        this.luceneIndexRepository = luceneIndexRepository;
    }

    public void save(ScenarioEntity scenario) {
        Document document = new Document();
        document.add(new StringField(WHAT, WHAT_VALUE, Field.Store.YES));
        document.add(new TextField(ID, scenario.getId().toString(), Field.Store.YES));
        document.add(new TextField(TITLE, scenario.getTitle().toLowerCase(), Field.Store.YES));
        document.add(new TextField(DESCRIPTION, scenario.getDescription().toLowerCase(), Field.Store.YES));
        document.add(new TextField(CONTENT, scenario.getContent().toLowerCase(), Field.Store.YES));
        document.add(new TextField(TAGS, scenario.getTags().toLowerCase(), Field.Store.YES));
        // for sorting
        document.add(new SortedDocValuesField(ID, new BytesRef(report.scenarioExecutionId().toString().getBytes()) ));
        luceneIndexRepository.index(document);
    }

    @Override
    public List<Hit> search(String keyword) {
        Query whatQuery = new TermQuery(new Term(WHAT, WHAT_VALUE));
        BooleanQuery propertiesQuery;
        propertiesQuery = new BooleanQuery.Builder()
            .setMinimumNumberShouldMatch(1)
            .add(likeQuery(ID, keyword), Occur.SHOULD)
            .add(likeQuery(TITLE, keyword), Occur.SHOULD)
            .add(likeQuery(DESCRIPTION, keyword), Occur.SHOULD)
            .add(likeQuery(CONTENT, keyword), Occur.SHOULD)
            .add(likeQuery(TAGS, keyword), Occur.SHOULD)
            .build();

        BooleanQuery query = new BooleanQuery.Builder()
            .add(whatQuery, Occur.MUST)
            .add(propertiesQuery, Occur.MUST)
            .build();

        Sort sort = new Sort(SortField.FIELD_SCORE, new SortField(ID, SortField.Type.STRING, true));

        return luceneIndexRepository.search(query, 100, sort)
            .stream()
            .map(doc -> new Hit(doc.get(ID),
                doc.get(TITLE),
                doc.get(DESCRIPTION),
                doc.get(CONTENT),
                Arrays.stream(doc.getValues(TAGS)).toList(),
                WHAT_VALUE))
            .toList();
    }

    private WildcardQuery likeQuery(String column, String keyword) {
        return new WildcardQuery(new Term(column, "*" + keyword.toLowerCase() + "*"));
    }
}
