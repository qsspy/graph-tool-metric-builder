package com.qsspy.graphtoolmetricbuilder.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ProcessOperationType {
    CREATE("Create operation"),
    READ("Read operation"),
    UPDATE("Update operation"),
    DELETE("Delete operation"),

    SAVE("Save operation"),
    UPSERT("Save/Update operation");

    private final String description;
}
