
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
                    <li><a href="/jarvis/">Home</a></li>
                    <li><a href="/jarvis/task">执行流水</a></li>
                    <li class="current"><em>执行详情</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row top-buffer">
        <div class="col-md-12">
            <div class="row">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="emphasize">任务名称</td><td>${jobVo.jobName}</td>
                        <td class="emphasize">任务ID</td><td>${jobVo.jobId}</td>
                        <td class="emphasize">发布者</td><td>${jobVo.submitUser}</td>
                    </tr>
                    <tr>
                        <td class="emphasize">计划执行时间</td><td>-</td>
                        <td class="emphasize">创建时间</td><td>${taskVo.createTimeStr}</td>
                        <td class="emphasize">数据日期</td><td>${taskVo.dataYmdStr}</td>
                    </tr>
                    <tr>
                        <td class="emphasize">耗时(秒)</td><td>${taskVo.executeTime}</td>
                        <td class="emphasize">开始时间</td><td>${taskVo.executeStartTimeStr}</td>
                        <td class="emphasize">结束时间</td><td>${taskVo.executeStartTimeStr}</td>
                    </tr>
                    <tr>
                        <td class="emphasize">最近30次平均耗时(秒)</td><td>${taskVo.avgExecuteTime}</td>
                        <td class="emphasize">状态</td><td colspan="3">${taskVo.taskStatus}</td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div id="container" style="height:400px;width:100%"></div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div>
                                <ul class="nav nav-tabs">
                                    <li role="presentation" class="active">
                                        <a href="#executeContent" data-toggle="tab">执行内容</a>
                                    </li>
                                    <li role="presentation">
                                        <a href="#errorLog" data-toggle="tab">错误日志</a>
                                    </li>
                                </ul>

                                <div class="tab-content">
                                    <div id="executeContent" class="tab-pane active" >
                                        <pre>
                                            ${taskVo.jobContent}
                                        </pre>
                                    </div>

                                    <div id="errorLog" class="tab-pane">

                                    </div>
                                </div>



                            </div>


                        </td>
                    </tr>
                    </tbody></table>
            </div>
        </div>
    </div>
</div>


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>
<script type="text/javascript">
    var taskVoList=${taskVoList};
</script>
<script type="text/javascript" src="/assets/jarvis/js/jarvis/task/detail.js"></script>