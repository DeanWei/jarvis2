package com.mogujie.jarvis.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobDependStatusExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public JobDependStatusExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andJobIdIsNull() {
            addCriterion("jobId is null");
            return (Criteria) this;
        }

        public Criteria andJobIdIsNotNull() {
            addCriterion("jobId is not null");
            return (Criteria) this;
        }

        public Criteria andJobIdEqualTo(Long value) {
            addCriterion("jobId =", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotEqualTo(Long value) {
            addCriterion("jobId <>", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThan(Long value) {
            addCriterion("jobId >", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThanOrEqualTo(Long value) {
            addCriterion("jobId >=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThan(Long value) {
            addCriterion("jobId <", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThanOrEqualTo(Long value) {
            addCriterion("jobId <=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdIn(List<Long> values) {
            addCriterion("jobId in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotIn(List<Long> values) {
            addCriterion("jobId not in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdBetween(Long value1, Long value2) {
            addCriterion("jobId between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotBetween(Long value1, Long value2) {
            addCriterion("jobId not between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdIsNull() {
            addCriterion("preJobId is null");
            return (Criteria) this;
        }

        public Criteria andPreJobIdIsNotNull() {
            addCriterion("preJobId is not null");
            return (Criteria) this;
        }

        public Criteria andPreJobIdEqualTo(Long value) {
            addCriterion("preJobId =", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdNotEqualTo(Long value) {
            addCriterion("preJobId <>", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdGreaterThan(Long value) {
            addCriterion("preJobId >", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdGreaterThanOrEqualTo(Long value) {
            addCriterion("preJobId >=", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdLessThan(Long value) {
            addCriterion("preJobId <", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdLessThanOrEqualTo(Long value) {
            addCriterion("preJobId <=", value, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdIn(List<Long> values) {
            addCriterion("preJobId in", values, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdNotIn(List<Long> values) {
            addCriterion("preJobId not in", values, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdBetween(Long value1, Long value2) {
            addCriterion("preJobId between", value1, value2, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreJobIdNotBetween(Long value1, Long value2) {
            addCriterion("preJobId not between", value1, value2, "preJobId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdIsNull() {
            addCriterion("preTaskId is null");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdIsNotNull() {
            addCriterion("preTaskId is not null");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdEqualTo(Long value) {
            addCriterion("preTaskId =", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdNotEqualTo(Long value) {
            addCriterion("preTaskId <>", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdGreaterThan(Long value) {
            addCriterion("preTaskId >", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdGreaterThanOrEqualTo(Long value) {
            addCriterion("preTaskId >=", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdLessThan(Long value) {
            addCriterion("preTaskId <", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdLessThanOrEqualTo(Long value) {
            addCriterion("preTaskId <=", value, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdIn(List<Long> values) {
            addCriterion("preTaskId in", values, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdNotIn(List<Long> values) {
            addCriterion("preTaskId not in", values, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdBetween(Long value1, Long value2) {
            addCriterion("preTaskId between", value1, value2, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskIdNotBetween(Long value1, Long value2) {
            addCriterion("preTaskId not between", value1, value2, "preTaskId");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusIsNull() {
            addCriterion("preTaskStatus is null");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusIsNotNull() {
            addCriterion("preTaskStatus is not null");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusEqualTo(Integer value) {
            addCriterion("preTaskStatus =", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusNotEqualTo(Integer value) {
            addCriterion("preTaskStatus <>", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusGreaterThan(Integer value) {
            addCriterion("preTaskStatus >", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("preTaskStatus >=", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusLessThan(Integer value) {
            addCriterion("preTaskStatus <", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusLessThanOrEqualTo(Integer value) {
            addCriterion("preTaskStatus <=", value, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusIn(List<Integer> values) {
            addCriterion("preTaskStatus in", values, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusNotIn(List<Integer> values) {
            addCriterion("preTaskStatus not in", values, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusBetween(Integer value1, Integer value2) {
            addCriterion("preTaskStatus between", value1, value2, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andPreTaskStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("preTaskStatus not between", value1, value2, "preTaskStatus");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("createTime is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("createTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("createTime =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("createTime <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("createTime >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createTime >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("createTime <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("createTime <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("createTime in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("createTime not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("createTime between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("createTime not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("updateTime is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("updateTime is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("updateTime =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("updateTime <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("updateTime >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("updateTime >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("updateTime <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("updateTime <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("updateTime in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("updateTime not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("updateTime between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("updateTime not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table job_depend_status
     *
     * @mbggenerated do_not_delete_during_merge Mon Sep 14 09:30:53 CST 2015
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table job_depend_status
     *
     * @mbggenerated Mon Sep 14 09:30:53 CST 2015
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}