package com.mogujie.jarvis.dao.diy;

import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int countByExample(CrontabExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int deleteByExample(CrontabExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int deleteByPrimaryKey(Integer cronId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int insert(Crontab record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int insertSelective(Crontab record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    List<Crontab> selectByExample(CrontabExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    Crontab selectByPrimaryKey(Integer cronId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int updateByExampleSelective(@Param("record") Crontab record, @Param("example") CrontabExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int updateByExample(@Param("record") Crontab record, @Param("example") CrontabExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int updateByPrimaryKeySelective(Crontab record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table crontab
     *
     * @mbggenerated Mon Sep 14 10:22:57 CST 2015
     */
    int updateByPrimaryKey(Crontab record);
}