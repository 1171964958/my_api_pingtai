var templateParams = {
		tableTheads:["业务编码","模块名称","任务类型","开始时间", "结束时间","结束标识", "测试报告", "备注","操作"],
		btnTools:[{
			type:"danger",
			size:"M",
			markClass:"object-batch-del",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}]
	};


var columnsSetting = [
	  {
	  	"data":null,
	  	"render":function(data, type, full, meta){
	  			return checkboxHmtl(data.taskId, data.taskId, "selectTask");
	         }
	  },	  
	{"data":"taskId"}, 
	{"data":"module.moduleCode"},
	ellipsisData("module.moduleName"),
	{
		"data":"taskType",
		"render":function(data) {
			if (data == "0") {
				return "内部任务";
			} else {
				return "外部任务";
			}
		}
	},
	ellipsisData("startTime"),
	ellipsisData("finishTime"),
	{
		"data":"finishFlag",
		"render":function(data) {
			var option = {
		  			"Y":{
		  				btnStyle:"success",
		  				status:"已完成"
		  				},
	  				"N":{
	  					btnStyle:"danger",
	  					status:"未完成"
	  					},
	  				 	
		  	};	
		  	return labelCreate(data, option);			
		}
			
	},
	{
		"data":null,
		"render":function(data, type, full, meta){
          	var context =
          		[{
        			type:"primary",
        			size:"M",
        			markClass:"show-web-report",
        			name:"查看"
        		}];
              return btnTextTemplate(context);
              }
	},
	{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta) { 
	    	if (data != "" && data != null) {
		    	return '<a href="javascript:;" onclick="showMark(\'web自动化测试任务\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
	    	}
	    	return "";
	    }
      },
	{
        "data":null,
        "render":function(data, type, full, meta){
          var context = [{
	    		title:"任务删除",
	    		markClass:"object-del",
	    		iconFont:"&#xe6e2;"
	    	}];                           
        	return btnIconTemplate(context);
        }}
	
	];



var eventList = {
		".object-batch-del":function(){
			var checkboxList = $(".selectModule:checked");
			batchDelObjs(checkboxList, top.WEB_SCRIPT_TASK_DEL_URL);
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确认要删除此测试任务吗？", top.WEB_SCRIPT_TASK_DEL_URL, data.taskId, this);			
		},
		".show-web-report":function() {
			var data = table.row( $(this).parents('tr') ).data();
			if (strIsNotEmpty(data.reportPath)) {
				window.open("../../" + data.reportPath);
			}
		}
};

var mySetting = {
		eventList:eventList,
		listPage:{
			listUrl:top.WEB_SCRIPT_TASK_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 8, 9, 10],
			exportExcel:true
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});