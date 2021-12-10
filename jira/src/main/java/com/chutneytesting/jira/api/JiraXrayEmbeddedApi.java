package com.chutneytesting.jira.api;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.chutneytesting.jira.domain.JiraXrayService;

public class JiraXrayEmbeddedApi {

    private final JiraXrayService jiraXrayService;

    public JiraXrayEmbeddedApi(JiraXrayService jiraXrayService) {
        this.jiraXrayService = jiraXrayService;
    }

    public void updateTestExecution(Long campaignId, String scenarioId, ReportForJira report) {
        if (report != null && isNotEmpty(scenarioId) && campaignId != null) {
            jiraXrayService.updateTestExecution(campaignId, scenarioId, report);
        }
    }
}

