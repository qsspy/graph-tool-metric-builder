package com.qsspy.graphtoolmetricbuilder.exception;

public abstract class GraphToolException extends RuntimeException {

    public GraphToolException() { }

    public GraphToolException(final String message) {
        super(message);
    }

    public GraphToolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
