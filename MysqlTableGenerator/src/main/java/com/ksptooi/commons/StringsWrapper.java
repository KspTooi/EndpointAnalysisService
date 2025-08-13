package com.ksptooi.commons;

import org.apache.commons.lang3.StringUtils;

public class StringsWrapper {

    public String replace(final String text, final String searchString, final String replacement) {
        return StringUtils.replace(text, searchString, replacement);
    }

    public String replace(final String text, final String searchString, final String replacement, final int max) {
        return StringUtils.replace(text, searchString, replacement, max);
    }



}
