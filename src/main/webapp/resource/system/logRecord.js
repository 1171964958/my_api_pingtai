var templateParams = {
		tableTheads:["调用url", "操作接口名","用户名", "调用时间","类型", "状态", "验证耗时", "执行耗时", "备注", "操作"],
		btnTools:[{
			type:"danger",
			size:"M",
			id:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		advancedQuery:{
			enable:true, 
			formTitle:"系统日志-高级查询",
			formControls:[[
	                       {label:"调用URL",
	                       	input:true,
	                       	name:"callUrl"},
	                       	{label:"调用时间",
	                       	 input:true,
	                       	 name:"opTimeText",
	                       	 datetime:true}
	                       ], [
	                        {label:"调用类型",
	                       	 select:true,
	                       	 name:"callType",
	                       	 option:[{value:"0", text:"用户调用"}, {value:"1", text:"外部API"},{value:"2", text:"内部自调"},{value:"3", text:"接口MOCK"}]}, 
	                       	{label:"状态",
	                       	 select:true,
	                       	 name:"interceptStatus",
	                       	 option:[{value:"0", text:"正常"}
	                       	 	,{value:"1", text:"无权限"},{value:"2", text:"未登录"}
	                       	 	,{value:"3", text:"放行"},{value:"4", text:"token不正确"}
	                       	 	,{value:"5", text:"禁用接口"},{value:"6", text:"系统错误"}
	                       	 	,{value:"7", text:"mock出错"},{value:"8", text:"接口不存在"}]}
	                        ], [
	                        {label:"调用用户",
	                       	 input:true,
	                       	 name:"callUserName"},
	                       	{label:"备注",
 	                       	 input:true,
 	                       	 name:"mark"}
	                        ]]
		}
	};

var columnsSetting = [
    {
    	"data":null,
    	"render":function(data, type, full, meta){
		  		return checkboxHmtl(data.recordId,data.recordId,"selectRecord");
	           }},
	{"data":"recordId"},
	ellipsisData("callUrl"),
	{
		"data":null,
		"className":"ellipsis",
		"render":function(data, type, full, meta){
			return data.opInterface == null ? "" : data.opInterface.opName;		
		}
	},
	{
		"data":null,
		"className":"ellipsis",
		"render":function(data, type, full, meta){
			return data.user == null ? "" : data.user.realName;			
		}
	},
	ellipsisData("opTime"),
	{
		"data":"callType",
		"render":function(data){
			var context = {
				"0":{
					btnStyle:"primary",
					status:"用户调用"
				},
				"1":{
					btnStyle:"success",
					status:"外部API"
				},
				"2":{
					btnStyle:"warning",
					status:"内部自调"
				},
				"3":{
					btnStyle:"success",
					status:"接口MOCK"
				}
			};
			
			return labelCreate(data, context);
		}
	},
	{
		"data":"interceptStatus",
		"render":function(data){
			var context = {
					"0":{
						btnStyle:"success",
						status:"正常"
					},
					"1":{
						btnStyle:"warning",
						status:"无权限"
					},
					"2":{
						btnStyle:"warning",
						status:"未登录"
					},
					"3":{
						btnStyle:"primary",
						status:"放行"
					},
					"4":{
						btnStyle:"danger",
						status:"token不正确"
					},
					"5":{
						btnStyle:"warning",
						status:"禁用接口"
					},
					"6":{
						btnStyle:"danger",
						status:"系统错误"
					},
					"7":{
						btnStyle:"danger",
						status:"mock出错"
					},
					"8":{
						btnStyle:"warning",
						status:"接口不存在"
					}
				};
				
			return labelCreate(data, context);
		}
	},
	{
		"data":"validateTime",
		"render":function(data) {
			return data || "0";
		}
			
	},
	{
		"data":"executeTime",
		"render":function(data) {
			return data || "0";
		}
			
	},
	longTextData("mark", "callUrl", "备注信息"),
	{
		"data":null,
		"render":function(){
			var context = [{
	    		title:"详情",
	    		markClass:"show-log-details",
	    		iconFont:"&#xe725;"
	    	},
	    	{
	    		title:"删除",
	    		markClass:"object-del",
	    		iconFont:"&#xe6e2;"
	    	}];
	    	return btnIconTemplate(context);
		}
	}
];

var eventList = {
	"#batch-del-object":function() {
		var checkboxList = $(".selectRecord:checked");
		batchDelObjs(checkboxList, top.LOG_RECORD_DEL_URL);
	},
	".object-del":function() {
		var data = table.row( $(this).parents('tr') ).data();
		delObj("确认要删除此此条日志信息吗？", top.LOG_RECORD_DEL_URL, data.recordId, this);
	},
	
	".show-log-details":function(){
		var data = table.row($(this).parents('tr') ).data();
		layer_show("操作日志详情", templates['log-record-details-view'](data), 900, 640, 1);
	}
};

var mySetting = {
		eventList:eventList,		
		listPage:{
			listUrl:top.LOG_RECORD_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 3, 10, 11]
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

/***********************************************************************************/