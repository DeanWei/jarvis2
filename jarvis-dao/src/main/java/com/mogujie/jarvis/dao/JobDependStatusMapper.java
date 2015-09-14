package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusExample;
import com.mogujie.jarvis.dto.JobDependStatusKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobDependStatusMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int countByExample(JobDependStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int deleteByExample(JobDependStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int deleteByPrimaryKey(JobDependStatusKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int insert(JobDependStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int insertSelective(JobDependStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    List<JobDependStatus> selectByExample(JobDependStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    JobDependStatus selectByPrimaryKey(JobDependStatusKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int updateByExampleSelective(@Param("record") JobDependStatus record, @Param("example") JobDependStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int updateByExample(@Param("record") JobDependStatus record, @Param("example") JobDependStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int updateByPrimaryKeySelective(JobDependStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    int updateByPrimaryKey(JobDependStatus record);
}