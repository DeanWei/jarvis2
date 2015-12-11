var taskStatusJson=null;

$(function(){
    createDatetimePickerById("executeDate");
    createDatetimePickerById("scheduleDate");
    createDatetimePickerById("startDate");
    createDatetimePickerById("endDate");


    //初始化作业类型内容
    $.getJSON(contextPath+"/assets/json/jobType.json",function(data){
        $("#jobType").select2({
            data:data,
            width:'100%'
        });
    });
    //select采用select2 实现
    $(".input-group select").select2({width:'100%'});
    $.ajaxSettings.async = false;
    $.getJSON(contextPath+"/assets/json/taskStatus.json",function(data){
        taskStatusJson=data;
        $(data).each(function(index,content){
            var value=content.id;
            var text=content.text;
            var input =$("<input type='checkbox' name='taskStatus'/>");
            $(input).attr("value",value);

            if(value=='all'){
                $(input).click(function(){
                    if(this.checked){
                        $($("#taskStatus input")).each(function(){
                            this.checked=true;
                        });
                    }
                    else{
                        $($("#taskStatus input")).each(function(){
                            this.checked=false;
                        });
                    }
                });
            }

            $("#taskStatus").append(input);
            $("#taskStatus").append(text);
            $("#taskStatus").append('  ');
        });
    });
    $.ajaxSettings.async = true;


    $("#jobId").select2({
        ajax: {
            url: contextPath+"/api/job/getSimilarJobIds",
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
        escapeMarkup: function (markup) { return markup; },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,

        width:'100%'
    });
    $("#jobName").select2({
        ajax: {
            url: contextPath+"/api/job/getSimilarJobNames",
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
        escapeMarkup: function (markup) { return markup; },
        minimumInputLength: 1,
        templateResult: formatResult,
        templateSelection: formatResultSelection,
        width:'100%'
    });


    initData();
});


//查找
function search(){
    $("#content").bootstrapTable("destroy");
    initData();
}
//重置参数
function reset(){
    $("#scheduleDate").val("");
    $("#executeDate").val("");
    $("#startDate").val("");
    $("#endDate").val("");
    $("#jobId").val("all").trigger("change");
    $("#jobName").val("all").trigger("change");
    $("#jobType").val("all").trigger("change");
    $("#executeUser").val("all").trigger("change");
    $("#taskStatus input").each(function(i,c){
        this.checked=false;
    });
}


//获取查询参数
function getQueryPara(){
    var queryPara={};

    var scheduleDate=$("#scheduleDate").val();
    var executeDate=$("#executeDate").val();
    var startDate=$("#startDate").val();
    var endDate=$("#endDate").val();
    var jobId=$("#jobId").val();
    var jobName=$("#jobName").val();
    var jobType=$("#jobType").val();
    var executeUser=$("#executeUser").val();

    var taskStatus=new Array();
    var inputs=$("#taskStatus").find("input:checked");
    $(inputs).each(function(i,c){
        var value=$(c).val();
        if(value!='all'&&value!=''){
            taskStatus.push(value);
        }
    });

    jobId=jobId=="all"?'':jobId;
    jobName=jobName=='all'?'':jobName;
    jobType=jobType=='all'?'':jobType;
    executeUser=executeUser=="all"?'':executeUser;

    queryPara["scheduleDate"]=scheduleDate;
    queryPara["executeDate"]=executeDate;
    queryPara["startDate"]=startDate;
    queryPara["endDate"]=endDate;
    queryPara["jobId"]=jobId;
    queryPara["jobName"]=jobName;
    queryPara["jobType"]=jobType;
    queryPara["executeUser"]=executeUser;
    queryPara["taskStatusArrStr"]=JSON.stringify(taskStatus);

    return queryPara;
}

//初始化数据及分页
function initData(){
    var queryParams=getQueryPara();
    $("#content").bootstrapTable({
        columns:columns,
        pagination:true,
        sidePagination:'server',
        search:false,
        url:contextPath+'/api/task/getTasks',
        queryParams:function(params) {
            for(var key in queryParams){
                var value = queryParams[key];
                params[key]=value;
            }
            return params;
        },
        showColumns:true,
        showHeader:true,
        showToggle:true,
        pageSize:1,
        pageSize:20,
        pageList:[10,20,50,100,200,500,1000],
        paginationFirstText:'首页',
        paginationPreText:'上一页',
        paginationNextText:'下一页',
        paginationLastText:'末页',
        showExport:true,
        exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'doc', 'excel'],
        exportDataType:'all'
    });
}








var columns=[{
    field: 'taskId',
    title: '执行ID',
    switchable:true,
    visible:true
}, {
    field: 'attemptId',
    title: '最后尝试ID',
    switchable:true,
    visible:false
}, {
    field: 'jobId',
    title: '任务ID',
    switchable:true
}, {
    field: 'jobName',
    title: '任务名',
    switchable:true
},{
    field: 'jobType',
    title: '任务类型',
    switchable:true
},{
    field: 'content',
    title: '任务内容',
    switchable:true,
    visible:false
}, {
    field: 'params',
    title: '任务参数',
    switchable:true,
    visible:false
},{
    field: 'status',
    title: '执行状态',
    switchable:true,
    formatter:taskStatusFormatter
}, {
    field: 'executeUser',
    title: '执行用户',
    switchable:true
},{
    field: 'scheduleTime',
    title: '调度时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'progress',
    title: '进度',
    switchable:true,
    visible:false,
    formatter:progressFormatter
}, {
    field: 'workerGroupId',
    title: 'workerGroupId',
    switchable:true,
    visible:false
},{
    field: 'workerId',
    title: 'workerId',
    switchable:true,
    visible:false
}, {
    field: 'executeStartTime',
    title: '开始执行时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'executeEndTime',
    title: '执行结束时间',
    switchable:true,
    formatter:formatDateTime
}, {
    field: 'executeTime',
    title: '执行时长',
    switchable:false,
    visible:true,
    formatter:formatTimeInterval
},{
    field: 'createTime',
    title: '执行创建时间',
    switchable:false,
    visible:false,
    formatter:formatDateTime
}, {
    field: 'updateTime',
    title: '执行更新时间',
    switchable:true,
    visible:false,
    formatter:formatDateTime
},   {
    field: 'submitUser',
    title: '任务创建者',
    switchable:true,
    visible:false
}, {
    field: 'appName',
    title: '应用名',
    switchable:true,
    visible:false
}, {
    field: 'priority',
    title: '任务优先级',
    switchable:true,
    visible:false
},  {
    field: 'operation',
    title: '操作',
    switchable:true,
    formatter: operateFormatter
}];

function operateFormatter(value, row, index) {
    //console.log(row);
    var taskId=row["taskId"];
    //console.log(jobId);
    var result= [
        '<a class="edit" href="'+contextPath+'/task/detail?taskId='+taskId+'" title="查看执行详情" target="_blank">',
        '<i class="glyphicon glyphicon-eye-open"></i>',
        '</a>  '
    ].join('');

    //console.log(result);

    return result;
}

//执行状态格式化
function taskStatusFormatter(value,row,index){
    return formatStatus(taskStatusJson,value);
}
//百分比格式化
function progressFormatter(value,row,index){
    var result=value*100+"%";
    return result;
}



function formatResult(result){
    return result.text;
}
function formatResultSelection(result){
    return result.id;
}
