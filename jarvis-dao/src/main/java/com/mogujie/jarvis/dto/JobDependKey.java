package com.mogujie.jarvis.dto;

public class JobDependKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column job_depend.jobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    private Integer jobId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column job_depend.preJobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    private Integer preJobId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column job_depend.jobId
     *
     * @return the value of job_depend.jobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    public Integer getJobId() {
        return jobId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column job_depend.jobId
     *
     * @param jobId the value for job_depend.jobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column job_depend.preJobId
     *
     * @return the value of job_depend.preJobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    public Integer getPreJobId() {
        return preJobId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column job_depend.preJobId
     *
     * @param preJobId the value for job_depend.preJobId
     *
     * @mbggenerated Mon Aug 31 14:35:07 CST 2015
     */
    public void setPreJobId(Integer preJobId) {
        this.preJobId = preJobId;
    }
}