package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.common.Constants;
import com.mogujie.jarvis.web.common.TimeTools;
import com.mogujie.jarvis.web.entity.vo.TaskSearchVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
@Service
public class TaskService {
    @Autowired
    private TaskMapper taskMapper;

    public TaskVo getTaskById(Long taskId){
        TaskVo taskVo=taskMapper.getTaskById(taskId);
        changeFormat(taskVo);
        return taskVo;
    }

    public JSONObject getTasks(TaskSearchVo taskSearchVo){
        JSONObject jsonObject=new JSONObject();

        if(StringUtils.isNotBlank(taskSearchVo.getTaskStatusArrStr())){
            JSONArray arr= JSON.parseArray(taskSearchVo.getTaskStatusArrStr());
            if(arr.size()>0){
                List<Integer> taskStatus= new ArrayList<Integer>();
                for(int i=0;i<arr.size();i++){
                    Integer status=arr.getInteger(i);
                    taskStatus.add(status);
                }
                taskSearchVo.setTaskStatus(taskStatus);
            }
        }

        Integer count = taskMapper.getCountByCondition(taskSearchVo);
        count=count==null?0:count;
        List<TaskVo> taskVoList=taskMapper.getTasksByCondition(taskSearchVo);

        for(TaskVo taskVo:taskVoList){
            changeFormat(taskVo);
        }

        jsonObject.put("total",count);
        jsonObject.put("rows",taskVoList);

        return jsonObject;
    }


    private void changeFormat(TaskVo taskVo){
        taskVo.setJobPriority(Constants.jobPriorityMap.get(taskVo.getPriority()));
        taskVo.setTaskStatus(Constants.taskStatusMap.get(taskVo.getStatus()));
        taskVo.setDataYmdStr(TimeTools.formatDate(taskVo.getDataYmd()));
        taskVo.setActiveStartDateStr(TimeTools.formatDate(taskVo.getActiveStartDate()));
        taskVo.setActiveEndDateStr(TimeTools.formatDate(taskVo.getActiveEndDate()));
        taskVo.setCreateTimeStr(TimeTools.formatDateTime(taskVo.getCreateTime()));
        taskVo.setUpdateTimeStr(TimeTools.formatDateTime(taskVo.getUpdateTime()));
        taskVo.setExecuteStartTimeStr(TimeTools.formatDateTime(taskVo.getExecuteStartTime()));
        taskVo.setExecuteEndTimeStr(TimeTools.formatDateTime(taskVo.getExecuteEndTime()));
    }
}
