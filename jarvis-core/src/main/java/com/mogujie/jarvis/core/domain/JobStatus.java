/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月12日 下午4:56:39
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author wuya
 *
 */
public enum JobStatus {
    UNKONW(-1), WAITING(1), RUNNING(3), SUCCESS(0), FAILED(5);

    private int value;

    JobStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
