
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li class="current"><em>执行流水</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <div class="row">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">调度日期</span>
                        <input id="scheduleDate" class="form-control" />
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行日期</span>
                        <input id="executeDate" class="form-control" />
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行开始日期>=</span>
                        <input id="startDate"  class="form-control" />
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行结束日期<</span>
                        <input id="endDate"  class="form-control" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务ID</span>
                        <select id="jobId" >

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务名称</span>
                        <select id="jobName" >

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务类型</span>
                        <select id="jobType" ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行用户</span>
                        <select id="executeUser" >
                            <option value="all">全部</option>
                            <c:forEach items="${executeUsers}" var="executeUser" varStatus="status">
                                <option value="${executeUser}">${executeUser}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

            </div>

            <div class="row top-buffer">

                <div class="col-md-6">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:16.5%">状态</span>
                        <div class="form-control" id="taskStatus">
                        </div>
                    </div>
                </div>

                <div class="col-md-3 col-md-offset-3">
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="input-group pull-right">
                                <button type="button" class="btn btn-primary" onclick="search()">查询</button>
                                <button type="button" class="btn btn-primary" onclick="reset()" style="margin-left: 3px">重置</button>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

        </div>
    </div>

    <hr>

    <div class="row top-buffer">
        <div class="col-md-12">
            <table id="content" >

            </table>

        </div>

    </div>


</div>


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/task/task.js"></script>
