var appStatus = null;
var appType = null;
$(function () {
    $.ajaxSettings.async = false;
    $.getJSON(contextPath + "/api/app/getAppStatus", function (data) {
        appStatus = data;
    });

    $.getJSON(contextPath + "/api/app/getAppType", function (data) {
        appType = data;
    });
    $.ajaxSettings.async = true;

    $(".input-group select").select2({width: '100%'});
    initType(appType);
    initStatus(appStatus);

    initData();
});

function initType(appType) {
    var data = new Array();
    var all = {};
    all["id"] = "all";
    all["text"] = "全部";
    data.push(all);
    $(appType).each(function (i, c) {
        data.push(c);
    });
    $("#appType").select2({
        data: data,
        width: '100%'
    });
    $("#appType").val(1).trigger("change");
}
function initStatus(appStatus) {
    var data = new Array();
    var all = {};
    all["id"] = "all";
    all["text"] = "全部";
    data.push(all);
    $(appStatus).each(function (i, c) {
        if (c["id"] != 3) {
            data.push(c);
        }
    });
    $("#status").select2({
        data: data,
        width: '100%'
    });

}

function search() {
    $("#content").bootstrapTable('destroy', '');
    initData();
}

function reset() {
    var selects = $(".input-group select");
    $(selects).each(function (i, c) {
        $(c).val("all").trigger("change");
    });
}

//获取查询参数
function getQueryPara() {
    var queryPara = {};

    var appName = $("#appName").val();
    var appType = $("#appType").val();
    var status = $("#status").val();

    appName = appName == 'all' ? '' : appName;
    appType = appType == 'all' ? '' : appType;
    status = status == 'all' ? '' : status;

    queryPara["appName"] = appName;
    queryPara["appType"] = appType;
    queryPara["status"] = status;

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
        url: contextPath + '/api/app/getApps',
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

function modifyAppStatus(appId, status, appName, maxConcurrency) {
    var data = {appId: appId, applicationName: appName, status: status, maxConcurrency: maxConcurrency};
    //删除需要提示
    if (3 == status) {
        (new PNotify({
            title: '删除App',
            text: '确定删除App?',
            icon: 'glyphicon glyphicon-question-sign',
            hide: false,
            confirm: {
                confirm: true
            },
            buttons: {
                closer: false,
                sticker: false
            },
            history: {
                history: false
            }
        })).get().on('pnotify.confirm', function () {
                requestRemoteRestApi("/api/app/edit", "修改应用状态", data);
                search();
            }).on('pnotify.cancel', function () {
            });
    }
    //启用或停用
    else {
        requestRemoteRestApi("/api/app/edit", "修改应用状态", data);
        search();
    }
}


var columns = [{
    field: 'appId',
    title: '应用id',
    switchable: true,
    visible: false
}, {
    field: 'appName',
    title: '应用名称',
    switchable: true
}, {
    field: 'appKey',
    title: 'appkey',
    switchable: true
}, {
    field: 'appType',
    title: '应用类型',
    switchable: true,
    formatter: appTypeFormatter,
    visible: false
}, {
    field: 'status',
    title: '应用状态',
    switchable: true,
    formatter: appStatusFormatter
}, {
    field: 'maxConcurrency',
    title: '最大并发数',
    switchable: true
}, {
    field: 'owner',
    title: '维护人',
    switchable: true
}, {
    field: 'updateUser',
    title: '最后更新人',
    switchable: true,
    visible: false
}, {
    field: 'createTime',
    title: '创建时间',
    switchable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'updateTime',
    title: '更新时间',
    switchable: true,
    formatter: formatDate,
    visible: false
}, {
    field: 'operation',
    title: '操作',
    switchable: true,
    formatter: operateFormatter
}];


function appStatusFormatter(value, row, index) {
    var result = "";
    //停用
    if (2 == value) {
        result = "<i class='glyphicon glyphicon-pause text-danger'></i>";
    }
    //启用
    else if (1 == value) {
        result = "<i class='glyphicon glyphicon-ok text-success'></i>";
    }

    return result;
}

function operateFormatter(value, row, index) {
    var appId = row["appId"];
    var appName = row["appName"];
    var maxConcurrency = row["maxConcurrency"];
    var status = row["status"];
    var result = [
        '<a class="edit" href="' + contextPath + '/manage/appAddOrEdit?appId=' + appId + '" title="编辑应用信息" target="_blank">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>  '
    ].join('');

    var operation = '';

    $(appStatus).each(function (i, c) {
        if (c["id"] != 'all' && c["id"] != status) {
            var style = "";
            if (1 == c["id"]) {
                style = "btn btn-xs btn-success";
            }
            else if (2 == c["id"]) {
                style = "btn btn-xs btn-danger";
            }
            operation += '&nbsp;<a class="' + style + '" href="javascript:void(0)" onclick="modifyAppStatus(' + appId + ',' + c["id"] + ',\'' + appName + '\',' + maxConcurrency + ')" >' + c["text"] + '</a>';
        }
    });
    return result + "&nbsp" + operation;
}

function appTypeFormatter(value, row, index) {
    var result = '';
    if (value == 1) {
        result = "普通";
    }
    else if (value == 2) {
        result = "管理";
    }
    else {
        result = "未定义类型:" + value;
    }
    return result;
}