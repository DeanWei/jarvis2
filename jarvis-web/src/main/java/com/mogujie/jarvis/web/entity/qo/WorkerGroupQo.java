package com.mogujie.jarvis.web.entity.qo;

import com.mogujie.jarvis.dto.generate.WorkerGroup;

/**
 * Created by hejian on 15/9/28.
 */
public class WorkerGroupQo{
    private Integer offset;
    private Integer limit;
    private Long appId;
    private Integer id;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
