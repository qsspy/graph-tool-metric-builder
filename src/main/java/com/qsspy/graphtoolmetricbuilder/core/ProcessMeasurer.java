package com.qsspy.graphtoolmetricbuilder.core;


import com.qsspy.graphtoolmetricbuilder.exception.InvalidMeasurementIdException;
import com.qsspy.graphtoolmetricbuilder.exception.ProcessMeasurerInitializationException;
import com.qsspy.graphtoolmetricbuilder.model.*;
import com.qsspy.graphtoolmetricbuilder.util.EnumUtils;
import com.qsspy.graphtoolmetricbuilder.util.StringUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class ProcessMeasurer {

    private static final String SUCCESS_COUNTER_SUFFIX = ".counter.success";
    private static final String SUCCESS_COUNTER_NAME = "successful executions counter";
    private static final String FAILURE_COUNTER_SUFFIX = ".counter.failure";
    private static final String FAILURE_COUNTER_NAME = "failed executions counter";
    private static final String TOTAL_COUNTER_SUFFIX = ".counter.total";
    private static final String TOTAL_COUNTER_NAME = "total executions count";
    private static final String EXECUTION_TIMER_SUFFIX = ".timer.execution.success";
    private static final String EXECUTION_TIMER_NAME = "process execution time";

    private final Map<String, Long> measuringStartTimesMap = new ConcurrentHashMap<>();

    @Getter
    private final String processName;
    @Getter
    private final String processDescription;

    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter totalExecutionsCounter;
    private final Timer executionTimer;

    private ProcessMeasurer(final MeterRegistry meterRegistry,
                            final String processName,
                            final String processDescription,
                            final ProcessScope processScope,
                            final ProcessOperationType operationType)
    {
        this.successCounter = initiateCounter(meterRegistry, processName, processDescription, processScope, operationType, SUCCESS_COUNTER_SUFFIX, SUCCESS_COUNTER_NAME, true);
        this.failureCounter = initiateCounter(meterRegistry, processName, processDescription, processScope, operationType, FAILURE_COUNTER_SUFFIX, FAILURE_COUNTER_NAME, false);
        this.totalExecutionsCounter = initiateCounter(meterRegistry, processName, processDescription, processScope, operationType, TOTAL_COUNTER_SUFFIX, TOTAL_COUNTER_NAME, true);
        this.executionTimer = initiateTimer(meterRegistry, processName, processDescription, processScope, operationType, EXECUTION_TIMER_SUFFIX, EXECUTION_TIMER_NAME, true);

        this.processName = processName;
        this.processDescription = processDescription;
    }

    /**
     *
     * @param meterRegistry meter registry to be used
     * @return
     */
    public static ProcessMeasurerBuilder builder(final MeterRegistry meterRegistry) {
        return new ProcessMeasurerBuilder(meterRegistry);
    }

    /**
     * Starts process measurement and returns ID correlated with this measurement
     *
     * @return measurement ID
     */
    public String startMeasurement() {
        final String id = UUID.randomUUID().toString();
        measuringStartTimesMap.put(id, new Date().getTime());
        return id;
    }

    /**
     * Finishes measuring of successfully executed process. This means incrementing success and total counter
     * and registering new timer measurement
     *
     * @param measurementId measurement ID
     */
    public void finishWithSuccess(final String measurementId) {
        final Long startTime = measuringStartTimesMap.get(measurementId);
        if(isNull(startTime)) {
            throw new InvalidMeasurementIdException("Could not find measurement with provided id");
        }
        final long processExecutionTime = new Date().getTime() - startTime;
        executionTimer.record(processExecutionTime, TimeUnit.MILLISECONDS);
        measuringStartTimesMap.remove(measurementId);
        successCounter.increment();
        totalExecutionsCounter.increment();
    }

    /**
     * Finishes measuring of failed process. This means incrementing success and total counter
     *
     * @param measurementId
     */
    public void finishWithFailure(final String measurementId) {
        final Long startTime = measuringStartTimesMap.remove(measurementId);
        if(isNull(startTime)) {
            throw new InvalidMeasurementIdException("Could not find measurement with provided id");
        }
        failureCounter.increment();
        totalExecutionsCounter.increment();
    }

    private Counter initiateCounter(final MeterRegistry meterRegistry,
                                    final String processName,
                                    final String processDescription,
                                    final ProcessScope processScope,
                                    final ProcessOperationType operationType,
                                    final String counterSuffix,
                                    final String counterName,
                                    final boolean representsSuccess)
    {
        return Counter
                .builder(processName + counterSuffix)
                .description(resolveDescription(processDescription, processScope, operationType, counterName))
                .tags(
                        GraphToolTags.PROCESSORS_KEY.getLiteral(),     GraphToolTags.PROCESSORS_VALUE.getLiteral(),
                        GraphToolTags.SCOPE_KEY.getLiteral(),          processScope.name(),
                        GraphToolTags.OPERATION_TYPE_KEY.getLiteral(), EnumUtils.getEnumStringOrEmpty(operationType),
                        GraphToolTags.METRIC_TYPE_KEY.getLiteral(),    MetricType.COUNTER.name(),
                        GraphToolTags.PROCESS_STATUS_KEY.getLiteral(), ProcessStatus.basedOnBoolean(representsSuccess).name()
                )
                .register(meterRegistry);
    }

    private Timer initiateTimer(final MeterRegistry meterRegistry,
                                final String processName,
                                final String processDescription,
                                final ProcessScope processScope,
                                final ProcessOperationType operationType,
                                final String timerSuffix,
                                final String timerName,
                                final boolean representsSuccess)
    {
        return Timer
                .builder(processName + timerSuffix)
                .description(resolveDescription(processDescription, processScope, operationType, timerName))
                .tags(
                        GraphToolTags.PROCESSORS_KEY.getLiteral(),     GraphToolTags.PROCESSORS_VALUE.getLiteral(),
                        GraphToolTags.SCOPE_KEY.getLiteral(),          processScope.name(),
                        GraphToolTags.OPERATION_TYPE_KEY.getLiteral(), EnumUtils.getEnumStringOrEmpty(operationType),
                        GraphToolTags.METRIC_TYPE_KEY.getLiteral(),    MetricType.COUNTER.name(),
                        GraphToolTags.PROCESS_STATUS_KEY.getLiteral(), ProcessStatus.basedOnBoolean(representsSuccess).name()
                )
                .register(meterRegistry);
    }

    private String resolveDescription(final String processDescription, final ProcessScope processScope, final ProcessOperationType operationType, final String metricName) {
        return StringUtils.getCommaAndSpaceSeparatedList(processDescription, processScope.getDescription(), operationType.getDescription(), metricName);
    }

    public static class ProcessMeasurerBuilder {

        private final MeterRegistry meterRegistry;
        private String processName;
        private String processDescription;
        private ProcessScope processScope;
        private ProcessOperationType processOperationType;

        public ProcessMeasurerBuilder(final MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        public ProcessMeasurerBuilder processName(final String processName) {
            this.processName = processName;
            return this;
        }

        public ProcessMeasurerBuilder processDescription(final String processDescription) {
            this.processDescription = processDescription;
            return this;
        }

        public ProcessMeasurerBuilder processScope(final ProcessScope processScope) {
            this.processScope = processScope;
            return this;
        }

        public ProcessMeasurerBuilder processOperationType(final ProcessOperationType processOperationType) {
            this.processOperationType = processOperationType;
            return this;
        }

        public ProcessMeasurer build() {
            if(isNull(this.processScope) || isNull(this.processName)) {
                throw new ProcessMeasurerInitializationException("Process name and process scope cannot be null.");
            }
            return new ProcessMeasurer(meterRegistry, processName, processDescription, processScope, processOperationType);
        }
    }
}
