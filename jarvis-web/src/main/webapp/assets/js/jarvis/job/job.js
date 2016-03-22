
var jobPriority = {};
var bizGroup = {};

$(function () {

    //select采用select2 实现
    $(".input-group select").select2({width: '100%'});

    glFuncs.initJobType("jobType",false);

    glFuncs.initJobStatus("jobStatus",false);


    $.ajax({
        url:contextPath + "/assets/json/jobPriority.json",
        async:false,
        success:function(data){
            $(data).each(function (i, c) {
                var key = c.id;
                var text = c.text;
                jobPriority[key] = text;
            });


            $("#jobPriority").select2({
                data: data,
                width: '100%'
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化任务优先级信息', msg);
        }
    })

    getBizGroup();
    initSubmitUser();
    initApp();
    initWorkerGroup();
    glFuncs.initJobId("jobId");
    glFuncs.initJobName("jobName");

    initData();
});


function initSubmitUser() {
    $.ajax({
        url:contextPath + "/api/job/getSubmitUsers",
        success:function(data){
            var newData = new Array();
            $(data).each(function (i, c) {
                var item = {};
                item["id"] = c;
                item["text"] = c;
                newData.push(item);
            });
            $("#submitUser").select2({
                data: newData,
                width: '100%'
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '初始化提交用户', msg);
        }
    })

}

function initApp() {
    $.ajax({
        url:contextPath + "/api/app/getApps",
        success:function(data){
            var newData = new Array();
            $(data.rows).each(function (i, c) {
                var item = {};
                item["id"] = c.appId;
                item["text"] = c.appName;
                newData.push(item);
            });
            $("#appId").select2({
                data: newData,
                width: '100%'
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '获取应用列表', msg);
        }
    })

}

function initWorkerGroup() {
    $.ajax({
        url:contextPath + "/api/workerGroup/getAllWorkerGroup",
        success:function(data){
            var newData = new Array();
            $(data).each(function (i, c) {
                var item = {};
                item["id"] = c.id;
                item["text"] = c.name;
                newData.push(item);
            });
            $("#workerGroupId").select2({
                data: newData,
                width: '100%'
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '获取workerGroup列表', msg);
        }
    })

}

//查找
function search() {
    $("#content").bootstrapTable('destroy', '');
    initData();
}
//重置参数
function reset() {
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#submitUser").val("all").trigger("change");
    $("#jobStatus").val("all").trigger("change");
    $("#jobPriority").val("all").trigger("change");
    $("#appId").val("").trigger("change");
    $("#workerGroupId").val("").trigger("change");
}


//获取查询参数
function getQueryPara() {
    var queryPara = {};

    var jobIdList = $("#jobId").val();
    var jobNameList = $("#jobName").val();
    var jobTypeList = $("#jobType").val();
    var statusList = $("#jobStatus").val();
    var priorityList = $("#jobPriority").val();
    var submitUserList = $("#submitUser").val();
    var appIdList = $("#appId").val();
    var workerGroupIdList = $("#workerGroupId").val();


    jobIdList = jobIdList == 'all' ? undefined : jobIdList;
    jobIdList = jobIdList == null ? undefined : jobIdList;
    jobNameList = jobNameList == 'all' ? undefined : jobNameList;
    jobNameList = jobNameList == null ? undefined : jobNameList;
    jobTypeList = jobTypeList == 'all' ? undefined : jobTypeList;
    jobTypeList = jobTypeList == null ? undefined : jobTypeList;
    statusList = statusList == 'all' ? undefined : statusList;
    statusList = statusList == null ? undefined : statusList;
    priorityList = priorityList == 'all' ? undefined : priorityList;
    priorityList = priorityList == null ? undefined : priorityList;
    submitUserList = submitUserList == 'all' ? undefined : submitUserList;
    submitUserList = submitUserList == null ? undefined : submitUserList;
    appIdList = appIdList == 'all' ? undefined : appIdList;
    appIdList = appIdList == null ? undefined : appIdList;
    workerGroupIdList = workerGroupIdList == 'all' ? undefined : workerGroupIdList;
    workerGroupIdList = workerGroupIdList == null ? undefined : workerGroupIdList;

    queryPara["jobIdList"] = JSON.stringify(jobIdList);
    queryPara["jobNameList"] = JSON.stringify(jobNameList);
    queryPara["jobTypeList"] = JSON.stringify(jobTypeList);
    queryPara["statusList"] = JSON.stringify(statusList);
    queryPara["priorityList"] = JSON.stringify(priorityList);
    queryPara["submitUserList"] = JSON.stringify(submitUserList);
    queryPara["appIdList"] = JSON.stringify(appIdList);
    queryPara["workerGroupIdList"] = JSON.stringify(workerGroupIdList);

    return queryPara;
}

function getBizGroup() {
    $.ajax({
        url:contextPath + "/api/bizGroup/getAllByCondition",
        async:false,
        success:function(data){
            $(data.data).each(function (i, c) {
                bizGroup[c.id] = c.name;
            });
        },
        error: function (jqXHR, exception) {
            var msg = getMsg4ajaxError(jqXHR, exception);
            showMsg('warning', '获取业务组列表', msg);
        }
    })
}

//初始化数据及分页
function initData() {
    var queryParams = getQueryPara();
    $("#content").bootstrapTable({
        columns: columns,
        pagination: true,
        sidePagination: 'server',
        search: false,
        url: contextPath + '/api/job/getJobs',
        queryParams: function (params) {
            for (var key in queryParams) {
                var value = queryParams[key];
                params[key] = value;
            }
            return params;
        },
        responseHandler:function(res){
            if(res.status){
                showMsg("error","初始化任务列表",res.status.msg);
                return res;
            }
            else{
                return res;
            }
        },
        showColumns: true,
        showHeader: true,
        showToggle: true,
        sortable: true,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500, 1000],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'basic'
    });
}
//更新job的状态
function updateJobStatus(jobId, jobStatus) {
    var data = {jobIds: [jobId], status: jobStatus};
    requestRemoteRestApi("/api/job/status/set", "更新任务状态", data);
    search();
}
//job的字段列表
var columns = [{
    field: 'jobId',
    title: '任务id',
    sortable: true,
    switchable: true
}, {
    field: 'jobName',
    title: '任务名',
    switchable: true,
    sortable: true,
    formatter: jobNameFormatter
}, {
    field: 'appName',
    title: '应用',
    sortable: true,
    switchable: true,
    formatter: appNameFormatter
}, {
    field: 'workerGroupId',
    title: 'worker组ID',
    switchable: true,
    sortable: true,
    visible: false
}, {
    field: 'workerGroupName',
    title: 'Worker组',
    switchable: true,
    sortable: true,
    visible: true
}, {
    field: 'jobType',
    title: '任务类型',
    sortable: true,
    switchable: true
}, {
    field: 'bizGroups',
    title: '业务标签',
    sortable: true,
    switchable: true,
    visible: true,
    formatter: bizGroupFormatter
}, {
    field: 'status',
    title: '状态',
    sortable: true,
    switchable: true,
    formatter: statusFormatter
}, {
    field: 'content',
    title: '任务内容',
    switchable: true,
    visible: false
}, {
    field: 'params',
    title: '参数',
    switchable: true,
    visible: false
}, {
    field: 'priority',
    title: '优先级',
    sortable: true,
    switchable: true,
    formatter: jobPriorityFormatter
}, {
    field: 'submitUser',
    title: '提交人',
    sortable: true,
    switchable: true
}, {
    field: 'activeStartDate',
    title: '开始日期',
    switchable: true,
    sortable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'activeEndDate',
    title: '结束日期',
    switchable: true,
    sortable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    sortable: true,
    formatter: formatDateTime,
    visible: false
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable: true,
    visible: false,
    sortable: true,
    formatter: formatDateTime
}, {
    field: 'rejectAttempts',
    title: '被Worker拒绝时重试次数',
    switchable: true,
    sortable: true,
    visible: false
}, {
    field: 'rejectInterval',
    title: '被Worker拒绝时重试间隔(秒)',
    sortable: true,
    switchable: true,
    visible: false
}, {
    field: 'failedAttempts',
    title: '运行失败时重试次数',
    sortable: true,
    switchable: true,
    visible: false
}, {
    field: 'failedInterval',
    title: '运行失败时重试间隔(秒)',
    sortable: true,
    switchable: true,
    visible: false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateFormatter
}];

function appNameFormatter(value, row, index) {
    var result = "<div style='white-space: nowrap'>" + value + "</div>";
    return result;
}

function jobPriorityFormatter(value, row, index) {
    var text = jobPriority[value];
    if (text == null) {
        $.ajax({
            url:contextPath + "/assets/json/jobPriority.json",
            success:function(data){
                $(data).each(function (i, c) {
                    var key = c.id;
                    if (value == key) {
                        text = c.text;
                        return false;
                    }
                });
            },
            error: function (jqXHR, exception) {
                var msg = getMsg4ajaxError(jqXHR, exception);
                showMsg('warning', '初始化优先级信息', msg);
            }
        })
    }

    return text;
}


//操作格式化器
function operateFormatter(value, row, index) {
    //console.log(row);
    var jobId = row["jobId"];
    var jobAppId = row["appId"];
    var appKey = row["appKey"];
    var appName = row["appName"];
    var status = row["status"];


    var result = [
        ].join('');

    if (appId == jobAppId) {
        var edit = [
            '<a  href="' + contextPath + '/job/addOrEdit?jobId=' + jobId + '" title="编辑任务信息" target="_blank">',
            '<i class="glyphicon glyphicon-edit"></i>编辑',
            '</a>  '
        ].join('');

        result += edit;


        var operation = '<div class="btn-group"> <button type="button" class="btn btn-xs btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">状态 <span class="caret"></span> </button>';
        operation = operation + '<ul class="dropdown-menu">';
        $(jobStatus).each(function (i, c) {
            if (c["id"] != 'all' && c["id"] != status && c["id"] != '3') {
                var li = '<li><a href="javascript:void(0)" onclick="updateJobStatus(' + jobId + ',' + c["id"] + ')" >' + c["text"] + '</a></li>';
                operation = operation + li;
            }
        });
        operation = operation + '</ul></div>';
        result = result + operation;
    }

    result = "<div style='white-space: nowrap'>" + result + "</div>";

    return result;
}

//job状态对应显示的图标
var jobStatusClass = {
    "1": {"color": "glyphicon glyphicon-ok text-success", "text": "启用"},
    "2": {"color": "glyphicon glyphicon-remove text-danger", "text": "禁用"},
    "3": {"color": "glyphicon glyphicon-calendar text-info", "text": "过期"},
    "4": {"color": "glyphicon glyphicon-trash text-danger", "text": "删除"},
    "5": {"color": "glyphicon glyphicon-pause text-warning", "text": "暂停"}
}
//状态格式化器
function statusFormatter(value, row, index) {
    var result;
    result = '<i class="' + jobStatusClass[value].color + '"></i> ' + jobStatusClass[value].text;
    return result;
}
//格式化结果
function formatResult(result) {
    return result.text;
}
//格式化选择框
function formatResultSelection(result) {
    return result.id;
}
//业务标签格式化器
function bizGroupFormatter(value, row, index) {
    return bizGroup[value];
}

function jobNameFormatter(value, row, index) {
    var result = '<a target="_blank" href="' + contextPath + "/job/detail?jobId=" + row["jobId"] + '">' + value + '</a>';
    return result;
}



