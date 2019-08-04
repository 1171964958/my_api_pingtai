var echartsObj;

var validateConfig = {//配置选项验证规则
		"config.intervalTime":{
			required:true,
			digits:true,
			range:[60, 86400]
		},
		"config.maxCallTime":{
			number:true,
			min:-1
		},
		"config.maxDurationTime":{
			number:true,
			min:-1
		},
		"config.maxResponseTime":{
			digits:true,
			min:1
		},
		"config.analysisCycleTime":{
			digits:true,
			min:1
		}
};

var templateParams = {
		tableTheads:["探测接口", "测试环境","探测类型", "创建时间", "创建用户", "探测次数", "开启时间", "最后一次探测", "状态", "最近结果","备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加探测任务"
		},{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		},{
			type:"primary",
			size:"M",
			id:"quality-count-view",
			iconFont:"&#xe61e;",
			name:"统计视图"
		}],
		formControls:[
		{
			edit:true,
			required:false,
			label:"&nbsp;&nbsp;ID",  	
			objText:"probeIdText",
			input:[{	
				hidden:true,
				name:"probeId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"接口场景",  			
			input:[{	
				name:"scene.messageSceneId",
				hidden:true
				}],
			button:[{
				 style:"success",
				 value:"选择",
				 name:"choose-probe-scene"
			}]
		},
		{
			edit:false,
			required:true,
			label:"探测类型",  			
			select:[{	
				name:"probeType",
				option:[{value:"1", text:"测试场景", selected:true}]
				}]		
		},
		{
			edit:false,
			required:true,
			label:"测试环境",  			
			input:[{	
				name:"system.systemId",
				hidden:true
				}],
			button:[{
				 style:"success",
				 value:"选择",
				 name:"choose-probe-system"
			}]
		},
		{
			edit:false,
			label:"探测配置",  			
			input:[{	
				name:"probeConfigJson",
				hidden:true
				}],
			button:[{
				 style:"success",
				 value:"设置",
				 name:"setting-probe-config"
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
			name:"status"
		},
		{
			name:"callCount"
		},
		{
			name:"firstCallTime"
		},
		{
			name:"lastCallTime"
		},
		{
			name:"protocolType"
		},
		{
			name:"sceneIds"
		}
		],
		advancedQuery:{
			enable:true, 
			formTitle:"探测任务-高级查询",
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
	                       {label:"探测接口",
	                       	input:true,
	                       	name:"interfaceName"},
	                       {label:"测试环境",
	                       	 select:true,
	                       	 name:"systemId",
	                       	 option:[]}
	                       ], [
	                        {label:"探测类型",
	                       	 select:true,
	                       	 name:"probeType",
	                       	 option:[{value:"1", text:"测试场景"}]}, 
	                       	{label:"当前状态",
	                       	 select:true,
	                       	 name:"status",
	                       	 option:[{value:"0", text:"已停止"}, {value:"1", text:"正在运行"}, {value:"2", text:"缺少数据"}, {value:"3", text:"执行出错"}]}
	                        ], [
	                        {label:"首次探测时间",
	                         input:true,
	                         datetime:true,
	                       	 name:"firstCallTimeText"},  
	                       	{label:"最近探测时间",
                       		 input:true,
	                         datetime:true,
	                       	 name:"lastCallTimeText"}
	                        ], [
	                        {label:"创建用户",
	                       	 input:true,
	                       	 name:"createUserName"},
	                       	{label:"创建时间",
                       		 input:true,
	                         datetime:true,
	                       	 name:"createTimeText"} 
	                        ]
					]
		}		
	};

var columnsSetting = [{"data":null,
	 	"render":function(data, type, full, meta){
		  		return checkboxHmtl(data.scene.interfaceName + "-" + data.scene.messageName + "-" + data.scene.sceneName, data.probeId, "selectProbel");
	          }},
	{"data":"probeId"},
	{
		"data":"scene",
		"className":"ellipsis probe-scene-name",
		"render":function (data) {
			return '<span title="' + data.interfaceName + ',' + data.messageName + ',' + data.sceneName + '">' + data.interfaceName + '</span>';
		}
	},
	{
		"data":"system",
		"className":"ellipsis",
		"render":function (data) {
			return '<span title="' + data.systemName + '">' + data.systemName + '</span>';
		}
	},
	{
		"data":"probeType",
		"render":function (data) {
			var option = {
      				"1":{
      					btnStyle:"success",
      					status:"测试场景"
      					},
      				"2":{
      					btnStyle:"warning",
      					status:"组合场景"
      				}
          		};                  
          	return labelCreate(data, option);
		}
	},
	ellipsisData("createTime"),
	{
		"data":"user",
		"className":"ellipsis",
		"render":function (data) {
			return '<span title="' + data.realName + '">' + data.realName + '</span>';
		}
	},
	{"data":"callCount"},
	ellipsisData("firstCallTime"),
	ellipsisData("lastCallTime"),
	{
		"data":"status",		
		"render":function (data) {
			var option = {
      				"0":{
      					btnStyle:"default",
      					status:"已停止"
      					},
      				"1":{
      					btnStyle:"success",
      					status:"正在运行"
      					},
      				"2":{
      					btnStyle:"warning",
      					status:"缺少数据"
      					},
      				"3":{
      					btnStyle:"danger",
      					status:"执行出错"
      					}
          		};                  
          	return labelCreate(data, option);
		}
	},
	{
	    "data":"lastResult.qualityLevel",
	    "width":"60px",
	    "className":"show-probe-results",
	    "render":function(data, type, full, meta ){
	    	var progressType = "";
	    	var title = "NormalLevel(正常的)"
	    	switch (data) {
			case 1:
				progressType = "progress-bar-success";
				title = "ExcellentLevel(优秀的)";
				break;
			case 3:
				progressType = "progress-bar-warning";
				title = "ProblematicLevel(有问题的)";
				break;
			case 2:
				break;
			case 0:
			case 4:
				progressType = "progress-bar-danger";
				title = "SeriousLevel(严重的)";
				break;
			default:
				return "";
			}
	    	return '<a href="javascript:;"><div class="progress radius" style="width:60px;"><div class="progress-bar ' 
	    		+ progressType + '"><span title="' + title 
	    		+ '" class="sr-only" style="width:100%;height:20px;"></span></div></div></a>';
	    }
	},
	{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta ){
	    	if (data != "" && data != null) {
  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.scene.interfaceName + '-探测任务\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
		    	}
		    return "";
	    }
	},	
	{
		"data":null,
	    "render":function(data, type, full, meta){
	    	if (data.status != "0") {
	    		return btnIconTemplate([{
	    			title:"停止探测",
            		markClass:"stop-probe-task",
            		iconFont:"&#xe631;"
	    		}]);
	    	}
	    	
	    	var context = [{
	    		title:"开启探测",
	    		markClass:"start-probe-task",
	    		iconFont:"&#xe601;"
	    		
	    	},	    		
	    	{
	    		title:"配置选项",
	    		markClass:"setting-probe-task",
	    		iconFont:"&#xe61d;"
	    	},	    	
	    	{
	    		title:"编辑",
	    		markClass:"object-edit",
	    		iconFont:"&#xe6df;"
	    	},{
	    		title:"删除",
	    		markClass:"object-del",
	    		iconFont:"&#xe6e2;"
	    	}];	    		
	    	return btnIconTemplate(context);	    	
	    }}];	

var eventList = {
		'#setting-probe-config':function(){//设置探测配置
			var config;
			if (publish.renderParams.editPage.modeFlag == 0) {
				config = {};
			} else {
				config = publish.renderParams.editPage.currentObj.config;
			}
			currIndex = layer_show("探测任务配置", templates["interface-probe-setting-config"](config), 860, 600, 1,
					function(layero, index) {
					//表单验证和提交
					formValidate(
							layero.find("#interface-probe-setting-config-form"),
							validateConfig, 
							{}, 
							null, 
							true, 
							null,
							null,
							function(formData){//替代默认的提交函数
								var str = JSON.stringify(formData).replace(/config\./g, "");
								$("#probeConfigJson").val(str.substring(1, str.length - 1));
								layer.close(currIndex);
							});
				
			}, null, null);
			
			
		},
		"#quality-count-view":function(){//打开全局视图
			$.post(top.PROBE_TASK_GET_PROBE_RESULT_SYSNOPSIS_VIEW_DATA_URL, {dateNum:7}, function(json){
				if (json.returnCode == 0) {
					layer_show('探测任务-全局视图', '<div class="page-container" id="probes-count-view" style="height:500px;"></div>', null, 600, 1, function(layero, index) {
						echartsObj = echarts.init(document.getElementById('probes-count-view'), 'shine');
						echartsObj.setOption(echartsViewOption(json.data));
						echartsObj.on('click', function(params){
							layer_show(params.data[2] + " 探测结果详情", "../message/result.html?probeId=" + params.data[4], null, null, 2)
						});
					});									
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
			
		},
		".show-probe-results":function () {
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.scene.interfaceName + "-" + data.system.systemName + " 探测结果详情", "../message/result.html?probeId=" + data.probeId, null, null, 2);
		},
		".probe-scene-name:gt(0)":{
			"mouseenter":function () {
				var data = table.row( $(this).parents('tr') ).data();
				var that = this;
				layer.tips(data.scene.messageName + "<br>" + data.scene.sceneName, that, {
					  tips: [1, '#0FA6D8'],
					  time:9999999
					});
			},
			"mouseleave":function () {
				layer.closeAll('tips');
			}
		},
		".start-probe-task":function() {//开启探测任务
			var data = table.row( $(this).parents('tr') ).data();
			opObj("确认开启该接口探测任务?", top.PROBE_TASK_START_URL, {probeId:data.probeId}, null, "已开启!", function(data){
				refreshTable();
			});
		},
		".stop-probe-task":function() {//停止探测任务
			var data = table.row( $(this).parents('tr') ).data();
			opObj("确认停止该接口探测任务?", top.PROBE_TASK_STOP_URL, {probeId:data.probeId}, null, "已停止!", function(data){
				refreshTable();
			});
		},
		".setting-probe-task":function () {//配置探测任务
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.scene.interfaceName + "-探测任务配置", templates["interface-probe-setting-config"](data.config), 860, 600, 1,
					function(layero, index) {
					$("#addHeaderFlag").val(data.config.addHeaderFlag);
					$("#notifyType").val(data.config.notifyType);
					$("#notifyLevel").val(data.config.notifyLevel);
					$("#probeId").val(data.probeId);
					//表单验证和提交
					formValidate(
							layero.find("#interface-probe-setting-config-form"),
							validateConfig, 
							{}, 
							top.PROBE_TASK_UPDATE_CONFIG, 
							true, 
							null, 
							null
							);
				
			}, null, null);
		},
		"#choose-probe-system":function () {//选择测试环境
			if (!strIsNotEmpty($("#protocolType").val())) {
				layer.msg("请先选择接口场景!", {icon:5, time:1800});
				return false;
			}						
			$.post(top.BUSINESS_SYSTEM_LIST_ALL_URL, {protocolType:$("#protocolType").val()}, function (json) {
				if (json.returnCode == 0) {
					if (json.data.length < 1) {
						layer.msg('无测试环境可供选择，查看测试场景详细信息!', {icon:0, time:1800});
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
						choosedValues:$("#system\\.systemId").val().split(","),//已被选择的数据合集		
						closeLayer:true,//是否在确认之后自动关闭窗口
						maxChooseCount:1,
						minChooseCount:1,
						confirmCallback:function (chooseValues, chooseObjects, index) {
							$("#choose-probe-system").siblings('span').remove();
							$.each(chooseObjects, function (i, n) {								
								$("#choose-probe-system").before("<span>" + n.systemName + "&nbsp;&nbsp;</span>");
							});							
							$("#system\\.systemId").val(chooseValues[0]);
						} //选择之后的回调						
				});				
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});	
		},
		"#choose-probe-scene":function () {
			layer_show("选择探测接口场景", "chooseMessageScene.html?callbackFun=chooseScene", null, null, 2);						
		},
		"#add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加探测任务", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();			
		},
		"#batch-op":function(){
			layer.confirm(
				'请选择你需要进行的批量操作:',
				{
					title:'批量操作',
					btn:['运行','停止','删除'],
					btn3:function(index){
						layer.close(index);
						batchOp($(".selectProbel:checked"), top.PROBE_TASK_DEL_URL, "删除", null, "probeId");
					}
				},function(index){ 
					layer.close(index);
					batchOp($(".selectProbel:checked"), top.PROBE_TASK_START_URL, "运行", null, "probeId");
				},function(index){
					layer.close(index);
					batchOp($(".selectProbel:checked"), top.PROBE_TASK_STOP_URL, "停止", null, "probeId");
				});
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.probeId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑探测任务信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此探测任务吗？<br><span class=\"c-red\">(删除会导致该任务的历史记录被清空!)</span>？", top.PROBE_TASK_DEL_URL, {probeId:data.probeId}, this, "删除成功!");
		}
		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			renderCallback:function (obj) {
				$("#choose-probe-scene").hide();
				$("#protocolType").val(obj.scene.protocolType);
				$("#choose-probe-system").before("<span>" + obj.system.systemName + "&nbsp;&nbsp;</span>");
				$("#choose-probe-scene").before("<span>" + obj.scene.interfaceName + "-" 
					+ obj.scene.messageName + "-" + obj.scene.sceneName + "&nbsp;&nbsp;</span>");
			},
			editUrl:top.PROBE_TASK_EDIT_URL,
			getUrl:top.PROBE_TASK_GET_URL,
			rules:{
				"scene.messageSceneId":{
					required:true
				},
				"system.systemId":{
					required:true
				}
			}
		},
		listPage:{
			listUrl:top.PROBE_TASK_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 11, 12, 13]
		},
		templateParams:templateParams		
	};

$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});
/********************************************************************************/
/**
 * 生成echarts配置
 */
function echartsViewOption(data) {
	var option = {
		    backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [{
		        offset: 0,
		        color: '#f7f8fa'
		    }, {
		        offset: 1,
		        color: '#F5FAFE'
		    }]),
		    title: {
		        text: '探测任务总览视图',
		        subtext: '最近七天内\n通过鼠标滑轮进行缩放'
		    },
		    tooltip: {
		    	trigger:'item',
		    	formatter:function(params){
		    		return params.data[3] + '-' + params.data[2];
		    	},
		        axisPointer: {
		            type: 'cross'
		        }
		    },
		    dataZoom: [{
		        type: 'inside'
		    }, {
		        type: 'slider'
		    }],
		    legend: {
		        right: 10,
		        data: []
		    },
		    xAxis: {
		    	type:"time",
		        splitLine: {
		            lineStyle: {
		                type: 'dashed'
		            }
		        },
		    },
		    yAxis: {
		    	type:"category",
		        splitLine: {
		            lineStyle: {
		                type: 'dashed'
		            }
		        },
		        scale: true
		    },
		    series: []
		};
	
	$.each(data, function(systemId, info) {
		option.legend.data.push(info[0][3]);
		option.series.push({
			name: info[0][3],
	        data: info,
	        type: 'scatter',
	        symbolSize: 6
		});
		
	});
	
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
		
	var objArr = [];
	if (Object.prototype.toString.call(obj) === '[object Array]' ) {//可能是多选的数组
		objArr = obj;
	} else {
		objArr.push(obj);
	}
	
	if (objArr.length == 0) {
		return false;
	}
	
	var protocolType = null;
	var flag = false;
	$.each(objArr, function(i, sceneObj){
		if (protocolType != null && protocolType != sceneObj.protocolType) {
			flag = true;
			return false;
		}
		protocolType = sceneObj.protocolType;		
	});
	if (flag) {//所选的场景的协议不是统一的
		layer.alert('<strong class="c-red">请选择协议相同的测试场景!</strong>', {title:'提示'});
		return false;
	}	
	
	if (protocolType != $("#protocolType").val()) {
		$("#protocolType").val(protocolType);
		$("#choose-probe-system").siblings('span').remove();
		$("#system\\.systemId").val('');		
	}
	
	$("#choose-probe-scene").siblings('span').remove();
	$("#scene\\.messageSceneId").val('');
	$("#sceneIds").val('');
	
	var ids = [];
	$.each(objArr, function(i, sceneObj){
		ids.push(sceneObj.messageSceneId);
		$("#choose-probe-scene").before("<span>" + sceneObj.interfaceName + "-" 
				+ sceneObj.messageName + "-" + sceneObj.sceneName + "&nbsp;&nbsp;</span>");
		if (i != objArr.length - 1) {
			$("#choose-probe-scene").siblings('span').eq(i).append('<br>');
		}		
	});
	$("#scene\\.messageSceneId").val(ids[0]);
	if (ids.length > 1) {
		$("#sceneIds").val(ids.join(','));
		publish.renderParams.editPage.editUrl = top.PROBE_TASK_BATCH_ADD_URL;
	} else {
		publish.renderParams.editPage.editUrl = top.PROBE_TASK_EDIT_URL;
	}
}