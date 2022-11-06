package com.qsspy.graphtoolmetricbuilder.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum GraphToolTags {

    PROCESSORS_KEY("processors"),
    PROCESSORS_VALUE("graph-tool"),

    SCOPE_KEY("graph-tool-scope"),
    OPERATION_TYPE_KEY("graph-tool-operation-type"),
    METRIC_TYPE_KEY("graph-tool-metric-type"),
    PROCESS_STATUS_KEY("graph-tool-process-status");

    private final String literal;
}
