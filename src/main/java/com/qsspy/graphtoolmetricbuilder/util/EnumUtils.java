package com.qsspy.graphtoolmetricbuilder.util;

import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;

@UtilityClass
public class EnumUtils {

    public static <E extends Enum<E>> String getEnumStringOrEmpty(final E value) {
        if(nonNull(value)) {
            return value.name();
        }
        return StringUtils.EMPTY_STRING;
    }
}
