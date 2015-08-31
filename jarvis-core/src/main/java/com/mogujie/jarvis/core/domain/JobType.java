/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月12日 下午4:26:33
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author wuya
 *
 */
public enum JobType {
    SHELL("shell"), HIVE("hive"), PRESTO("presto");

    private String value;

    private JobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
