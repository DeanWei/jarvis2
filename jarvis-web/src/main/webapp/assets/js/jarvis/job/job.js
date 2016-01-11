var jobTypeJson = null;
var jobStatus = null;
var jobPriorityJson = null;
$(function () {
    //select采用select2 实现
    $(".input-group select").select2({width: '100%'});

    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/assets/json/jobType.json", function (data) {
        jobTypeJson = data;
        $("#jobType").select2({
            data: data,
            width: '100%',
            tags: true
        });
    });

    $.getJSON(contextPath + "/api/job/getJobStatus", function (data) {
        jobStatus = data;
        $("#jobStatus").select2({
            data: data,
            width: '100%'
        });
    });

    $.getJSON(contextPath + "/assets/json/jobPriority.json", function (data) {
        jobPriorityJson = data;
        $("#jobPriority").select2({
            data: data,
            width: '100%'
        });
    });
    $.ajaxSettings.async = true;


    $("#jobId").select2({
        ajax: {
            url: contextPath + "/api/job/getSimilarJobIds",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, page) {
                return {
                    results: data.items
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,

        width: '100%'
    });

    $("#jobName").select2({
        ajax: {
            url: contextPath + "/api/job/getSimilarJobNames",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, page) {
                return {
                    results: data.items
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,
        width: '100%'
    });


    initData();
});


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
        showColumns: true,
        showHeader: true,
        showToggle: true,
        pageSize: 1,
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'all'
    });
}


function updateJobStatus(jobId, jobStatus) {
    var data = {jobId: jobId, status: jobStatus};
    requestRemoteRestApi("/api/job/status/set", "更新任务状态", data);
    search();
}


var columns = [{
    field: 'jobId',
    title: '任务id',
    switchable: true
}, {
    field: 'jobName',
    title: '任务名',
    switchable: true
}, {
    field: 'appName',
    title: '应用名',
    switchable: true
}, {
    field: 'workerGroupId',
    title: 'worker组ID',
    switchable: true,
    visible: false
}, {
    field: 'workerGroupName',
    title: 'WorkerGroup名',
    switchable: true,
    visible: false
}, {
    field: 'jobType',
    title: '任务类型',
    switchable: true
}, {
    field: 'status',
    title: '任务状态',
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
    switchable: true
}, {
    field: 'submitUser',
    title: '提交人',
    switchable: true
}, {
    field: 'activeStartDate',
    title: '开始日期',
    switchable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'activeEndDate',
    title: '结束日期',
    switchable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    formatter: formatDateTime
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'rejectAttempts',
    title: '被Worker拒绝时重试次数',
    switchable: true,
    visible: false
}, {
    field: 'rejectInterval',
    title: '被Worker拒绝时重试间隔(秒)',
    switchable: true,
    visible: false
}, {
    field: 'failedAttempts',
    title: '运行失败时重试次数',
    switchable: true,
    visible: false
}, {
    field: 'failedInterval',
    title: '运行失败时重试间隔(秒)',
    switchable: true,
    visible: false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateFormatter
}];


function operateFormatter(value, row, index) {
    //console.log(row);
    var jobId = row["jobId"];
    var appKey = row["appKey"];
    var appName = row["appName"];
    var status = row["status"];
    //console.log(jobId);
    var result = [
        '<a  href="' + contextPath + '/job/addOrEdit?jobId=' + jobId + '" title="编辑任务信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  ',
        '<a  href="' + contextPath + '/job/dependency?jobId=' + jobId + '" title="查看任务依赖" target="_blank">',
        '<i class="glyphicon glyphicon-eye-open"></i>',
        '</a>  '
    ].join('');


    var operation = '<div class="btn-group"> <button type="button" class="btn btn-xs btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">修改状态 <span class="caret"></span> </button>';
    operation = operation + '<ul class="dropdown-menu">';
    $(jobStatus).each(function (i, c) {
        if (c["id"] != 'all' && c["id"] != status && c["id"] != '3') {
            var li = '<li><a href="javascript:void(0)" onclick="updateJobStatus(' + jobId + ',' + c["id"] + ')" >' + c["text"] + '</a></li>';
            operation = operation + li;
        }
    });
    operation = operation + '</ul></div>';

    //console.log(result);

    return result + operation;
}

var jobStatusClass = {
    "1": "glyphicon glyphicon-ok text-success",
    "2": "glyphicon glyphicon-remove text-danger",
    "3": "glyphicon glyphicon-calendar text-info",
    "4": "glyphicon glyphicon-minus text-danger",
    "5": "glyphicon glyphicon-pause text-warning"
}

function statusFormatter(value, row, index) {
    var result;
    result = '<i class="' + jobStatusClass[value] + '"></i>'
    return result;
}

function formatResult(result) {
    return result.text;
}
function formatResultSelection(result) {
    return result.id;
}
