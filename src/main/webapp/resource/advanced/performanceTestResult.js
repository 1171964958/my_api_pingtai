var ptId;

var templateParams = {
		tableTheads:["名称", "接口场景", "测试环境", "线程数", "TPS", "响应时间", "开始时间","测试用户", "操作"],
		btnTools:[{
			type:"danger",
			size:"M",
			id:"batch-del",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		},{
			type:"primary",
			size:"M",
			id:"download-excel",
			iconFont:"&#xe641;",
			name:"汇总下载"
		}],
		advancedQuery:{
			enable:true, 
			formTitle:"性能测试结果-高级查询",
			pageOpenSuccessCallback:function (layero, index) {
				$.get(top.BUSINESS_SYSTEM_LIST_ALL_URL, function (json) {
					if (json.returnCode == 0) {
						$.each(json.data, function(i, n){
							layero.find("#systemId").append('<option value="'+ n.systemId +'">' + n.systemName + '[' 
								+ n.systemHost + ':' + n.systemPort + ']' + '</option>'); 					
						});	
						layero.find("#systemId").val(advancedQueryParameters["systemId"]);
					} else {
						layer.alert(json.msg, {icon:1});	
					}				
				});
			},
			formControls:[[
	                       {label:"配置名称",
	                       	input:true,
	                       	name:"ptName"},
	                       	{label:"接口名称",
	                       	  input:true,
	                       	  name:"interfaceName"} 
	                       ], [
	                        {label:"测试环境",
	                       	  input:true,
	                       	  name:"systemName"}, 
	                       	{label:"最小线程数",
	                       	 input:true,
	                       	 name:"threadCount",
	                       	 datatype:"number"} 
	                        ], [
	                        {label:"测试时间",
	                       	 input:true,
	                       	 name:"startTimeText",
	                       	 datetime:true},
	                       	{label:"测试用户",
	                       	 input:true,
	                       	 name:"testUserName"}
	                        ]]
		}
};

var columnsSetting = [
              {
			  	  	"data":null,
			  	  	"render":function(data, type, full, meta){                       
			          return checkboxHmtl(data.interfaceName, data.ptResultId, "selectPtResult");
			  }},
			  {"data":"ptResultId"},
			  ellipsisData("ptName"),
			  ellipsisData("interfaceName"),
			  ellipsisData("systemName"),
			  {
				"data":"threadCount"  
			  },			  
			  ellipsisData("tps"),
			  ellipsisData("responseTime"),
			  ellipsisData("startTime"),
			  {
				"data":"user.realName"  
			  },		  
			  {
				  	"data":null,
                    "render":function(data, type, full, meta){                    		                        	
                    	var context = [{
	                			title:"查看统计视图",
	                    		markClass:"view-test",
	                    		iconFont:"&#xe61e;"
                			},{
	                			title:"详情结果查看",
	                    		markClass:"result-details",
	                    		iconFont:"&#xe720;"
                			},{
	                			title:"下载参数化文件",
	                    		markClass:"download-parameterized-file",
	                    		iconFont:"&#xe616;"
                			},{
	                			title:"删除",
	                    		markClass:"object-del",
	                    		iconFont:"&#xe6e2;"
                			}];                     	                   	                 		                       	                        	
                    	return btnIconTemplate(context);
           }
}];
var eventList = {				
		"#batch-del":function(){
			batchOp($(".selectPtResult:checked"), top.PERFORMANCE_TEST_RESULT_DEL_URL, "删除", null, "ptResultId");
		},				
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此测试结果吗？", top.PERFORMANCE_TEST_RESULT_DEL_URL, {id:data.ptResultId}, this, "删除成功!");
		},
		"#download-excel":function(){ //下载汇总结果	
			if ($(".selectPtResult:checked").length < 1) {
				return;
			}
			var ids = [];
			$(".selectPtResult:checked").each(function(){
				ids.push(table.row( $(this).parents('tr') ).data().ptResultId);
			});
			
			$.post(top.PERFORMANCE_TEST_RESULT_SUMMARIZED_URL, {ids:ids.join(",")}, function(json){
				if (json.returnCode == 0) {
					window.open("../../" + top.DOWNLOAD_FILE_URL + "?downloadFileName=" + json.path.replace(/\\/g,"/"));
				} else {
					layer.alert(json.msg, {title:"提示"});
				}
			});
			
		},
		".view-test":function(){//查看统计视图
			var data = table.row( $(this).parents('tr') ).data();
			
			$.post(top.PERFORMANCE_TEST_RESULT_ANAYLZE_URL, {ptResultId:data.ptResultId}, function(json) {
				parent.layer_show("性能测试视图", templates["performance-test-task-view"](json.object), null, null, 1, function(layero, index){
					$(layero).find('.pt-task-btn-group:not(:eq(2))').hide();
					$(layero).find('.pt-task-btn-group:eq(2)').show();
					$(layero).find("#ptResultId").val(data.ptResultId);
					parent.createTestView(layero, json.object);
				}, null, null, {maxmin: true});
			});	
		},
		".result-details":function(){//查看详情结果
			var data = table.row( $(this).parents('tr') ).data();
			parent.layer_show(data.interfaceName + "-详细测试结果", "ptResultDetails.html?ptResultId=" + data.ptResultId, null, null, 2);
		},
		".download-parameterized-file":function(){//下载参数化文件
			var data = table.row( $(this).parents('tr') ).data();
			if (!strIsNotEmpty(data.parameterizedFilePath)) {
				layer.msg('无参数化文件!', {time:1500});
				return;
			}			
			window.open("../../" + top.DOWNLOAD_FILE_URL + "?downloadFileName=" + data.parameterizedFilePath.replace(/\\/g, '/'));
		}		
};

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			ptId = GetQueryString("ptId");
			if (ptId != null) {
				publish.renderParams.listPage.listUrl = top.PERFORMANCE_TEST_RESULT_LIST_URL + "?ptId=" + ptId;
			}
			df.resolve();
			return;
		},
		listPage:{
			listUrl:top.PERFORMANCE_TEST_RESULT_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 6, 7, 10]			
		},		
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

/**************************************************************************************************************************/