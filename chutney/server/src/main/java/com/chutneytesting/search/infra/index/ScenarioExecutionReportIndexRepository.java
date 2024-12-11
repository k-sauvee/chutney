/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.search.infra.index;

import static org.apache.lucene.document.Field.Store;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.search.api.dto.Hit;
import com.chutneytesting.search.domain.SearchRepository;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import java.util.List;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Repository;

@Repository
public class ScenarioExecutionReportIndexRepository implements SearchRepository {

    public static final String SCENARIO_EXECUTION_REPORT = "scenarioExecutionReport";
    public static final String WHAT = "what";
    private static final String SCENARIO_EXECUTION_ID = "scenarioExecutionId";
    private static final String SCENARIO_TITLE = "scenarioTitle";
    private static final String REPORT = "report";
    private final IndexRepository indexRepository;

    public ScenarioExecutionReportIndexRepository(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public void save(ScenarioExecutionReportEntity report) {
        ExecutionHistory.Execution execution = report.toDomain();
        Document document = new Document();
        document.add(new StringField(WHAT, SCENARIO_EXECUTION_REPORT, Store.YES));
        document.add(new StringField(SCENARIO_EXECUTION_ID, execution.executionId().toString(),Store.YES));
        document.add(new StringField(SCENARIO_TITLE, execution.testCaseTitle(),Store.YES));
        document.add(new TextField(REPORT, execution.report().toLowerCase(), Store.YES));
        // for sorting
        document.add(new SortedDocValuesField(SCENARIO_EXECUTION_ID, new BytesRef(report.scenarioExecutionId().toString().getBytes()) ));
        indexRepository.index(document);
    }

    public void saveAll(List<ScenarioExecutionReportEntity> reports) {
        reports.forEach(this::save);
    }

    public void delete(Long scenarioExecutionId) {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        Query idQuery = new TermQuery(new Term(SCENARIO_EXECUTION_ID, scenarioExecutionId.toString()));
        BooleanQuery query = new BooleanQuery.Builder()
            .add(idQuery, BooleanClause.Occur.MUST)
            .add(whatQuery, BooleanClause.Occur.MUST)
            .build();
        indexRepository.delete(query);
    }

    public void deleteAllById(Set<Long> scenarioExecutionIds) {
        scenarioExecutionIds.forEach(this::delete);
    }


    public List<Long> idsByKeywordInReport(String keyword) {
        return search(keyword).stream()
            .map(Hit::id)
            .map(Long::parseLong)
            .toList();

    }

    @Override
    public List<Hit> search(String keyword) {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        Query reportQuery = new WildcardQuery(new Term(REPORT,  "*" + keyword.toLowerCase() + "*"));

        BooleanQuery query = new BooleanQuery.Builder()
            .add(reportQuery, BooleanClause.Occur.MUST)
            .add(whatQuery, BooleanClause.Occur.MUST)
            .build();

        Sort sort = new Sort(SortField.FIELD_SCORE, new SortField(SCENARIO_EXECUTION_ID, SortField.Type.STRING, true));

        return indexRepository.search(query, 100, sort)
            .stream()
            .map(doc -> new Hit(doc.get(SCENARIO_EXECUTION_ID), doc.get(SCENARIO_TITLE), doc.get(REPORT), SCENARIO_EXECUTION_REPORT))
            .toList();
    }
}
