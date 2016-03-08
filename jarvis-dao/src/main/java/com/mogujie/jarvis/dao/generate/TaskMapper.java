package com.mogujie.jarvis.dao.generate;

import org.apache.ibatis.annotations.Param;

import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.dto.generate.TaskExample;

public interface TaskMapper {
    int countByExample(TaskExample example);

    int deleteByExample(TaskExample example);

    int deleteByPrimaryKey(Long taskId);

    int insert(Task record);

    int insertSelective(Task record);

    java.util.List<com.mogujie.jarvis.dto.generate.Task> selectByExampleWithBLOBs(TaskExample example);

    java.util.List<com.mogujie.jarvis.dto.generate.Task> selectByExample(TaskExample example);

    Task selectByPrimaryKey(Long taskId);

    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExampleWithBLOBs(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExample(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKeyWithBLOBs(Task record);

    int updateByPrimaryKey(Task record);
}