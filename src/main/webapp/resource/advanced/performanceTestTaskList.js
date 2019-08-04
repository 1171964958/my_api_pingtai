var templateParams = {
		tableTheads:["接口场景", "测试环境", "开始时间", "当前状态", "响应时间", "TPS", "事务总数", "成功数", "失败数", "错误日志", "失败信息","操作"],
		btnTools:[{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		}]
	};

var columnsSetting = [
                      {
        			  	  	"data":null,
        			  	  	"render":function(data, type, full, meta){                       
        			          return checkboxHmtl(data.interfaceName, data.objectId, "selectObject");
        			  }},
        			  {"data":"objectId"},
        			  ellipsisData("interfaceName"),
        			  ellipsisData("systemName"),
        			  ellipsisData("startTime"),
        			  {
        				"data":"currentStatus",
        				"render":function(data){
        					return labelCreate(data);
        				}
        			  },
        			  ellipsisData("responseTime"),
        			  ellipsisData("tps"),
        			  ellipsisData("totalCount"),
        			  ellipsisData("successCount"),
        			  ellipsisData("failCount"),       			  
        			  {
        				    "data":"errorMsg",
        				    "render":function(data, type, full, meta ){
        				    	var context =
        	                		[{
        	              			type:"danger",
        	              			size:"M",
        	              			markClass:"show-error-messages",
        	              			name:data.length
        	              		}];
        	                    return btnTextTemplate(context);
        				    	
        				    }
        			  },
        			  {
      				    "data":"resultMark",
      				    "render":function(data, type, full, meta ){
      				    	var context =
      	                		[{
      	              			type:"danger",
      	              			size:"M",
      	              			markClass:"show-result-marks",
      	              			name:data.length
      	              		}];
      	                    return btnTextTemplate(context);
      				    	
      				    }
        			  },
        			  {
        				  	"data":null,
                            "render":function(data, type, full, meta){                    		                        	
                            	var context = [{
        	                			title:"打开视图",
        	                    		markClass:"view-test",
        	                    		iconFont:"&#xe61e;"
                        			},{
        	                			title:"停止测试",
        	                    		markClass:"stop-test",
        	                    		iconFont:"&#xe631;"
                        			},{
        	                			title:"删除测试任务",
        	                    		markClass:"del-test",
        	                    		iconFont:"&#xe6e2;"
                        			}];                     	                   	                 		                       	                        	
                            	return btnIconTemplate(context);
                   }
        }];

var eventList = {	
		"#batch-op":function(){
			if ($(".selectObject:checked").length < 1) return;
			layer.confirm(
					'请选择你需要进行的批量操作:',
					{
						title:'批量操作',
						btn:['运行','停止','删除'],
						btn3:function(index){
							layer.close(index);
							batchOp($(".selectObject:checked"), top.PERFORMANCE_TEST_TASK_DEL_URL, "删除", null, "objectId");
						}
					},function(index){ 
						layer.close(index);
						batchOp($(".selectObject:checked"), top.PERFORMANCE_TEST_TASK_ACTION_URL, "启动", null, "objectId");
					},function(index){
						layer.close(index);
						batchOp($(".selectObject:checked"), top.PERFORMANCE_TEST_TASK_STOP_URL, "停止", null, "objectId");
					});	
		},
		".show-result-marks":function() {
			var data = table.row( $(this).parents('tr') ).data().resultMark;
			createViewWindow(data.join("\n"), {
				title:"查看失败详细信息", //标题
				copyBtn:true//是否显示复制按钮
			});
		},
		".show-error-messages":function() {
			var data = table.row( $(this).parents('tr') ).data().errorMsg;
			createViewWindow(data.join("\n"), {
				title:"查看错误信息", //标题
				copyBtn:true//是否显示复制按钮
			});
		},
		".view-test":function(){
			var data = table.row( $(this).parents('tr') ).data();
			$.post(top.PERFORMANCE_TEST_TASK_VIEW_URL, {objectId:data.objectId}, function(text){
				if (text.returnCode == 0) {	
					parent.layer_show("性能测试视图", templates["performance-test-task-view"](text.object), null, null, 1, function(layero, index){
						window.intervalId = parent.createTestView(layero, text.object);
					}, null, function(){
						parent.clearInterval(window.intervalId);
					}, {maxmin: true});
				} else {
					layer.alert(json.msg, {icon:5});
				}						
			});
			
		},
		".stop-test":function(){
			var data = table.row( $(this).parents('tr') ).data();
			layer.confirm('确认停止该测试任务吗？', {title:'警告'}, function(index){
				$.post(top.PERFORMANCE_TEST_TASK_STOP_URL, {objectId:data.objectId}, function(json) {
					if (json.returnCode == 0) {
						layer.msg('停止成功,请稍后刷新列表查看!', {icon:1, time:1800});
					} else {
						layer.alert(json.msg, {icon:5, title:'提示'});
					}
				});				
				layer.close(index);
			});			
		},
		".del-test":function(){
			var obj = this;
			var data = table.row( $(this).parents('tr') ).data();			
			layer.confirm('确认删除该测试任务吗,<span style="color:red;"><strong>删除的测试任务将不会保存所有的测试结果</strong></span>？', {title:'警告'}, function(index){
				$.post(top.PERFORMANCE_TEST_TASK_DEL_URL, {objectId:data.objectId}, function(json) {
					if (json.returnCode == 0) {
						layer.msg('删除成功,请稍等刷新列表查看!', {icon:1, time:1800});
						table.row($(obj).parents('tr')).remove().draw();
					} else {
						layer.alert(json.msg, {icon:5, title:'提示'});
					}
				});			
				layer.close(index);
			});
		}
};


var mySetting = {
		eventList:eventList,
		listPage:{
			listUrl:top.PERFORMANCE_TEST_TASK_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 13],
			dblclickEdit:false,
			exportExcel:false,
			dtOtherSetting:{
				"bStateSave": false,//状态保存	
				"serverSide":false
			}
		},		
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});