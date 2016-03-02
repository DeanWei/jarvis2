package com.mogujie.jarvis.dao.generate;

import org.apache.ibatis.annotations.Param;

import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependExample;
import com.mogujie.jarvis.dto.generate.JobDependKey;

public interface JobDependMapper {
    int countByExample(JobDependExample example);

    int deleteByExample(JobDependExample example);

    int deleteByPrimaryKey(JobDependKey key);

    int insert(JobDepend record);

    int insertSelective(JobDepend record);

    java.util.List<com.mogujie.jarvis.dto.generate.JobDepend> selectByExample(JobDependExample example);

    JobDepend selectByPrimaryKey(JobDependKey key);

    int updateByExampleSelective(@Param("record") JobDepend record, @Param("example") JobDependExample example);

    int updateByExample(@Param("record") JobDepend record, @Param("example") JobDependExample example);

    int updateByPrimaryKeySelective(JobDepend record);

    int updateByPrimaryKey(JobDepend record);
}