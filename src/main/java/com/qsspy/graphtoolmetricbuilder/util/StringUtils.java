package com.qsspy.graphtoolmetricbuilder.util;

import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;

@UtilityClass
public class StringUtils {

    public static final String EMPTY_STRING = "";

    private static final String COMMA_AND_SPACE = ", ";

    public String getCommaAndSpaceSeparatedList(final String... strings) {
        final StringBuilder builder = new StringBuilder();
        for (final String string: strings) {
            if(nonNull(string)) {
                builder.append(string).append(COMMA_AND_SPACE);
            }
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
