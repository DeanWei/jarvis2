var taskStatusJson = null;
var taskStatusColor = null;

$(function () {
    $('#dataTime').datetimepicker({
        language: 'zh-CN',
        minView: 'hour',
        format: 'yyyy-mm-dd hh:ii',
        autoclose: true
    });
    //初始化作业类型内容
    $.getJSON(contextPath + "/assets/json/jobType.json", function (data) {
        $("#jobType").select2({
            data: data,
            width: '100%',
            tags: true
        });
    });
    //初始化作业来源内容
    $.getJSON(contextPath + "/assets/json/jobPriority.json", function (data) {
        $("#priority").select2({
            data: data,
            width: '100%'
        });
    });
    //select采用select2 实现
    $(".input-group select").select2({width: '100%'});

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

    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/job/getJobStatus", function (data) {
        taskStatusJson = data;
    });
    //初始化颜色
    $.getJSON(contextPath + "/assets/json/taskStatusColor.json", function (data) {
        taskStatusColor = data;
    });
    $.ajaxSettings.async = true;
    initData();
});

function formatResult(result) {
    return result.text;
}
function formatResultSelection(result) {
    return result.id;
}

function search() {
    $("#content").bootstrapTable('destroy', '');
    initData();
}
//重置参数
function reset() {
    $("#jobName").val("").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#priority").val("all").trigger("change");
    $("#executeUser").val("all").trigger("change");
    $("#dataTime").val("");
}

//获取查询参数
function getQueryPara() {
    var queryPara = {};

    var jobNameList = $("#jobName").val();
    var jobTypeList = $("#jobType").val();
    var priorityList = $("#priority").val();
    var executeUserList = $("#executeUser").val();
    var dataTime = $("#dataTime").val();

    jobNameList = jobNameList == 'all' ? undefined : jobNameList;
    jobNameList = jobNameList == null ? undefined : jobNameList;
    jobTypeList = jobTypeList == 'all' ? undefined : jobTypeList;
    jobTypeList = jobTypeList == null ? undefined : jobTypeList;
    executeUserList = executeUserList == "all" ? undefined : executeUserList;
    executeUserList = executeUserList == null ? undefined : executeUserList;
    priorityList = priorityList == "all" ? undefined : priorityList;
    priorityList = priorityList == null ? undefined : priorityList;
    dataTime = dataTime == '' ? undefined : dataTime;

    queryPara["jobNameList"] = JSON.stringify(jobNameList);
    queryPara["jobTypeList"] = JSON.stringify(jobTypeList);
    queryPara["executeUserList"] = JSON.stringify(executeUserList);
    queryPara["priorityList"] = JSON.stringify(priorityList);
    queryPara["dataTime"] = dataTime;

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
        url: contextPath + '/api/plan/getPlans',
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
        pageSize: 20,
        pageList: [10, 20, 50, 100, 200, 500, 1000],
        paginationFirstText: '首页',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        paginationLastText: '末页',
        showExport: true,
        exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType: 'all'
    });
}


var columns = [{
    field: 'jobId',
    title: '任务ID',
    switchable: true,
    visible: true
}, {
    field: 'jobName',
    title: '任务名称',
    switchable: true,
    formatter:jobNameFormatter
}, {
    field: 'appId',
    title: 'APP ID',
    switchable: true,
    visible: false
}, {
    field: 'appName',
    title: 'APP名',
    switchable: true,
    visible: false
}, {
    field: 'jobType',
    title: '任务类型',
    switchable: true,
    visible: true
}, {
    field: 'bizGroupId',
    title: '业务组ID',
    switchable: true,
    visible: false
}, {
    field: 'bizGroupName',
    title: '业务组名',
    switchable: true,
    visible: true
}, {
    field: 'priority',
    title: '任务优先级',
    switchable: true,
    visible: true
}, {
    field: 'submitUser',
    title: '提交用户',
    switchable: true,
    visible: true
}, {
    field: 'workerGroupId',
    title: 'workerGroupId',
    switchable: true,
    visible: false
}, {
    field: 'workerGroupName',
    title: 'worker组名',
    switchable: true,
    visible: false
}, {
    field: 'scheduleTimeFirst',
    title: '首个调度时间',
    switchable: true,
    formatter: formatDateTime,
    visible: false
}, {
    field: 'taskStatus',
    title: '执行状态一览',
    switchable: true,
    visible: true,
    formatter: taskStatusListFormatter
},  {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'updateTime',
    title: '最后更新时间',
    switchable: true,
    visible: false,
    formatter: formatDateTime
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateFormatter
}];

function jobNameFormatter(value,row,index){
    var result='<a target="_blank" href="'+contextPath+"/job/detail?jobId="+row["jobId"]+'">'+value+'</a>';

    return result;
}

function operateFormatter(value, row, index) {
    var jobId = row["jobId"];
    var dependUrl = contextPath + '/job/dependency?jobId=' + jobId;
    var taskUrl = contextPath + '/task?jobIdList=' + JSON.stringify([jobId]) + '&scheduleDate=' + planQo.scheduleDate;
    var result = [
        '<a class="edit" href="' + dependUrl + '" title="查看任务依赖" target="_blank">',
        '<i class="glyphicon glyphicon-object-align-vertical"></i>',
        '</a>  ',
        '<a class="edit" href="' + taskUrl + '" title="查看当前执行" target="_blank">',
        '<i class="glyphicon glyphicon-list"></i>',
        '</a>  ',
    ].join('');
    return result;
}
function taskStatusFormatter(value, row, index) {
    var color = taskStatusColor[value];
    var result = '<i class="fa fa-circle fa-2x" style="color: ' + color + '"></i>';
    return result;
}
function taskStatusListFormatter(value, row, index) {
    var result = "";
    $(value).each(function(i,c){
        var taskId= c.taskId;
        var status= c.status;
        var color = taskStatusColor[status];


        var item='<a target="_blank" href="'+contextPath+"/task/detail?taskId="+taskId+'"><i class="fa fa-circle fa-2x" style="color: ' + color + '"></i></a>';

        result = result + item;
    });


    return result;
}

function StringFormatter(value, row, index) {
    return value;
}

