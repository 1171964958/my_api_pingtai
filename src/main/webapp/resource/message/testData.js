var messageSceneId; //当前正在操作的sceneId
var sceneName; //当前场景名称
var currIndex;//当前正在操作的layer窗口的index
var protocolType;

var currParams; //当前设置数据内容

var templateParams = {
		tableTheads:["标记", "状态", "默认数据","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加数据"
		},{
			type:"danger",
			size:"M",
			markClass:"batch-op-object",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		}],
		formControls:[
		{
			edit:true,
			label:"数据ID",  	
			objText:"dataIdText",
			input:[{	
				hidden:true,
				name:"dataId"
				}]
		},		
		{
			required:true,
			label:"数据标记",  
			input:[{	
				name:"dataDiscr",
				placeholder:"区别不同数据的标示,自定义"
				}]
		},
		{
			label:"可用于的测试环境", 
			required:true,
			input:[{	
				name:"systems",
				hidden:true
				}],
			button:[{
				 style:"danger",
				 value:"选择",
				 name:"choose-business-system"
			}]
		},
		{
			label:"数据内容",  
			button:[{
				style:"secondary",
				value:"查看设置",
				name:"view-params-data"
			}]
		},
		{	
			required:true,
			label:"可用状态",  			
			select:[{	
				name:"status",
				option:[{
					value:"0",
					text:"可用"
				},{
					value:"1",
					text:"已使用"
				},{
					value:"2",
					text:"可重复使用"
				}]
				}]
		},
		{	
			required:true,
			label:"默认数据",  			
			select:[{	
				name:"defaultData",
				option:[{
					value:"0",
					text:"是"
				},{
					value:"1",
					text:"否"
				}]
				}]
		},
		{
			name:"messageScene.messageSceneId"
		},
		{
			name:"paramsData"
		}
		]		
	};


var columnsSetting = [
                      {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.dataDiscr, data.dataId, "selectData");
                          }},
                      {"data":"dataId"},
                      {
                    	  "data":"dataDiscr",
                    	  "className":"appoint ellipsis",
                          "render":function(data, type, full, meta){  
                          		return '<span title="点击快速修改数据">' + data + '</span>';
                            }
            		    },
                      {
                    	  "data":"status",
                    	  "render":function(data) {
                    		  	var option = {
                    		  			"0":{
                    		  				btnStyle:"success",
                    		  				status:"可用"
                    		  				},
                		  				"1":{
                		  					btnStyle:"danger",
                		  					status:"已使用"
                		  					},
                		  				"2":{
                		  					btnStyle:"success",
                		  					status:"可重复使用"
                		  				}
                    		  	};	
                    		  	return labelCreate(data, option);							
                    	  }
                      }, 
                      {
                    	  "data":"defaultData",
                    	  "render":function(data) {
                    		  	var option = {
                    		  			"0":{
                    		  				btnStyle:"success",
                    		  				status:"是"
                    		  				},
                		  				"1":{
                		  					btnStyle:"default",
                		  					status:"否"
                		  					},
                		  				"default":{
                		  					btnStyle:"success",
                		  					status:"是"
                		  				}
                    		  	};	
                    		  	return labelCreate(data, option);							
                    	  }
                      },
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                            var context = [{
                	    		title:"数据编辑",
                	    		markClass:"object-edit",
                	    		iconFont:"&#xe6df;"
                	    	},{
                	    		title:"数据删除",
                	    		markClass:"object-del",
                	    		iconFont:"&#xe6e2;"
                	    	}];                           
                          	return btnIconTemplate(context);
                          }}
                  ];


var eventList = {
		".add-object":function() {
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("增加数据", editHtml, editPageWidth, editPageHeight.add, 1);
			//layer.full(index);
			publish.init();			
		},
		".batch-op-object":function() {//批量操作
			var checkboxList = $(".selectData:checked");
			layer.confirm(
					'请选择你需要进行的批量操作',
					{
						title:'批量操作',
						shadeClose:true,
						btn:['删除数据', '批量导入','修改状态'],
						btn3:function(index) {
							if (checkboxList.length < 1) {
								layer.close(index);
								return false;
							}
							showSelectBox([{status:"0", name:"可用"}, {status:"1", name:"不可用/已使用"},
							               {status:"2", name:"可重复使用"}], "status", "name",
							               function(status, obj, selectBoxIndex) {
												batchOp(checkboxList, top.DATA_CHANGE_STATUS_URL, '修改', table, 'dataId', {status:status})
												layer.close(selectBoxIndex);
								
							});
							layer.close(index);
						}
					},function(index){ 
						batchDelObjs(checkboxList, top.DATA_DEL_URL);
						layer.close(index);
						
					},function(index){	
						var fullIndex = parent.layer.open({
							title:sceneName + "-批量导入数据",
							type:2,	
							content:"testData-batchImportData.html?messageSceneId=" + messageSceneId,
							end:function() {
								refreshTable();
							}
						});
						parent.layer.full(fullIndex);
						layer.close(index);
					});							
		},
		".object-edit":function() {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.dataId;
			layer_show("编辑数据信息", editHtml, editPageWidth, editPageHeight.edit,1);
			publish.init();	
		},
		".object-del":function() {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此测试数据？请慎重操作!", top.DATA_DEL_URL, data.dataId, this);
		},
		".appoint":function() { //单独展示数据编辑页面
			var data = table.row( $(this).parents('tr') ).data();
			
			$.post(top.GET_SETTING_DATA_URL, {messageSceneId:messageSceneId, dataId:data.dataId} ,function(json) {
				currParams = json.params;
				if (json.returnCode == 0) {					
					layer_show("设置数据", templates["setting-parameter-data"](json), null, null, 1, null, function(index, layero) {
						$.post(top.DATA_UPDATE_PARAMS_DATA_URL, {dataId:data.dataId, paramsData:getParamsData()}, function (returnJson) {
							if (returnJson.returnCode == 0) {
								layer.msg("更新数据内容成功!", {icon:1, time:1500});	
								layer.close(index);
							} else {
								layer.alert("更新失败：" + returnJson.msg, {icon:5});
							}								
						});	
						
						return false;
					}, null, {shadeClose:false});
				} else {
					layer.alert(json.msg, {icon:5});
				}				
			});			
		},
		"#view-params-data":function() {  //查看或者设置数据内容
			//publish.renderParams.editPage.modeFlag 0 1
			$.post(top.GET_SETTING_DATA_URL, {messageSceneId:messageSceneId, dataId:$("#dataId").val()} ,function(json) {
				if (json.returnCode == 0) {
					currParams = json.params;
					layer_show("设置数据", templates["setting-parameter-data"](json), null, null, 1, null, function(index, layero) {
						$("#paramsData").val(getParamsData());
						layer.close(index);
					}, null, {shadeClose:false});
				} else {
					layer.alert(json.msg, {icon:5});
				}				
			});			
		},
		"#choose-business-system":function () {//选择测试环境,测试数据的测试环境可以不用与场景中的测试环境一致
			$.post(top.BUSINESS_SYSTEM_LIST_ALL_URL, {protocolType:protocolType}, function (json) {
				if (json.returnCode == 0) {
					layerMultipleChoose({
						title:"请选择测试数据所属的测试环境(可多选,最多不超过10个)",
						customData:{//自定义数据，Array数组对象
							enable:true,
							data:json.data,
							textItemName:["systemName", "protocolType"],
							valueItemName:"systemId"
						},
						choosedValues:$("#systems").val().split(","),//已被选择的数据合集		
						closeLayer:true,//是否在确认之后自动关闭窗口
						maxChooseCount:10,
						confirmCallback:function (chooseValues, chooseObjects, index) {
							$("#choose-business-system").siblings('p').remove();
							$.each(chooseObjects, function (i, n) {								
								$("#choose-business-system").before('<p>' + n.systemName + "[" + n.systemHost + ":" + n.systemPort + ']</p>');
							});							
							$("#systems").val(chooseValues.join(','));
						} //选择之后的回调						
				});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
		}
};


var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			messageSceneId = GetQueryString("messageSceneId");
			sceneName = GetQueryString("sceneName");
			protocolType = GetQueryString("protocolType");
			publish.renderParams.listPage.listUrl = top.DATA_LIST_URL + "?messageSceneId=" + messageSceneId;		
			df.resolve();			   		 	
   	 	},
		editPage:{
			editUrl:top.DATA_EDIT_URL,
			getUrl:top.DATA_GET_URL,
			rules:{
				dataDiscr:{
					required:true,
					minlength:1,
					maxlength:100,
					remote:{
						url:top.DATA_CHECK_NAME_URL,
						type:"post",
						dataType: "json",
						data: {                   
					        dataDiscr: function() {
					            return $("#dataDiscr").val();
					        },
					        dataId:function(){
					        	return $("#dataId").val();
					        },
					        messageSceneId:function() {
					        	return messageSceneId;
					        }					        
					}}
				},
				systems:{
					required:true
		        }
			},
			beforeInit:function(df){
				$("#messageScene\\.messageSceneId").val(messageSceneId);
				
				if (messageSceneId != null && publish.renderParams.editPage.modeFlag == 0) {
					$.post(top.SCENE_GET_URL, {id:messageSceneId}, function (json) {
						if (json.returnCode == 0) {
							appendSystem(json.object.businessSystems);
						}
					});
				}
				
       		 	df.resolve();
       	 	},
       	 	renderCallback:function(obj){					
				appendSystem(obj.businessSystems);
			}

		},		
		listPage:{
			listUrl:top.DATA_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 5]
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/******************************************************************************************************/
/**
 * 获取改动的参数列表
 */
function getParamsData() {
	var paramInputs = $("#select-parameters .setting-parameter-input");
	var paramsData={};
	$.each(paramInputs, function(i, n) {
		for (var i = 0;i < currParams.length;i++) {
			if (($(n).attr("name") == (currParams[i].path))
					&& ($(n).val() == currParams[i].defaultValue)) {
				return true;
			}
		}
		if (paramsData["TopRoot." + $(n).attr("name")] != null) {
			var indexValue = paramsData["TopRoot." + $(n).attr("name")];
			
			if (typeof indexValue == 'object') {
				indexValue.push($(n).val());
			} else {
				paramsData["TopRoot." + $(n).attr("name")] = [];
				paramsData["TopRoot." + $(n).attr("name")].push(indexValue);
				paramsData["TopRoot." + $(n).attr("name")].push($(n).val());
			}
			
		} else {
			paramsData["TopRoot." + $(n).attr("name")] = $(n).val();
		}
	});
	
	if ($.isEmptyObject(paramsData)) {
		return "";
	}
	
	return JSON.stringify(paramsData);
}

/**
 * 根据设置的数据重新生成带数据的报文
 */
function createDataMessage() {
	var paramsDataStr;
	if ((paramsDataStr =getParamsData()) == "") paramsDataStr = "{}";
	$.post(top.CREATE_NEW_DATA_MSG_URL, {messageSceneId:messageSceneId, paramsData:paramsDataStr}, function(json) {
		if (json.returnCode == 0) {
			layer.msg("生成报文成功!", {icon:1,time:1500});
			$("#dataMsg > textarea").val(json.dataMsg);
			currParams = JSON.parse(paramsDataStr);
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});		
}