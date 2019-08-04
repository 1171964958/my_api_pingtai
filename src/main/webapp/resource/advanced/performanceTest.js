var templateParams = {
		tableTheads:["名称", "接口场景", "测试环境", "线程数", "创建用户", "创建时间", "测试结果", "备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"list-result",
			iconFont:"&#xe623;",
			name:"性能报告"
		},{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加测试配置"
		},{
			type:"danger",
			size:"M",
			id:"batch-del",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		},{
			type:"success",
			size:"M",
			id:"test-task",
			iconFont:"&#xe6f3;",
			name:"任务列表"
		}],
		formControls:[{
					edit:true,
					label:"ID",  	
					objText:"ptIdText",
					input:[{	
						hidden:true,
						name:"ptId"
						}]
					},
					{
						required:true,
						label:"名称",  
						input:[{	
							name:"ptName"
							}]					
					},
					{
						 required:true,
						 label:"测试场景",
						 input:[{
							 hidden:true,
							 name:"messageScene.messageSceneId"
							}],
						 button:[{
							 style:"success",
							 value:"选择",
							 name:"choose-message-scene"
						}]					 
					 },
					 {
						 required:true,
						 label:"测试环境",
						 input:[{
							 hidden:true,
							 name:"businessSystem.systemId"
							}],
						 button:[{
							 style:"success",
							 value:"选择",
							 name:"choose-business-system"
						}]					 
					 },
					 {
							required:true,
							label:"最大线程数",  
							input:[{	
								name:"threadCount",
								value:"1"
								}]					
					 },
					 {
						 label:"测试配置",
						 input:[{
							 hidden:true,
							 name:"keepAlive"
							},
							{
							 hidden:true,
							 name:"maxCount"
							}
							,
							{
							 hidden:true,
							 name:"maxTime"
							},
							{
							 hidden:true,
							 name:"formatCharacter"
							},
							{
							 hidden:true,
							 name:"parameterizedFilePath"
							},
							{
							 hidden:true,
							 name:"parameterReuse"
							}
							,
							{
							 hidden:true,
							 name:"parameterPickType"
							}],
						 button:[{
							 style:"primary",
							 value:"配置",
							 name:"test-setting"
						}]					 
					 },					 
					 {
						 name:"createTime",
						 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
						
					 },
					 {
						 name:"user.userId"											
					 },
					 {
						 name:"protocolType"											
					 },
					 {
						label:"备注",  			
						textarea:[{
							name:"mark"
						}]
					},
				
]};

var columnsSetting = [
              {
			  	  	"data":null,
			  	  	"render":function(data, type, full, meta){                       
			          return checkboxHmtl(data.ptName, data.ptId, "selectPt");
			  }},
			  {"data":"ptId"},
			  ellipsisData("ptName"),
			  {
				"data":"messageScene",
				"className":"ellipsis",
				"render":function(data){
					data = data.interfaceName + "-" + data.messageName + "-" + data.sceneName
					return '<span title="' + data + '">' + data + '</span>';
				}
			  },
			  ellipsisData("businessSystem.systemName"),
			  {
				"data":"threadCount"  
			  },			  
			  ellipsisData("user.realName"),
			  ellipsisData("createTime"),
			  {
				  "data":"resultNum",
				  "render":function(data){
					  var context =
                  		[{
                			type:"default",
                			size:"M",
                			markClass:"show-performance-result",
                			name:data
                		}];
                      return btnTextTemplate(context);
				  }
			  },
			  {
				    "data":"mark",
				    "className":"ellipsis",
				    "render":function(data, type, full, meta ){
				    	if (data != "" && data != null) {
			  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.ptName + '-\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
					    	}
					    return "";
				    }
			  },
			  {
				  	"data":null,
                    "render":function(data, type, full, meta){                    		                        	
                    	var context = [{
	                			title:"性能测试",
	                    		markClass:"object-test",
	                    		iconFont:"&#xe603;"
                			},{
	                			title:"编辑",
	                    		markClass:"object-edit",
	                    		iconFont:"&#xe6df;"
                			},{
	                			title:"删除",
	                    		markClass:"object-del",
	                    		iconFont:"&#xe6e2;"
                			}];                     	                   	                 		                       	                        	
                    	return btnIconTemplate(context);
           }
}];
var currentTestObject;
var eventList = {		
		"#add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加性能测试配置", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();			
		},
		"#batch-del":function(){
			batchOp($(".selectPt:checked"), top.PERFORMANCE_TEST_CONFIG_DEL_URL, "删除", null, "ptId");
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.ptId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑配置信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此配置信息吗？", top.PERFORMANCE_TEST_CONFIG_DEL_URL, {id:data.ptId}, this, "删除成功!");
		},
		"#choose-message-scene":function(){ //选择测试场景
			layer_show("选择测试接口场景", "chooseMessageScene.html?callbackFun=chooseScene", null, null, 2);
		},
		"#choose-business-system":function(){//选择业务系统
			if (!strIsNotEmpty($("#protocolType").val())) {
				layer.msg("请先选择接口场景!", {icon:5, time:1800});
				return false;
			}
			$.post(top.BUSINESS_SYSTEM_LIST_ALL_URL, {protocolType:$("#protocolType").val()}, function (json) {
				if (json.returnCode == 0) {
					if (json.data.length < 1) {
						layer.msg('无测试环境可供选择，请查看测试场景详细信息!', {icon:0, time:1800});
						return false;
					}
					layerMultipleChoose({
						title:"请选择测试环境(最多选择一个)",
						customData:{//自定义数据，Array数组对象
							enable:true,
							data:json.data,
							textItemName:["systemName", "protocolType"],
							valueItemName:"systemId"
						},
						choosedValues:$("#businessSystem\\.systemId").val().split(","),//已被选择的数据合集		
						closeLayer:true,//是否在确认之后自动关闭窗口
						maxChooseCount:1,
						minChooseCount:1,
						confirmCallback:function (chooseValues, chooseObjects, index) {
							$("#choose-business-system").siblings('span').remove();
							$.each(chooseObjects, function (i, n) {								
								$("#choose-business-system").before("<span>" + n.systemName + "&nbsp;&nbsp;</span>");
							});							
							$("#businessSystem\\.systemId").val(chooseValues[0]);
						} //选择之后的回调						
				});				
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});	
		},
		".object-test":function(){//开始性能测试
			var data = table.row( $(this).parents('tr') ).data();
			
			layer.confirm('确认新建一个性能测试任务吗？(你可以在 任务列表 中查看当前的所有性能测试任务。)', {title:'提示'}, function(index) {
				loading(true, '正在初始化测试...');
				$.post(top.PERFORMANCE_TEST_TASK_INIT_URL, {ptId:data.ptId}, function(json){
					if (json.returnCode == 0) {
						layer.confirm('初始化完成,是否需要打开测试视图页面？', {title:'提示'}, function(index){
							$.post(top.PERFORMANCE_TEST_TASK_VIEW_URL, {objectId:json.object.objectId}, function(text){
								loading(false);
								if (text.returnCode == 0) {	
									layer_show("性能测试视图", templates["performance-test-task-view"](text.object), null, null, 1, function(layero, index){
										createTestView(layero, text.object);
									}, null, function(){
										clearInterval(window.intervalId);
									}, {maxmin: true});
								} else {
									layer.alert(json.msg, {icon:5});
								}						
							});	
							layer.close(index);
						});			
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});	
				
				layer.close(index);
			});			
		},
		"#test-task":function(){//查看测试任务列表
			layer_show("我的性能测试任务", "performanceTestTaskList.html", null, 700, 2);
		},
		".show-performance-result":function() { //查看测试结果列表
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.ptName + "-测试结果", "performanceTestResult.html?ptId=" + data.ptId, null, null, 2);
		},
		"#test-setting":function(){//测试配置编辑
			var data = {
					parameterizedFilePath:$("#parameterizedFilePath").val(),
					parameterReuse:$("#parameterReuse").val(),
					parameterPickType:$("#parameterPickType").val(),
					keepAlive:$("#keepAlive").val(),
					maxCount:$("#maxCount").val(),
					maxTime:$("#maxTime").val(),	
					formatCharacter:$("#formatCharacter").val()
				};
			
			layer_show("测试配置", templates['performance-test-config-setting'](data), 750, 435, 1, function(layero, index){
				$(layero).find('#layerIndex').val(index);
				$(layero).find("#parameterReuse").val(data.parameterReuse);
				$(layero).find("#parameterPickType").val(data.parameterPickType);
				$(layero).find("#keepAlive").val(data.keepAlive);
				uploadFile({
					elem:$(layero).find("#parameterizedFilePath")[0],
					exts:'txt',
					acceptMime:'txt',
					done:function(path, relativePath){
						$(layero).find('#parameterizedFilePath').val(relativePath);
					}
				});
			}, null, null, {shadeClose:false});			
		},		
		"#save-performance-test-setting":function() {//保存配置
			var $form = $(this).parents('.form-horizontal');
			//判断是否需要重新上传参数化文件			
			$form.find("input[id],select").each(function(i, n){
				$("#" + $(n).attr("id") + ":hidden").val($(n).val());
			});						
			layer.close($form.find("#layerIndex").val());			
		},
		"#download-parameterized-file":function(){//下载参数化文件
			var path = $(this).siblings('input[type="text"]').val();
			if (strIsNotEmpty(path)) {
				window.open("../../" + top.DOWNLOAD_FILE_URL + "?downloadFileName=" + path.replace(/\\/g, "/"));
			}
		},
		"#remove-parameterized-file":function(){
			$(this).siblings('input[type="text"]').val('');
		},
		//测试视图界面-查看测试信息
		".show-pt-test-msg":function(){
			var type = $(this).attr("type");
			if (currentTestObject[type] == null) {
				return false;
			}
			createViewWindow(currentTestObject[type].reverse().join("\n"), {
				title:"测试信息查看", //标题
				copyBtn:true//是否显示复制按钮
			});
		},
		//测试视图界面-执行测试
		"#start-pt-test":function(){
			if (currentTestObject.running) {
				layer.msg('测试任务正在运行...', {time:1600});
				return;
			}
			$.post(top.PERFORMANCE_TEST_TASK_ACTION_URL, {objectId:currentTestObject.objectId}, function(json){
				if (json.returnCode == 0) {
					layer.msg('启动成功!', {icon:1, time:1500});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			})
			
		},
		//测试视图界面-停止测试
		"#stop-pt-test":function(){
			layer.confirm('确认停止该测试任务吗？', {title:'警告'}, function(index){
				$.post(top.PERFORMANCE_TEST_TASK_STOP_URL, {objectId:currentTestObject.objectId}, function(json) {
					if (json.returnCode == 0) {
						layer.msg('停止成功!', {icon:1, time:1600});
					} else {
						layer.alert(json.msg, {icon:5, title:'提示'});
					}
				});				
				layer.close(index);
			});	
		},
		//测试视图界面-删除测试
		"#del-pt-test":function(){
			layer.confirm('确认删除该测试任务吗,<span style="color:red;"><strong>删除的测试任务将不会保存所有的测试结果</strong></span>？', {title:'警告'}, function(index){
				$.post(top.PERFORMANCE_TEST_TASK_DEL_URL, {objectId:currentTestObject.objectId}, function(json) {
					if (json.returnCode == 0) {
						layer.msg('删除成功！', {icon:1, time:1800});
					} else {
						layer.alert(json.msg, {icon:5, title:'提示'});
					}
				});			
				layer.close(index);
			});
		},
		//测试视图界面-视图配置
		"#anaylze-view-setting":function(){
			layer_show("性能测试-视图设置", templates["performance-test-view-setting"]({}), 524, 184, 1, function(layero, index){
				//初始化laydate
				laydate.render({
				    elem: "#rangeTime" // 指定元素
				    ,type:'datetime'	
				    ,value: currentTestObject.time[0] + " ~ " + currentTestObject.time[currentTestObject.time.length - 1]
				    ,range: '~'	
				    ,min: currentTestObject.time[0]
				    ,max: currentTestObject.time[currentTestObject.time.length - 1]	
				  });	
				$("#layerIndex").val(index);
			});
		},
		"#update-pt-result-view":function() {
			//重新获取视图数据
			loading('正在重新处理数据...', true);
			$.post(top.PERFORMANCE_TEST_RESULT_ANAYLZE_URL, {ptResultId:$("#ptResultId").val(), rangeTime:$("#rangeTime").val()}, function(json) {
				loading(false);
				if (json.returnCode == 0) {
					currentTestObject = json.object;
					updateViewDataInterval(window.layero);
					layer.close($("#layerIndex").val());
					layer.msg('视图刷新成功!', {icon:1, time:1600}); 
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});	
		},
		//全部测试结果展示
		"#list-result":function(){
			layer_show("性能测试-结果列表", "performanceTestResult.html", null, null, 2);
		}
};


var mySetting = {
		eventList:eventList,
		listPage:{
			listUrl:top.PERFORMANCE_TEST_CONFIG_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 10]			
		},
		editPage:{
			renderCallback:function (obj) {
				$("#choose-message-scene").hide();
				$("#protocolType").val(obj.messageScene.protocolType);
				$("#choose-business-system").before("<span>" + obj.businessSystem.systemName + "&nbsp;&nbsp;</span>");
				$("#choose-message-scene").before("<span>" + obj.messageScene.interfaceName + "-" 
					+ obj.messageScene.messageName + "-" + obj.messageScene.sceneName + "&nbsp;&nbsp;</span>");
			},
			
			editUrl:top.PERFORMANCE_TEST_CONFIG_EDIT_URL,
			getUrl:top.PERFORMANCE_TEST_CONFIG_GET_URL,
			rules:{
				"businessSystem.systemId":{
					required:true
				},
				"messageScene.messageSceneId":{
					required:true
				},
				ptName:{
					required:true
				},
				threadCount:{
					digits:true,
					min:1,
					max:100
				}
			}
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

var echartsObjs;
var echartsViewMark = {
	tps:{
		title:"TPS",
		sign:"",
	},
	responseTime:{
		title:"响应时间",
		sign:"ms",
	},
	pressCpu:{
		title:"本机CPU消耗",
		sign:"%"
	},
	pressMemory:{
		title:"本机内存消耗",
		sign:"%"
	}
	
};
/**************************************************************************************************************************/
function createTestView(layero, object) {
	window.layero = layero;
	currentTestObject = object;
	if (currentTestObject == null) {
		layer.alert('获取数据失败,无法初始化图表!', {icon:5});
		return;
	}
	
	//每次新建都要初始化下该对象
	echartsObjs = {};
	//初始化echarts
	$(layero).find(".pt-task-echart-view").each(function(){
		var type = $(this).attr('type');
		var echartsObj = echarts.init($(this)[0], 'shine');
		echartsObj.setOption(createEchartsOption(type, currentTestObject.time, currentTestObject[type]));
		echartsObjs[type] = echartsObj;
	});
		
	//设定定时更新
	if (!object.finished) {
		window.intervalId = setInterval(function(){
			$.post(top.PERFORMANCE_TEST_TASK_VIEW_URL, {objectId:currentTestObject.objectId}, function(json){
				if (json.returnCode == 0) {
					if (!json.object.running) {
						return;
					}
					if (currentTestObject.time[currentTestObject.time.length - 1] === json.object.time[json.object.time.length - 1]) {
						return;
					}
					//更新页面
					currentTestObject = json.object;
					updateViewDataInterval(layero);			
				} else {
					layer.msg('获取数据失败:' + json.msg, {time:1500});
				}
			});
		}, 6000);
	}
	
	return window.intervalId;
}

function updateViewDataInterval(layero) {
	$(layero).find('.col-sm-2:eq(2) .label-success').text(currentTestObject.currentStatus);
	$(layero).find('.col-sm-2:eq(2) .label-primary').text(currentTestObject.startTime);
	$(layero).find('.col-sm-2:eq(2) span[type]').each(function(){
		var type = $(this).attr('type');
		$(this).children('.badge').text(currentTestObject[type].length);
	});
	$(layero).find('tbody td').each(function(){
		var type = $(this).attr('type');		
		$(this).text(currentTestObject[type]);
	});
	
	//更新图表
	$.each(echartsObjs, function(type, object){
		object.setOption(createEchartsOption(type, currentTestObject.time, currentTestObject[type]));
	});
}


function createEchartsOption(type, times, dataArray) {
	var mark = echartsViewMark[type];
	var option = {
			title: {
		        text: mark.title,
		        x:'center'
		    },
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'line'
		        }
		    },
		    xAxis:  {
		        type: 'category',
		        boundaryGap: false,
		        data: times
		    },
		    yAxis: {
		        type: 'value',
		        axisPointer: {
		            snap: true
		        }
		    },
		    series: [
		        {
		            name:mark.title + (mark.sign == '' ? '' : '[' + mark.sign + ']'),
		            type:'line',
		            smooth: true,
		            data: dataArray
		        }
		    ]	
	};
	
	return option;
}

/**
 * 选择测试场景之后的回调
 * @param obj
 * @returns {Boolean}
 */
function chooseScene (obj) {
	if (obj == null) {
		return false;
	}	
		
	if (Object.prototype.toString.call(obj) === '[object Array]' ) {//可能是多选的数组
		layer.alert('暂时不能使用多选功能,请选择单个场景!', {title:"提示", icon:5});
		return false;
	} 
	
	var protocolType = obj.protocolType;

	if (protocolType != $("#protocolType").val()) {
		$("#protocolType").val(protocolType);
		$("#choose-business-system").siblings('span').remove();
		$("#businessSystem\\.systemId").val('');		
	}
	
	$("#choose-message-scene").siblings('span').remove();
	$("#messageScene\\.messageSceneId").val(obj.messageSceneId);
	
	$("#choose-message-scene").before("<span>" + obj.interfaceName + "-" 
			+ obj.messageName + "-" + obj.sceneName + "&nbsp;&nbsp;</span>");
	
}