var taskId;
var currIndex;//当前正在操作的layer窗口的index
var quartzStatus;//quartz定时器状态
var expressionEditHtml;//cron表达式简单编辑页面

var cronExpressionData = [{"labelText":"小时","name":"hour","options":
		{"*":"每小时","0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10"
		:"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19"
		,"20":"20","21":"21","22":"22","23":"23"}},{"labelText":"分钟","name":"minute","options":
		{"*":"每分钟","0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10"
		:"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21"
		:"21","22":"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32"
		:"32","33":"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40","41":"41","42":"42","43"
		:"43","44":"44","45":"45","46":"46","47":"47","48":"48","49":"49","50":"50","51":"51","52":"52","53":"53","54"
		:"54","55":"55","56":"56","57":"57","58":"58","59":"59"}},{"labelText":"秒","name":"second","options":
		{"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11"
		:"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22"
		:"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32":"32","33"
		:"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40","41":"41","42":"42","43":"43","44"
		:"44","45":"45","46":"46","47":"47","48":"48","49":"49","50":"50","51":"51","52":"52","53":"53","54":"54","55"
		:"55","56":"56","57":"57","58":"58","59":"59"}},{"labelText":"日期","name":"date","options":{"*":"每天",
		"1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12","13"
		:"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22":"22","23":"23",
		"24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31"}},{"labelText":"星期","name":"week"
		,"options":{"?":"不设置"}},{"labelText":"月份","name":"month","options":{"*":"每个月","1":"1","2":"2","3":"3","4":"4"
		,"5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12"}}];


var templateParams = {
		tableTheads:["名称", "类型", "测试集", "定时规则表达式", "已执行次数", "最后一次执行", "创建时间", "开启状态", "邮件通知","创建用户","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加新任务"
		},{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		}],
		formControls:[{
					edit:true,
					label:"任务ID",  	
					objText:"taskIdText",
					input:[{	
						hidden:true,
						name:"taskId"
						}]
					},
					{
						required:true,
						label:"任务名称",  
						input:[{	
							name:"taskName"
							}]					
					},
					{
						required:true,
						label:"任务类型",  			
						select:[{	
							name:"taskType",
							option:[{
								value:"0",
								text:"接口自动化"
							}]
							}]				
					 },
					 {
						 required:true,
						 label:"测试集",
						 input:[{
							 hidden:true,
							 name:"relatedId"
							}],
						 button:[{
							 style:"success",
							 value:"选择",
							 name:"choose-task-set"
						}]					 
					 },
					 {
							required:true,
							label:"邮件推送报告",  			
							select:[{	
								name:"mailNotify",
								option:[{
									value:"1",
									text:"是"
								},{
									value:"0",
									text:"否"
								}]
							}]				
				     },
					 {
						 name:"status",
						 value:"1"
					 },
					 {
						 name:"createTime",
						 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
						
					 },
					 {
						 name:"user.userId"
											
					 },
					 {
						 name:"lastFinishTime"
					 },
					 {
						 name:"taskCronExpression"
					 },
					 {
						 name:"runCount",
						 value:"0"
					 }
		      ]};

var columnsSetting = [
                      {
					  	  	"data":null,
					  	  	"render":function(data, type, full, meta){                       
					          return checkboxHmtl(data.taskName, data.taskId, "selectTask");
					  }},
					  {"data":"taskId"},
					  ellipsisData("taskName"),
					  {
						   	"data":"taskType",
							"render":function(data) {
								var option = {
	                		  			"0":{
	                		  				btnStyle:"success",
	                		  				status:"接口"
	                		  				},
	            		  				"1":{
	            		  					btnStyle:"success",
	            		  					status:"Web"
	            		  					}
	                		  	};	
	                		  	return labelCreate(data, option);
					  }},
					  ellipsisData("setName"),
					  {
						  "data":null,
						  "render":function(data) {
							  if (data.status == "0") {
								  return data.taskCronExpression;
							  }
							  var context =
	                          		[{
	                        			type:"primary",
	                        			size:"M",
	                        			markClass:"setting-cron-expression",
	                        			name:"设定"
	                        		}];
	                          return btnTextTemplate(context);
							  
						}
					  },
					  {
						  "data":"runCount"
					  },
					  {
						  "data":"lastFinishTime"
					  },
					  {
						  "data":"createTime"
					  },
					  {
						  	"data":"status",
							"render":function(data) {
								var option = {
	                		  			"0":{
	                		  				btnStyle:"success",
	                		  				status:"正在运行"
	                		  				},
	            		  				"1":{
	            		  					btnStyle:"disabled",
	            		  					status:"已停止"
	            		  					}
	                		  	};	
	                		  	return labelCreate(data, option);
							}
					  },
					  {
						  	"data":"mailNotify",
							"render":function(data) {
								var option = {
	                		  			"1":{
	                		  				btnStyle:"success",
	                		  				status:"是"
	                		  				},
	            		  				"0":{
	            		  					btnStyle:"disabled",
	            		  					status:"否"
	            		  					}
	                		  	};	
	                		  	return labelCreate(data, option);
							}
					  },
					  ellipsisData("createUserName"),
					  {
						  	"data":null,
	                        "render":function(data, type, full, meta){
	                        	var statusIcon = '&#xe631;';
	                        	var statusTitle = '停止定时任务';

	                        	if (data.status != "0") {
	                        		statusIcon = '&#xe601;';
	                        		statusTitle = '启动定时任务';
	                        	}
	                        		                        	
	                        	var context = [{
	                        		title:statusTitle,
	                        		markClass:"start-stop-task",
	                        		iconFont:statusIcon
	                        	}];  
	                        	
	                        	if (data.status != "0") {
	                        		context.push({
	                        			title:"编辑",
		                        		markClass:"object-edit",
		                        		iconFont:"&#xe6df;"
	                        		});
	                        		context.push({
	                        			title:"删除",
		                        		markClass:"object-del",
		                        		iconFont:"&#xe6e2;"
	                        		});
	                        	}                        		                       	                        	
	                        	return btnIconTemplate(context);
	                        }
					  }];

var eventList = {
		'#onCl':function() {
			updateRuleExpression($("#taskCronExpression").val(), $("#taskId").val());
		},
		'article .cron-item':{
				'change' : function(){
					showExplain();
				}
			},
		'.object-del':function() {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此定时任务？请慎重操作!", top.TASK_DEL_URL, data.taskId, this);
		},
		'.object-edit':function() {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.taskId;
			layer_show("编辑定时任务信息", editHtml, "680", "380", 1);
			publish.init();	
		},
		'.start-stop-task':function() {						
			if (quartzStatus != "true") {
				layer.msg('请先恢复定时任务调度器!', {icon:0, time:1500});
				return false;
			}
			
			var data = table.row( $(this).parents('tr') ).data();
			
			layer.confirm('确认停止/启动该定时任务?', {title:'提示', icon:0}, function(index) {
				
				$.get(data.status == "0" ? top.TASK_STOP_TASK_URL : top.TASK_ADD_RUNABLE_TASK_URL, {taskId:data.taskId}, function(json) {
					if (json.returnCode == 0) {
						layer.msg('操作成功!', {icon:1, time:1500});
						refreshTable();
					} else {
						layer.alert(json.msg, {icon:5});
					}
					layer.close(index);
				});
			});			
		},
		'.setting-cron-expression':function() {
			var data = table.row( $(this).parents('tr') ).data();
			layer.confirm("请选择编辑模板:", {title:"定时规则编辑", btn:["填写CRON表达式","简易设置"]},
		    		function(index){
						layer.close(index);
			    		layer.prompt({
			    			  formType: 0,
			    			  value: data.taskCronExpression,
			    			  title: '请输入CRON表达式',
			    			}, function(value, index, elem){   			  
			    				updateRuleExpression(value, data.taskId, index);   			  
			    			});
		    		},
		    		function(index){
		    			layer_show ("定时规则设置", expressionEditHtml, '800', '570', '1', function(layero, index) {
		    				if (data.taskCronExpression != null && data.taskCronExpression != "") {
		    					var expressionArray = (data.taskCronExpression).split(" ");
		    					$("#second").val(expressionArray[0]);
		    					$("#minute").val(expressionArray[1]);
		    					$("#hour").val(expressionArray[2]);
		    					$("#date").val(expressionArray[3]);
		    					$("#month").val(expressionArray[4]);
		    					//$("#week").val(expressionArray[5]);
		    					$("#week").parents(".row").hide();
		    					$("#year").val(expressionArray[6]);	
		    					$("#taskCronExpression").val(data.taskCronExpression);
		    				}
		    				$(layero).find("#taskId").val(data.taskId);
		    				showExplain();
		    			});		    			
		    			layer.close(index);
		    	});
		},
		'#op-quartz':function() {
			layer.confirm('确定进行此操作?', {icon:0, title:'警告'}, function(index) {
				layer.close(index);
				$.get(quartzStatus == "true" ? top.TASK_STOP_QUARTZ_URL : top.TASK_START_QUARTZ_URL, function(data) {
					if (data.returnCode == 0) {
						quartzStatus = data.status;
						changeStatusView();
						layer.msg("操作成功!", {icon:1, time:1500});
					} else {
						layer.alert(data.msg, {icon:5});
					}
				});
			});						
		},
		'#add-object':function() {
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("增加定时任务", editHtml, "680", "360", 1);
			//layer.full(index);
			publish.init();	
		},
		'#batch-op':function() {
			layer.confirm(
					'请选择你需要进行的批量操作:',
					{
						title:'批量操作',
						btn:['运行任务','停止任务','删除任务'],
						btn3:function(index){
							layer.close(index);
							batchDelObjs($(".selectTask:checked"), top.TASK_DEL_URL);
						}
					},function(index){ 
						layer.close(index);
						batchOp($(".selectTask:checked"), top.TASK_ADD_RUNABLE_TASK_URL, "运行", null, "taskId");
					},function(index){
						layer.close(index);
						batchOp($(".selectTask:checked"), top.TASK_STOP_TASK_URL, "停止", null, "taskId");
					});	
		},
		'#choose-task-set':function() {
			var type = $("#taskType").val();
			
			//判断是否为接口自动化类型
			if (type == "0") {
				layer_show("接口自动化-选择测试集", "../message/testSet.html?selectMode=0", 1100, 660, 2);
			}
			//web自动化
			if (type == "1") {
				layer.alert("尚未完成!");
			}			
			
			
		}		
};


var mySetting = {
		eventList:eventList,
		customCallBack:function(p) {
			$.get(top.TASK_GET_QUARTZ_STATUS_URL, function(data) {
				if (data.returnCode == 0) {
					quartzStatus = data.status;
					changeStatusView();
				} else {
					layer.alert(data.msg, {icon:5});
				}
			});
			
			expressionEditHtml =  templates["quartz-expression-edit"](cronExpressionData);
		},
		listPage:{
			listUrl:top.TASK_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			dblclickEdit:false,
			columnsJson:[0, 4, 5, 12]			
		},
		editPage:{
			editUrl:top.TASK_EDIT_URL,
			getUrl:top.TASK_GET_URL,
			rules:{
				taskName:{
					required:true,
					minlength:1,
					maxlength:100,
					remote:{
						url:top.TASK_CHECK_NAME_URL,
						type:"post",
						dataType:"json",
						data: {                   
					        taskName: function() {
					            return $("#taskName").val();
					        },
					        taskId:function(){
					        	return $("#taskId").val();
					        }
					}}
				},
				relatedId:{
					required:true
				}								
			},
			renderCallback:function(obj){
				$("#choose-task-set").before('<span>' + obj.setName + '&nbsp;</span>');
			}
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});


/***********************************************************************************************************/
function showExplain() {
	var explain='';
	var year = $("#year").val();
	var second = $("#second").val();
	var minute = $("#minute").val();
	var hour = $("#hour").val();
	var date = $("#date").val();
	var month = $("#month").val();
	var week = $("#week").val();
	
	$("#taskCronExpression").val($.trim(second + " " + minute + " " + hour + " " + date + " " + month + " " + week + " " + year));
	
	if (year != "") {
		explain += year + "年的";
	} 
	

	if (month != "*") {
		explain += month + "月份的";
	}
	
	if(date != "?"){
		if(date != "*"){
			explain += date + "号的";
		} else {
			explain += "每天";
		}
	}
	
	if (week != "?") {		
		if (week != "*") {
			explain += "每个" + $("#week option:selected").text() + "的";
		} else {
			explain += "每天";
		}
	}
	
	if (hour !="*") {
		explain += hour + "点";
	} else {
		explain += "每个小时的";
	}
	
	if (minute != "*") {
		explain += minute + "分";
	} else {
		explain += "每分钟";
	}
	
	if (second != "0") {
		explain += second + "秒";
	}
	explain += "开始执行定时任务!";
	
	$("#expressExplain").text(explain);
}

function changeStatusView () {
	var obj = $("#op-quartz");
	if (quartzStatus == "true") {
		obj.removeClass("btn-success").addClass("btn-default").text("暂停定时任务调度器");		
	} else {
		obj.removeClass("btn-default").addClass("btn-success").text("恢复定时任务调度器");
		layer.msg('当前任务调度器处于暂停状态!', {icon:0, time:2500});
	}
}

function updateRuleExpression(expression, id, layerIndex) {
	$.post(top.TASK_UPDATE_CRON_EXPRESSION_URL, {taskId:id, taskCronExpression:expression}, function(json) {
		if (json.returnCode == 0) {
			layer.msg('更新成功!', {icon:1, time:1500});
			if (layerIndex == null) {
				layer.closeAll('page');
			} else {
				layer.close(layerIndex);
			}
			
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});
}