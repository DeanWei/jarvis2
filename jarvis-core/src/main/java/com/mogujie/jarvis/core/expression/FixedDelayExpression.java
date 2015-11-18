/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月29日 下午10:41:44
 */

package com.mogujie.jarvis.core.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.DurationFieldTypes;

/**
 *
 *
 */
public class FixedDelayExpression extends ScheduleExpression {

    private int isValid;
    private DurationFieldType durationFieldType;
    private int value;
    private DateTime firstDateTime;
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("([smhdwMy])\\(('(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})',)?(\\d+)\\)");

    public FixedDelayExpression(String expression) {
        super(expression);
    }

    @Override
    public boolean isValid() {
        Matcher m = EXPRESSION_PATTERN.matcher(expression);
        if (m.matches()) {
            durationFieldType = DurationFieldTypes.valueOf(m.group(1).charAt(0));
            String strDateTime = m.group(3);
            if (strDateTime != null) {
                try {
                    firstDateTime = DateTimeFormat.forPattern(JarvisConstants.DEFAULT_DATE_TIME_FORMAT).parseDateTime(strDateTime);
                } catch (IllegalFieldValueException e) {
                    isValid = -1;
                    return false;
                }
            }

            value = Integer.parseInt(m.group(4));
            if (value <= 0) {
                isValid = -1;
                return false;
            }

            isValid = 1;
            return true;
        }

        isValid = -1;
        return false;
    }

    @Override
    public DateTime getTimeBefore(DateTime dateTime) {
        if (isValid > 0 || (isValid == 0 && isValid())) {
            if (firstDateTime != null && firstDateTime.isBefore(dateTime)) {
                return firstDateTime;
            }
            return dateTime.withFieldAdded(durationFieldType, -value);
        }

        return null;
    }

    @Override
    public DateTime getTimeAfter(DateTime dateTime) {
        if (isValid > 0 || (isValid == 0 && isValid())) {
            if (firstDateTime != null && firstDateTime.isAfter(dateTime)) {
                return firstDateTime;
            }
            return dateTime.withFieldAdded(durationFieldType, value);
        }

        return null;
    }

    @Override
    public String toString() {
        return expression;
    }
}
