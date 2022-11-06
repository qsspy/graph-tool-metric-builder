package com.qsspy.graphtoolmetricbuilder.model;

public enum ProcessStatus {
    SUCCESS, FAILURE;

    public static ProcessStatus basedOnBoolean(final boolean isRelatedToSuccess) {
        return isRelatedToSuccess ? FAILURE : SUCCESS;
    }
}
