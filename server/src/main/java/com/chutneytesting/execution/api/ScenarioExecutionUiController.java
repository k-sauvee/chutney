package com.chutneytesting.execution.api;

import static com.chutneytesting.tools.orient.ComposableIdUtils.fromFrontId;

import com.chutneytesting.design.domain.scenario.TestCase;
import com.chutneytesting.design.domain.scenario.TestCaseRepository;
import com.chutneytesting.execution.domain.ExecutionRequest;
import com.chutneytesting.execution.domain.report.ScenarioExecutionReport;
import com.chutneytesting.execution.domain.scenario.ScenarioExecutionEngine;
import com.chutneytesting.execution.domain.scenario.ScenarioExecutionEngineAsync;
import com.chutneytesting.execution.domain.scenario.composed.ExecutableComposedStep;
import com.chutneytesting.execution.domain.scenario.composed.ExecutableStepRepository;
import com.chutneytesting.security.infra.SpringUserService;
import com.chutneytesting.tools.ui.KeyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
@RestController
public class ScenarioExecutionUiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioExecutionUiController.class);
    private final ScenarioExecutionEngine executionEngine;
    private final ScenarioExecutionEngineAsync executionEngineAsync;
    private final TestCaseRepository testCaseRepository;
    private final ObjectMapper objectMapper;
    private final ObjectMapper reportObjectMapper;
    private final ExecutableStepRepository stepRepository;
    private final SpringUserService userService;

    ScenarioExecutionUiController(
        ScenarioExecutionEngine executionEngine,
        ScenarioExecutionEngineAsync executionEngineAsync,
        TestCaseRepository testCaseRepository,
        ObjectMapper objectMapper,
        @Qualifier("reportObjectMapper") ObjectMapper reportObjectMapper,
        ExecutableStepRepository stepRepository,
        SpringUserService userService
    ) {
        this.executionEngine = executionEngine;
        this.executionEngineAsync = executionEngineAsync;
        this.testCaseRepository = testCaseRepository;
        this.objectMapper = objectMapper;
        this.reportObjectMapper = reportObjectMapper;
        this.stepRepository = stepRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/ui/scenario/execution/v1/{scenarioId}/{env}")
    public String executeScenario(@PathVariable("scenarioId") String scenarioId, @PathVariable("env") String env) throws IOException {
        LOGGER.debug("executeScenario for scenarioId='{}'", scenarioId);
        TestCase testCase = testCaseRepository.findById(scenarioId);
        String userId = userService.currentUser().getId();
        ScenarioExecutionReport report = executionEngine.execute(new ExecutionRequest(testCase, env, userId));
        return reportObjectMapper.writeValueAsString(report);
    }

    @PreAuthorize("hasAuthority('COMPONENT_WRITE')")
    @PostMapping(path = "/api/ui/component/execution/v1/{componentId}/{env}")
    public String executeComponent(@PathVariable("componentId") String componentId, @PathVariable("env") String env) throws IOException {
        LOGGER.debug("executeComponent for componentId={{}] on env [{}]", componentId, env);
        ExecutableComposedStep composedStep = stepRepository.findExecutableById(fromFrontId(Optional.of(componentId)));
        String userId = userService.currentUser().getId();
        ScenarioExecutionReport report = executionEngine.execute(composedStep, env, userId);
        return reportObjectMapper.writeValueAsString(report);
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/idea/scenario/execution/{env}")
    public String executeScenarioWitRawContent(@RequestBody IdeaRequest ideaRequest, @PathVariable("env") String env) throws IOException {
        LOGGER.debug("execute Scenario v2 for content='{}' with parameters '{}'", ideaRequest.getContent(), ideaRequest.getParams());
        String userId = userService.currentUser().getId();
        ScenarioExecutionReport report = executionEngine.execute(ideaRequest.getContent(), ideaRequest.getParams(), env, userId);
        return objectMapper.writeValueAsString(report);
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/ui/scenario/executionasync/v1/{scenarioId}/{env}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String executeScenarioAsyncWithExecutionParameters(@PathVariable("scenarioId") String scenarioId, @PathVariable("env") String env, @RequestBody List<KeyValue> executionParametersKV) {
        LOGGER.debug("execute async scenario '{}' using parameters '{}'", scenarioId, executionParametersKV);
        TestCase testCase = testCaseRepository.findById(fromFrontId(Optional.of(scenarioId)));
        Map<String, String> executionParameters = KeyValue.toMap(executionParametersKV);
        String userId = userService.currentUser().getId();
        return executionEngineAsync.execute(new ExecutionRequest(testCase, env, executionParameters, userId)).toString();
    }

    @PreAuthorize("hasAuthority('SCENARIO_READ')")
    @GetMapping(path = "/api/ui/scenario/executionasync/v1/{scenarioId}/execution/{executionId}")
    public Flux<ServerSentEvent<String>> followScenarioExecution(@PathVariable("scenarioId") String scenarioId, @PathVariable("executionId") Long executionId) {
        LOGGER.debug("followScenarioExecution for scenarioId='{}' and executionID='{}'", scenarioId, executionId);
        return createScenarioExecutionSSEFlux(
            executionEngineAsync.followExecution(scenarioId, executionId)
        );
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/ui/scenario/executionasync/v1/{scenarioId}/execution/{executionId}/stop")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void stopExecution(@PathVariable("scenarioId") String scenarioId, @PathVariable("executionId") Long executionId) {
        LOGGER.debug("Stop for scenarioId='{}' and executionID='{}'", scenarioId, executionId);
        executionEngineAsync.stop(scenarioId, executionId);
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/ui/scenario/executionasync/v1/{scenarioId}/execution/{executionId}/pause")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void pauseExecution(@PathVariable("scenarioId") String scenarioId, @PathVariable("executionId") Long executionId) {
        LOGGER.debug("Pause for scenarioId='{}' and executionID='{}'", scenarioId, executionId);
        executionEngineAsync.pause(scenarioId, executionId);
    }

    @PreAuthorize("hasAuthority('SCENARIO_EXECUTE')")
    @PostMapping(path = "/api/ui/scenario/executionasync/v1/{scenarioId}/execution/{executionId}/resume")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void resumeExecution(@PathVariable("scenarioId") String scenarioId, @PathVariable("executionId") Long executionId) {
        LOGGER.debug("Resume for scenarioId='{}' and executionID='{}'", scenarioId, executionId);
        executionEngineAsync.resume(scenarioId, executionId);
    }

    private Flux<ServerSentEvent<String>> createScenarioExecutionSSEFlux(Observable<ScenarioExecutionReport> scenarioExecutionReports) {
        return Flux.from(scenarioExecutionReports.map(
            reportEvent -> ServerSentEvent.<String>builder()
                .id(String.valueOf(reportEvent.executionId))
                .event(reportEvent.report.isTerminated() ? "last" : "partial")
                .data(reportObjectMapper.writeValueAsString(reportEvent))
                .build()
        ).toFlowable(BackpressureStrategy.BUFFER));
    }
}
