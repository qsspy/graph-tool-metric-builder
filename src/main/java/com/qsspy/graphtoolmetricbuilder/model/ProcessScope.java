package com.qsspy.graphtoolmetricbuilder.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ProcessScope {
    HTTP_REQUESTING("HTTP requesting related process"),
    DATABASE_INTEGRATION("Database operation related process"),
    MESSAGE_QUEUE_INTEGRATION("Message queue integration related process"),
    EXTERNAL_SERVICE_INTEGRATION("External service integration related process"),
    FILE_SYSTEM_INTEGRATION("File system integration related process"),
    CACHE_OPERATION("Cache operation related process"),
    OTHER(null);

    private final String description;
}
