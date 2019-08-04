var messageId; //当前messageid
var messageSceneId; //当前正在操作的sceneId
var currIndex;//当前正在操作的layer窗口的index

var interfaceName;
var messageName;

var setId; //添加缺少数据时获取测试场景的测试集  0表示全量测试场景

var currentScene;//当前被操作的场景对象

var mode = "manage";//在测试场景从某些集合中添加或者删除时的标记， manage-管理界面  add-添加界面

var templateParams = {
		tableTheads:["接口", "报文", "场景名", "测试数据", "验证规则", "执行顺序", "出参示例", "创建时间", "请求路径","备注","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加场景"
		},{
			type:"danger",
			size:"M",
			id:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		},{
			type:"success",
			size:"M",
			id:"import-data-from-excel",
			iconFont:"&#xe642;",
			name:"Excel导入"
		},{
			type:"primary",
			size:"M",
			id:"manage-complex-scene",
			iconFont:"&#xe60c;",
			name:"管理场景"
		},{
			size:"M",
			id:"add-complex-scene",
			iconFont:"&#xe600;",
			name:"添加场景"
		},{
			type:"danger",
			size:"M",
			id:"setting-scene-order",
			iconFont:"&#xe6f3;",
			name:"执行排序"
		}],
		formControls:[
		{
			edit:true,
			label:"场景ID",  	
			objText:"messageSceneIdText",
			input:[{	
				hidden:true,
				name:"messageSceneId"
				}]
		},
		{
			required:true,
			label:"场景名称",  
			input:[{	
				name:"sceneName",
				placeholder:"输入场景名称"
				}]
		},
		{
			label:"默认关联验证",
			input:[{
				hidden:true,
				name:"variableId"
				}],
			button:[{
				style:"primary",
				value:"模板",
				name:"choose-validate-template"
			}]
		},
		{
			name:"message.messageId"
		},
		{
			label:"测试环境", 
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
			label:"请求路径",
			reminder:"<span class=\"parameter-reminder\">你可以单独给此场景设置请求路径,该路径的优先级大于接口和报文中设置的请求路径</span>",
			input:[{	
				name:"requestUrl"
				}]
		},
		{
			label:"返回示例",  
			textarea:[{	
				name:"responseExample",
				placeholder:"填入符合场景规则的返回示例报文"
				}]
		},
		{
			edit:true,			
			label:"创建日期",  
			objText:"createTimeText",
			input:[{	
				hidden:true,
				name:"createTime"
				}]
		},
		{
			label:"备注",  
			textarea:[{	
				name:"mark",
				placeholder:"输入场景备注或者备忘的查询数据用的SQL语句"
				}]
		}
		]		
	};

var columnsSetting = [
                      {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.sceneName, data.messageSceneId, "selectScene");
                          }},
                      {"data":"messageSceneId"},
                      ellipsisData("interfaceName"),
                      ellipsisData("messageName"),
                      ellipsisData("sceneName"),
                      {
                    	  "data":"testDataNum",
                          "render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"default",
                        			size:"M",
                        			markClass:"show-test-data",
                        			name:data
                        		}];
                              return btnTextTemplate(context);
                              }
            		    },  
            		    {
                      	  	"data":"rulesNum",
	                        "render":function(data, type, full, meta){
	                        	var context =
	                        		[{
	                      			type:"default",
	                      			size:"M",
	                      			markClass:"validate-method",
	                      			name:data
	                      		}];
	                            return btnTextTemplate(context);
	                            }
              		    },
              		   {"data":"sequenceNum"},
              		   {
              			 "data":"responseExample",
              			 "render":function (data) {
              				var context =
                        		[{
                      			type:"primary",
                      			size:"M",
                      			markClass:"get-responseExample",
                      			name:"获取"
                      		}];
                            return btnTextTemplate(context);
              			 }
              		   },
              		  ellipsisData("createTime"),
              		  ellipsisData("requestUrl"),
                      {
            		    "data":"mark",
            		    "className":"ellipsis",
            		    "render":function(data, type, full, meta) { 
            		    	if (data != "" && data != null) {
                		    	return '<a href="javascript:;" onclick="showMark(\'' + full.sceneName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
            		    	}
            		    	return "";
            		    }
                      },
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                        	  var context = [{
                              	title:"场景测试",
                  	    		markClass:"scene-test",
                  	    		iconFont:"&#xe603;"
                              }]; 
                        	  
                        	if (complexSceneId != null) {
                        		if (mode == "manage") {
                        			return btnIconTemplate(context.concat(
                                			[{
                                            	title:"配置管理",
                                	    		markClass:"set-complex-scene-setting",
                                	    		iconFont:"&#xe61d;"                           	
                                            }, {
                                            	title:"删除场景",
                                	    		markClass:"del-from-complex-scene",
                                	    		iconFont:"&#xe6e2;" 
                                            }]));
                        		}
                        		return btnIconTemplate(context.concat(
                            			[{
                                        	title:"添加到组合场景",
                            	    		markClass:"add-to-complex-scene",
                            	    		iconFont:"&#xe61f;"                           	
                                        }]));
                        	} 
                        	                        	                                                                                                         
                            if (setId == null) {
                            	return btnIconTemplate(context.concat(
                            			[{
                            	    		title:"场景编辑",
                            	    		markClass:"object-edit",
                            	    		iconFont:"&#xe6df;"
                            	    	},{
                            	    		title:"场景删除",
                            	    		markClass:"object-del",
                            	    		iconFont:"&#xe6e2;"
                            	    	}]));
                            }
                            
                          	return btnIconTemplate(context);
                          }}
                  ];
var currentVariablesSpan;
var eventList = {
		"#setting-scene-order":function () {//组合场景中排序执行顺序
			//重新获取最新的场景列表
			$.get(top.COMPLEX_SCENE_LIST_SCENES_URL + "?id=" + complexSceneId, function (json) {
				if (json.returnCode == 0) {
					if (json.data.length < 2) {
						layer.msg('测试场景太少,再添加几个吧!', {time:1500});
						return false;
					}
					var heignt = 140 + 40 * json.data.length;
					var sortableHtml = templates["sort-complex-scenes"](json.data);
					layer_show("场景执行排序-请手动拖拽", sortableHtml, 680, heignt, 1, function (layero, index) {
						$(layero).spinModal(); 
						var sortObj = new Sortable(document.getElementById('scenes-steps'), {
							animation: 150
						});
						var orderArr = [];
						for (var i = 1;i <= json.data.length;i++){
							orderArr.push(i + "");
						}
						sortObj.sort(orderArr);
						$("#save-complex-scenes-sort").click(function () {//保存提交到后台并关闭弹出层
							if (JSON.stringify(sortObj.toArray()) == JSON.stringify(orderArr)) {//没有改动
								layer.msg('未作改动!', {time:1500});
								layer.close(index);
								return false;
							}
							$.post(top.COMPLEX_SCENE_SORT_SCENES_URL, {id:complexSceneId, sequenceNums:sortObj.toArray().join(",")}, function (json) {
								if (json.returnCode == 0) {
									layer.msg("保存排序成功!", {icon:1, time:1800})
									refreshTable();
									layer.close(index);
								} else {
									layer.alert("操作失败：" + json.msg, {icon:5});
								}
							});				
						});
						$("#reset-complex-scenes-sort").click(function () {//重置
							sortObj.sort(orderArr);
						});
						$(layero).spinModal(false); 
					}, null, function () {
						$("#save-complex-scenes-sort").unbind('click');
						$("#reset-complex-scenes-sort").unbind('click');
					}, {
						skin: 'layui-layer-rim', //加上边框	
						shade: 0
					});
					
					
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
		},
		".set-complex-scene-setting":function () {//打开设定该组合场景中测试场景的配置
			var data = table.row( $(this).parents('tr') ).data();
			messageSceneId = data.messageSceneId;
			currentScene = data;
			layer_show("编辑配置信息", templates["scene-show-variables"](data.config), null, null, 1, function () {
				$("#errorExecFlag").val(data.config.errorExecFlag);
				//显示测试环境
				if (strIsNotEmpty($("#systemId").val())) {
					$.post(top.BUSINESS_SYSTEM_GET_URL, {id:$("#systemId").val()}, function(json) {
						if (json.returnCode == 0) {
							$("#complex-choose-business-system").siblings('span').remove();
							$("#complex-choose-business-system").before('<span>' + json.object.systemName + "[" + json.object.systemHost + ":" + json.object.systemPort + ']&nbsp;&nbsp;</span>');
						} else {
							console.log("获取测试环境信息出错:\n" + json.msg);
						}
					});
				}				
			}, function (layero, index) {
				var updateConfig = $.extend(true, {}, data.config);
				updateConfig["messageSceneId"] = messageSceneId;
				updateConfig["retryCount"] = Number($("#retryCount").val());
				updateConfig["intervalTime"] = Number($("#intervalTime").val());
				updateConfig["errorExecFlag"] = $("#errorExecFlag").val();
				updateConfig["systemId"] = $("#systemId").val();
				updateConfig["useVariables"] = {};
				updateConfig["saveVariables"] = {};
				$.each($("#save-variables").siblings('div').children(".edit-this-variables"), function(i, n) {
					var variables = $(n).text();
					updateConfig["saveVariables"][variables.substring(0, variables.indexOf("="))] = variables.substring(variables.indexOf("=") + 1);
				});
				$.each($("#use-variables").siblings('div').children(".edit-this-variables"), function(i, n) {
					var variables = $(n).text();
					updateConfig["useVariables"][variables.substring(0, variables.indexOf("="))] = variables.substring(variables.indexOf("=") + 1);
				});
				
				if (JSON.stringify(updateConfig) != JSON.stringify(data.config)) {
					$.post(top.COMPLEX_SCENE_UPDATE_SCENE_CONFIG_URL, {id:complexSceneId, sequenceNum:data.sequenceNum, config:JSON.stringify(updateConfig)}, function (json) {
						if (json.returnCode == 0) {
							layer.msg("已更新配置信息", {icon:1, time:1800});
							refreshTable();
						} else {
							layer.alert("保存配置信息出错,你的更改没有保存到服务器上:" + json.msg, {icon:5});	
						}						
					});
				}
			}, null, {shadeClose:false});
		},
		".del-from-complex-scene":function () {//从当前组合场景中删除该测试场景
			var data = table.row( $(this).parents('tr') ).data();
			opObj("确认从该组合场景中删除该报文场景吗？", 
					top.COMPLEX_SCENE_DEL_SCENE_URL, {id:complexSceneId, sequenceNum:data.sequenceNum}, this, "删除成功!", function(){
						refreshTable();
					});
		},
		".add-to-complex-scene":function () {//将该测试场景添加到组合场景中
			var data = table.row( $(this).parents('tr') ).data();
			opObj("确认添加该报文场景到组合场景中吗？<span class=\"c-red\">(可重复添加)</span>", 
					top.COMPLEX_SCENE_ADD_SCENE_URL, {id:complexSceneId, messageSceneId:data.messageSceneId}, null, "添加成功!");
		},
		"#add-object":function() {
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("增加场景", editHtml, editPageWidth, editPageHeight.add, 1);
			//layer.full(index);
			publish.init();			
		},
		"#batch-del-object":function() {//批量删除
			var checkboxList = $(".selectScene:checked");
			batchDelObjs(checkboxList, top.SCENE_DEL_URL);
		},
		".object-edit":function() {//场景信息编辑
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.messageSceneId;
			layer_show("编辑场景信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function() {//删除场景
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此场景？请慎重操作!", top.SCENE_DEL_URL, data.messageSceneId, this);
		},
		".scene-test":function() {//场景测试
			var data = table.row( $(this).parents('tr') ).data();
			messageSceneId = data.messageSceneId;
			layer_show(data.interfaceName + "-" + data.messageName + "-" + data.sceneName + "-测试", htmls["messageScene-test"], 1000, 520, 1, function() {
				renderSceneTestPage();				
			}, null, null);
			
		},
		".validate-method":function() {//场景验证规则管理
			var data = table.row( $(this).parents('tr') ).data();
			messageSceneId = data.messageSceneId;
			
			layer_show (data.sceneName + "-验证规则管理", 'validateParameters.html?messageSceneId=' + messageSceneId, null, null, 2, null, null, function() {
				refreshTable();
			});						
		},
		".show-test-data":function() { //展示测试数据
			var data = table.row( $(this).parents('tr') ).data();	
			var title = data.interfaceName + "-" + data.messageName + "-" + data.sceneName + " " + "测试数据";
			var url = "testData.html?messageSceneId=" + data.messageSceneId + "&sceneName=" + data.sceneName + "&protocolType=" + data.protocolType;
			
			layer_show (title, url, null, null, 2, null, null, function() {
				refreshTable();
			});
		},
		"#add-complex-scene":function () {//此时展示的指定组合场景没有包含的测试场景
			table.destroy();
			var that = this;
			publish.renderParams.listPage.dtOtherSetting.serverSide = true;
			publish.renderParams.listPage.listUrl = top.SCENE_LIST_URL;
			publish.renderParams.renderType = "list";
			mode = "add";
			publish.renderParams.customCallBack = function (p) {
				$(that).addClass('btn-primary').siblings("#manage-complex-scene").removeClass('btn-primary');
			};
			
			publish.init();
			
		},	
		"#manage-complex-scene":function () {//此时展示的指定组合场景包含的测试场景
			table.destroy();
			var that = this;
			publish.renderParams.listPage.dtOtherSetting.serverSide = false;
			publish.renderParams.listPage.listUrl = top.COMPLEX_SCENE_LIST_SCENES_URL + "?id=" + complexSceneId;
			publish.renderParams.renderType = "list";
			publish.renderParams.customCallBack = function (p) {
				$(that).addClass('btn-primary').siblings("#add-complex-scene").removeClass('btn-primary');
			};
			
			mode = "manage";
			publish.init();
			
		},		
		"#save-new-varibales":function() {//保存变量信息
			if (!strIsNotEmpty($("#scene-variables-value").val()) || !strIsNotEmpty($("#scene-variables-key").val())) {
				layer.msg('你还没有输入完整!', {icon:5, time:1500});
				return false;
			}
			
			if ($(this).attr("mode") == "edit") {
				currentVariablesSpan.text($("#scene-variables-key").val() + '=' +  $("#scene-variables-value").val());
			} else {
				$("label:contains('" + $(this).attr("parent-parameter-name") + "')").siblings('div')
					.append('<span class="label label-default radius edit-this-variables appoint">' + $("#scene-variables-key")
					.val() + '=' +  $("#scene-variables-value").val() 
					+ '</span><i class="Hui-iconfont del-this-variables appoint" style="margin-right:8px;">&#xe6a6;</i>');
			}
			
			layer.close($(this).attr("layer-index"));
			layer.msg('保存成功!', {icon:1, time:1500});
		},
		"#save-variables,#use-variables":function() {//新建组合参数变量
			editSceneConfigVariable(this, "add");
		},
		".edit-this-variables":function() {//编辑变量信息
			var returnObject = editSceneConfigVariable(this, "edit");			
			currentVariablesSpan = returnObject.currentVariablesSpan;
		},
		"#choose-scene-variables-value":function(){//从上下文变量中选择参数化的内容
			$.ajax({
				url:top.COMPLEX_SCENE_LIST_SAVE_VARIABLES_URL,
				type:"POST",
				data: {id:complexSceneId, sequenceNum:currentScene.sequenceNum},
				success:function (json) {
					if (json.returnCode == 0) {
						showSelectBox(json.data, "id", "id", function(id, object, index){
							$("#scene-variables-value").val(id);
							layer.close(index);
						});
					} else {
						layer.alert(json.msg, {icon:5});
					}
				}					
			});
		},		
		"#choose-scene-variables-key":function () {//选择指定的节点路径-出参或者入参信息
			var getUrl;
			var titleName;
			if ($("#save-new-varibales").attr("parent-parameter-name") == "替换变量") {
				//获取的是入参节点
				getUrl = top.SCENE_GET_REQUEST_MSG_JSON_TREE_URL;
				titleName = currentScene.interfaceName + "-" + currentScene.messageName + "-" + currentScene.sceneName + " 入参节点树";
			} else {
				//获取的是出参节点
				getUrl = top.SCENE_GET_RESPONSE_MSG_JSON_TREE_URL;
				titleName = currentScene.interfaceName + "-" + currentScene.messageName + "-" + currentScene.sceneName + " 出参节点树";
			}
			
			chooseParameterNodePath(getUrl, {messageSceneId:messageSceneId}, {
				titleName:titleName,
				isChoosePath:true, 
				notChooseTypes:["Array", "Map", "List", "Object"],
				choosenCallback:function (path) {
					$("#scene-variables-key").val(path);
				}
			});
		},
		"#choose-header-variables-key":function(){//选择指定的节点路径-请求头或者返回头信息
			var getUrl;
			var titleName;
			if ($("#save-new-varibales").attr("parent-parameter-name") == "替换变量") {
				//获取的是请求头
				getUrl = "../../js/json/HttpRequestHeader.json";
				titleName = "HTTP/HTTPS通用请求头-参数节点";
			} else {
				//获取的是返回头
				getUrl = "../../js/json/HttpResponseHeader.json";
				titleName = "HTTP/HTTPS通用响应头-参数节点";
			}
			
			chooseParameterNodePath(getUrl, {}, {
				titleName:titleName,
				isChoosePath:true, 
				notChooseTypes:["Array", "Map", "List", "Object"],
				choosenCallback:function (path) {
					$("#scene-variables-key").val(path);
				}
			});
		},
		"#choose-query-variables-key":function(){//填写Query查询参数节点路径
			$("#scene-variables-key").val("Querys.");
			layer.msg('请手动填写Query查询参数名称', {icon:0, time:1500});
		},
		".del-this-variables":function() {//删除变量信息
			$(this).prev('span').remove();
			$(this).remove();
		},
		'#choose-validate-template':function() { //创建测试场景时可以选择默认的关联模板
			$.post(top.GLOBAL_VARIABLE_LIST_URL, {variableType:"relatedKeyWord"}, function(json) {
				if (json.returnCode == 0) {
					showSelectBox(json.data, "variableId", "variableName", function(variableId, globalVariable, index) {
						$("#variableId").val(variableId);
						$("#choose-validate-template").siblings("span").remove();
						$("#choose-validate-template").before('<span>' + globalVariable.variableName + '&nbsp;</span>');	
						layer.msg('已确定选择!', {icon:1, time:1800});
						layer.close(index);
					});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});			
		},
		"#import-data-from-excel":function() {//Excel导入数据
			createImportExcelMark("Excel导入场景信息", "../../excel/upload_scene_template.xlsx"
					, top.UPLOAD_FILE_URL, top.SCENE_IMPORT_FROM_EXCEL + "?messageId=" + messageId);
		},
		".get-responseExample":function () {//获取返回示例报文
			var data = table.row( $(this).parents('tr') ).data();
			createViewWindow(data.responseExample, {
				title:data.sceneName + "-[出参示例]",
				copyBtn:true
			});	
		},
		"#complex-choose-business-system":function () {//选择测试环境-组合场景中给场景配置时
			$.post(top.SCENE_GET_URL, {id:messageSceneId}, function (json) {
			if (json.returnCode == 0) {
				layerMultipleChoose({
					title:"请选择对应所属的测试环境",
					customData:{//自定义数据，Array数组对象
						enable:true,
						data:json.object.businessSystems,
						textItemName:"systemName",
						valueItemName:"systemId"
					},
					choosedValues:$("#systemId").val().split(","),//已被选择的数据合集		
					closeLayer:true,//是否在确认之后自动关闭窗口
					maxChooseCount:1,//最多选择一个
					minChooseCount:1,//至少选择一个
					confirmCallback:function (chooseValues, chooseObjects, index) {
						$("#complex-choose-business-system").siblings('span').remove();
						$.each(chooseObjects, function (i, n) {								
							$("#complex-choose-business-system").before('<span>' + n.systemName + "[" + n.systemHost + ":" + n.systemPort + ']&nbsp;&nbsp;</span>');
						});							
						$("#systemId").val(chooseValues.join(','));
					} //选择之后的回调						
			});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});		
		},
		"#choose-business-system":function () {//选择测试环境-编辑新增测试场景
			$.post(top.MESSAGE_GET_URL, {id:messageId}, function (json) {
				if (json.returnCode == 0) {
					layerMultipleChoose({
						title:"请选择测试场景所属的测试环境(可多选,最多不超过10个)",
						customData:{//自定义数据，Array数组对象
							enable:true,
							data:json.object.businessSystems,
							textItemName:"systemName",
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
		customCallBack:function () {
			if (complexSceneId == null) {
				table.column(7).visible(false);
			} else {
				table.column(7).visible(true);
			}						
		},
		templateCallBack:function(df){					
			complexSceneId = GetQueryString("complexSceneId");
			//测试集中组合场景的编辑页面
			if (complexSceneId) {
		
				publish.renderParams.listPage.listUrl = top.COMPLEX_SCENE_LIST_SCENES_URL + "?id=" + complexSceneId;
				publish.renderParams.listPage.dtOtherSetting= {serverSide:false};
				publish.renderParams.listPage.exportExcel = false;
				
				$("#add-object").hide();
				$("#batch-del-object").hide();				
				$("#import-data-from-excel").hide();
				
				df.resolve();
				return false;
			}
			
			$("#add-complex-scene").hide();
			$("#manage-complex-scene").hide();
			$("#setting-scene-order").hide();
			
			//测试集中没有测试数据的场景展示
			if (GetQueryString("addDataFlag") == "0") {
				setId = GetQueryString("setId");
				publish.renderParams.listPage.listUrl = top.SCENE_LIST_NO_DATA_SCENES_URL + "?setId=" + setId;
				publish.renderParams.listPage.dtOtherSetting.serverSide = false;
				publish.renderParams.listPage.dtOtherSetting.aaSorting = [[ 7, "asc" ]];
				$("#btn-tools").parent("div").hide();
				
				df.resolve();
				return false;
			}
			//正常展示指定报文中的测试场景
			messageId = GetQueryString("messageId");
			interfaceName = GetQueryString("interfaceName");
			messageName = GetQueryString("messageName");				
			publish.renderParams.listPage.listUrl = top.SCENE_LIST_URL + "?messageId=" + messageId;
									
			df.resolve();			   		 	
   	 	},
		editPage:{
			editUrl:top.SCENE_EDIT_URL,
			getUrl:top.SCENE_GET_URL,
			rules:{
				sceneName:{
					required:true,
					minlength:2,
					maxlength:255
				},
				systems:{
					required:true
				}
			},
			renderCallback:function(obj){					
				appendSystem(obj.businessSystems);
			},
			beforeInit:function(df){
				$("#message\\.messageId").val(messageId);
				if (publish.renderParams.editPage.modeFlag == 1) {//编辑页面的时候
					$("#variableId").parents('.cl').remove();
				}	
				
				if (messageId != null && publish.renderParams.editPage.modeFlag == 0) {
					$.post(top.MESSAGE_GET_URL, {id:messageId}, function (json) {
						if (json.returnCode == 0) {
							appendSystem(json.object.businessSystems);
						}
					});
				}
				
       		 	df.resolve();
       	 	}

		},		
		listPage:{
			listUrl:top.SCENE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 8, 11, 12],
			dtOtherSetting:{
				stateSave: false
			}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/**********************************************************************************************************************/

/**
 * 场景测试页面渲染
 */
function renderSceneTestPage(flag) {
	var index = layer.msg('加载中,请稍后...', {icon:16, time:60000, shade:0.35});
	$.get(top.SCENE_GET_TEST_OBJECT_URL, {messageSceneId:messageSceneId}, function(data){					
		if(data.returnCode == 0){
			var $F = $("#message-scene-test-view");
			
			var $selectSystem = $F.find("#select-system");
			
			$.each(data.testObject, function(systemId, object) {
				$selectSystem.append("<option value='" + systemId + "'>" + object.system.systemName + "[" 
					+ object.system.systemHost + ":" + object.system.systemPort + "]" + "</option>");			
			});
			
			$selectSystem.change(function(){
				var systemId = $(this).val();
				var object = data.testObject[systemId];
				if (object != null) {
					$F.find("#request-url").text(object.requestUrl);
					$F.find("#select-data").html('');
					$.each(object.requestData, function(i, n){
						$F.find("#select-data").append("<option data-id='" + n.dataId + "' value='" + i + "'>" + n.dataDiscr + "</option>");			
					});
					$F.find("#select-data").change();
				}
			});
			
			$F.find("#select-data").change(function(){
				var systemId = $selectSystem.val();	
				var that = this;
				if (!strIsNotEmpty($(that).val())) {
					$F.find("#scene-test-request-message").val('');
					return false;
				}
				$F.find("#scene-test-request-message").val(data.testObject[systemId]["requestData"][$(that).val()]["dataJson"]);				
			});
			$selectSystem.change();
									
		} else {
			layer.alert(data.msg, {icon:5});
		}	
		layer.close(index);
	});
}

/**
 * 场景测试
 */
function sceneTest() {
	var $F = $("#message-scene-test-view");
	
	var requestUrl = $F.find("#request-url").text();
	var requestMessage = $F.find("#scene-test-request-message").val();
	
	if(!strIsNotEmpty(requestUrl) || !strIsNotEmpty(requestMessage)){
		layer.msg('请选择正确的接口地址和测试数据',{icon:2, time:1500});
		return;
	}
	
	var dataId = $F.find("#select-data > option:selected").attr("data-id");
	var systemId = $F.find("#select-system").val();
	var index = layer.msg('正在进行测试...', {icon:16, time:9999999, shade:0.35});

	
	$.post(top.TEST_SCENE_URL, {messageSceneId:messageSceneId, dataId:dataId, requestUrl:requestUrl, requestMessage:requestMessage, systemId:systemId},function(data) {
		if (data.returnCode == 0) {			
			layer.close(index);
			renderResultViewPage(data.result, messageSceneId);			
		}else{
			layer.close(index);
			layer.alert(data.msg, {icon:5});
		}
	});
}

/**
 * 打开编辑或者新增变量的页面
 * @param obj
 * @param editMode
 * @returns
 */
function editSceneConfigVariable (obj, editMode) {
	var returnContext = {};
	var context = {"key":"", "value":""};
	var title = "新增";
	var name = $(obj).text();
	if (editMode == "edit") {
		returnContext.currentVariablesSpan = $(obj);
		var keyValue = $(obj).text();
		context = {"key":keyValue.substring(0, keyValue.indexOf("=")), "value":keyValue.substring(keyValue.indexOf("=") + 1)};
		name = $(obj).parents('div').siblings('label').text();
		title = "修改";		
	}
		
	if (name == "替换变量") {		
		context.use = true;			
	} else {
		context.add = true;
	}
	
	layer_show(title + "-" + name, templates["scene-edit-variables"](context), 800, 260, 1, function(layero, index) {
		$("#save-new-varibales").attr("layer-index", index);
		$("#save-new-varibales").attr("mode", editMode);
		$("#save-new-varibales").attr("parent-parameter-name", name);
		if (editMode == "edit") {
			$("#scene-variables-value").val(context.value);
		}		
	});	
	
	return returnContext;
}