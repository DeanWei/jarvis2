<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<style>
    .nav-tabs > li.active > a, .nav-tabs > li.active > a:focus, .nav-tabs > li.active > a:hover {
        background-color: #96c03d;
        color: #fff;
    }

    label.disabled {
        color: darkgrey;
    }

    input.disabled{
        background: lightgrey;
    }

</style>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li><a href="${contextPath}/job">任务管理</a></li>
                    <c:choose>
                        <c:when test="${jobVo!=null}">
                            <li class="current"><em>编辑任务</em></li>
                        </c:when>
                        <c:otherwise>
                            <li class="current"><em>新增任务</em></li>
                        </c:otherwise>
                    </c:choose>
                </ol>
            </nav>
        </div>
    </div>


    <div class="row">
        <div class="col-md-12">
            <!-- Nav tabs -->

            <ul class="nav nav-tabs cd-multi-steps text-center custom-icons">
                <li class="active">
                    <a href="#baseInfo" data-toggle="tab">基本信息</a>
                </li>
                <li>
                    <a href="#dependInfo" data-toggle="tab">依赖任务</a>
                </li>
                <li>
                    <a href="#alarmInfo" data-toggle="tab">报警设置</a>
                </li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content">
                <div class="tab-pane active" id="baseInfo">

                    <!--任务名称 -->
                    <div class="col-md-12">
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务名称
                            <span class="text-danger" style="vertical-align: middle">*</span></span>
                                    <input id="jobName" class="form-control" desc="任务名称" onblur="checkJobName(this)"/>
                                </div>
                            </div>
                        </div>

                        <!--部门 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">部门</span>
                                    <input id="department" class="form-control"/>
                                </div>
                            </div>
                        </div>

                        <!--业务标签 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">业务标签</span>
                                    <select id="bizGroups" desc="业务标签" multiple="multiple"
                                            placeholder="标签可多选,标签增加请联系管理员"></select>
                                </div>
                            </div>
                        </div>

                        <!--worker组 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">Worker Group
                                        <span class="text-danger" style="vertical-align: middle">*</span>
                                    </span>
                                    <select id="workerGroupId" desc="Worker Group"></select>
                                </div>
                            </div>
                        </div>

                        <!--任务类型 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">任务类型
                                        <span class="text-danger" style="vertical-align: middle">*</span>
                                    </span>
                                    <select id="jobType" desc="任务类型"></select>
                                </div>
                            </div>
                        </div>

                        <!--内容类型 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">内容类型
                                        <span class="text-danger" style="vertical-align: middle">*</span>
                                    </span>

                                    <div class="form-control">
                                        <label class="radio-inline">
                                            <input name="contentType" id="contentTypeText" value="1" type="radio"
                                                   checked> 文本
                                        </label>
                                        <label class="radio-inline">
                                            <input name="contentType" id="contentTypeScript" value="2" type="radio"> 脚本
                                        </label>
                                        <label class="radio-inline">
                                            <input name="contentType" id="contentTypeJar" value="3" type="radio"> jar包
                                        </label>
                                        <label class="radio-inline" style="display: none">
                                            <input name="contentType" id="contentTypeEmpty" value="0" type="radio"> jar包
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!--脚本选择 -->
                        <div id="scriptItemDiv" class="row top-buffer" style="display: none">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">脚本名称</span>
                                    <input id="scriptTitle" class="form-control" type="text" readonly/>
                                    <input id="scriptId" value="" type="hidden">
                                </div>
                            </div>
                            <button id="searchScriptBtn" type="button" class="btn btn-xs btn-default"
                                    onclick="showSearchScriptModal()">选择脚本
                            </button>
                        </div>

                        <!-- jar包上传 -->
                        <div id="jarItemDiv" class="row top-buffer" style="display: none">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">jar包</span>
                                    <input id="jarUrl" name="jarUrl" class="form-control" type="text" readonly/>
                                </div>
                            </div>
                            <input id="uploadJarInput" type="file" class="btn btn-xs btn-default">
                        </div>

                        <!--任务内容 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">任务内容
                                        <span class="text-danger" style="vertical-align: middle">*</span>
                                    </span>
                                <textarea id="jobContent" class="form-control" desc="任务内容" rows="4"
                                          onclick="changeTextArea(this,15,10)"
                                          onblur="changeTextArea(this,4,10)">
                                </textarea>
                                </div>
                            </div>
                        </div>

                        <!--任务参数 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">任务参数</span>
                                    <input id="params" value="{}" defaultValue="{}" class="form-control"
                                           onclick="showParaModal()"/>
                                </div>
                            </div>
                        </div>

                        <!--任务计划 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">任务计划</span>
                                    <input id="jobSchedule" value="" defaultValue="" class="form-control"
                                           onclick="showJobScheduleModal()"/>

                                    <input id="expressionType" hidden/>
                                    <input id="expression" hidden/>


                                </div>
                            </div>
                        </div>

                        <%--<!--表达式类型 -->--%>
                        <%--<div class="row top-buffer">--%>
                            <%--<div class="col-md-6 col-md-offset-3">--%>
                                <%--<div class="input-group" style="width:100%">--%>
                                    <%--<span class="input-group-addon" style="width:35%">表达式类型</span>--%>
                                    <%--<select id="expressionType">--%>
                                    <%--</select>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>

                        <%--<!--表达式 -->--%>
                        <%--<div class="row top-buffer">--%>
                            <%--<div class="col-md-6 col-md-offset-3">--%>
                                <%--<div class="input-group" style="width:100%">--%>
                                    <%--<span class="input-group-addon" style="width:35%">表达式</span>--%>
                                    <%--<input id="expression" class="form-control"/>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>

                        <!--优先级 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">优先级</span>
                                    <select id="priority"></select>
                                </div>
                            </div>
                        </div>

                        <!--起始日期 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">起始日期</span>

                                    <input id="activeStartDate" class="form-control" placeholder="不填代表无限制"/>

                                </div>
                            </div>
                        </div>

                        <!--结束日期 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">结束日期</span>
                                    <input id="activeEndDate" class="form-control" placeholder="不填代表无限制"/>
                                </div>
                            </div>
                        </div>


                        <!--点击:高级参数设置 -->
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <a href="javascript:void(0)" onclick="AdvancedOptionsToggle(this)"><i
                                        class="pull-right text-primary glyphicon glyphicon-chevron-down"
                                        toState="up"></i></a>
                                <span class="pull-right text-info" style="margin-right:20px">点击配置详细参数</span>
                            </div>
                        </div>

                        <!--展开:高级参数设置 -->
                        <div id="other" style="display: none">

                            <!--失败重试数 -->
                            <div class="row top-buffer">
                                <div class="col-md-6 col-md-offset-3">
                                    <div class="input-group" style="width:100%">
                                        <span class="input-group-addon" style="width:35%">失败重试数</span>
                                        <input id="failedAttempts" class="form-control" value="0"
                                               desc="失败重试数"
                                               placeholder="默认为0" defaultValue="0" onblur="checkNum(this)"/>
                                    </div>
                                </div>
                            </div>

                            <!--失败重试间隔 -->
                            <div class="row top-buffer">
                                <div class="col-md-6 col-md-offset-3">
                                    <div class="input-group" style="width:100%">
                                        <span class="input-group-addon" style="width:35%">失败重试间隔(秒)</span>
                                        <input id="failedInterval" class="form-control" value="3"
                                               desc="失败重试间隔(秒)"
                                               placeholder="默认为3" defaultValue="3" onblur="checkNum(this)"/>
                                    </div>
                                </div>
                            </div>

                        </div>


                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3 text-center">
                                <button type="button" class="btn btn-primary" onclick="saveJob()">保存</button>
                                <button type="button" class="btn btn-primary" onclick="resetJob()">重置</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-----------------------    依赖任务-Tab  -------------------------------------------------- -->
                <div class="tab-pane" id="dependInfo">
                    <div class="col-md-12">
                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3">
                                <div class="input-group" style="width:100%">
                                    <span class="input-group-addon" style="width:35%">依赖任务</span>
                                    <select id="dependJobIds" multiple>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row top-buffer">
                            <div class="col-md-5 col-md-offset-4">
                                <div id="strategyPattern" style="display: none">

                                    <div class="col-md-9" style="margin-bottom: 2px">
                                        <div class="input-group commonStrategy" style="width:100%">
                                            <span name="dependJob" class="input-group-addon"
                                                  style="width:50%;background-color:#d9edf7"></span>
                                            <span class="input-group-addon" style="width:10%">通用</span>
                                            <select name="commonStrategy" class="form-control"></select>
                                        </div>
                                    </div>

                                    <div class="col-md-3" style="margin-bottom: 2px">
                                        <div class="input-group offsetStrategy"
                                             style="margin-left:-25px;margin-right: -15px"
                                             style="width:100%">
                                            <span class="input-group-addon" style="width:30%;">偏移</span>
                                            <input name="offsetStrategy" class="form-control" value="cd"
                                                   placeholder="默认:cd(当天)"/>
                                        </div>
                                    </div>
                                </div>

                                <dl id="strategyList"></dl>
                            </div>
                            <span>
                                <i class="fa fa-question text-info fa-2x"
                                   style="cursor: pointer;position: relative;position: absolute;margin-left:10px;"
                                   onmouseover="showDescription(this)" onmouseout="hideDescription(this)"></i>
                            </span>
                        </div>

                        <div class="row top-buffer">
                            <div class="col-md-6 col-md-offset-3 text-center">
                                <button type="button" class="btn btn-primary" onclick="saveDepend()">保存</button>
                                <button type="button" class="btn btn-primary" onclick="resetDepend()">重置</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-----------------------    报警设定-Tab  -------------------------------------------------- -->
                <div class="tab-pane" id="alarmInfo">
                    <div class="row top-buffer">
                        <div class="col-md-6 col-md-offset-3">
                            <div class="input-group" style="width:100%">
                                <span class="input-group-addon" style="width:35%">接收人</span>
                                <select id="alarm" multiple></select>
                            </div>
                        </div>
                    </div>

                    <div class="row top-buffer">
                        <div class="col-md-6 col-md-offset-3">
                            <div class="input-group" style="width:100%">
                                <span class="input-group-addon" style="width:35%">报警类型</span>

                                <div id="alarmType" class="form-control">
                                    <input type="checkbox" onclick="clickAlarmTypeAll(this)">全部
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row top-buffer">
                        <div class="col-md-6 col-md-offset-3">
                            <div class="input-group" style="width:100%">
                                <span class="input-group-addon" style="width:35%">报警启用/禁用</span>

                                <div id="alarmStatus" class="form-control">
                                    <input name="alarmStatus" type="radio" value="1" checked="checked">启用
                                    <input name="alarmStatus" type="radio" value="2">禁用
                                </div>

                            </div>
                        </div>
                    </div>

                    <div class="row top-buffer">
                        <div class="col-md-6 col-md-offset-3 text-center">
                            <button type="button" class="btn btn-primary" onclick="saveAlarm()">保存</button>
                            <button type="button" class="btn btn-primary" onclick="resetAlarm()">重置</button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="addOrEditModal.jsp" %>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script>
    jobId = '${jobId}';
    appId = '${app.appId}';
</script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/addOrEdit.js"></script>
<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/addOrEditModal.js"></script>
<script type="text/javascript" src="${contextPath}/assets/js/cron.js"></script>


