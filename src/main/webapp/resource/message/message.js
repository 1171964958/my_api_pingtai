var interfaceId; //当前interfaceId
var messageId; //当前正在操作的messageid
var currIndex;//当前正在操作的layer窗口的index
var protocolJson;//协议配置参数json
var protocolType;//当前配置的协议类型
var processJson;//报文处理类型配置json


var templateParams = {
		tableTheads:["接口","报文名", "类型", "创建时间", "状态", "创建用户", "最后修改", "入参报文", "场景", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加报文"
		},{
			type:"danger",
			size:"M",
			markClass:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		},{
			type:"success",
			size:"M",
			id:"import-data-from-excel",
			iconFont:"&#xe642;",
			name:"Excel导入"
		}],
		formControls:[
		{
			edit:true,
			label:"报文ID",  	
			objText:"messageIdText",
			input:[{	
				hidden:true,
				name:"messageId"
				}]
		},
		{
			required:true,
			label:"报文名称",  
			input:[{	
				name:"messageName"
				}]
		},
		{	
			required:true,
			label:"报文类型",  			
			select:[{	
				name:"messageType",
				option:[{
					value:"JSON",
					text:"JSON"
				},{
					value:"XML",
					text:"XML"
				},{
					value:"URL",
					text:"URL"
				},{
					value:"FIXED",
					text:"固定报文"
				},{
					value:"OPT",
					text:"自定义格式"
				}]
				}]
		},
		{
			name:"interfaceInfo.interfaceId"
		},
		{
			label:"调用参数",
			input:[{
				hidden:true,
				name:"callParameter"
				}],
			button:[{
				style:"primary",
				value:"配置",
				name:"setting-call-parameter",
				embellish:"&nbsp;&nbsp;"
			},{
				style:"secondary",
				value:"模板",
				name:"template-call-parameter"
			}]
		},
		{
			label:"处理类型",
			select:[{	
				name:"processType",
				option:[{
					value:"",
					text:"请选择一个处理类型"
				}]
				}],
			input:[{
					hidden:true,
					name:"processParameter"	
				}],
			button:[{
				style:"secondary",
				value:"参数",
				name:"setting-process-parameter"
			}]
		},
		{
			required:true,
			label:"报文入参",  
			input:[{
				hidden:true,
				name:"parameterJson"
				}],
			button:[{
				style:"danger",
				value:"选择",
				markClass:"choose-parameter-nodes"
			},{
				style:"danger",
				value:"验证",
				markClass:"validate-parameter-json"
			},{
				style:"info",
				value:"格式化",
				embellish:"<br><br>",
				markClass:"format-parameter-json"
			}],
			textarea:[{
				placeholder:"输入报文入参"
			}]
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
			reminder:"<span class=\"parameter-reminder\">你可以单独给此报文设置请求路径,该路径的优先级大于接口中设置的请求路径</span>",
			input:[{	
				name:"requestUrl"
				}]
		},
		{	
			required:true,
			label:"报文状态",  			
			select:[{	
				name:"status",
				option:[{
					value:"0",
					text:"正常"
				},{
					value:"1",
					text:"禁用"
				}]
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
			edit:true,			
			label:"创建用户",  
			objText:"user.realNameText"
		},
		{
			edit:true,
			name:"user.userId"
							
		},
		{
			edit:true,			
			label:"最后修改",  
			objText:"lastModifyUserText",
			input:[{	
				hidden:true,
				name:"lastModifyUser"
				}]
		}
		]		
	};
//["报文名", "创建时间", "状态", "创建用户", "最后修改", "入参", "场景", "操作"],
var columnsSetting = [
                       {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.messageName,data.messageId,"selectMessage");
                          }},
                       {"data":"messageId"},
                       {
                         "className":"ellipsis",
               		     "data":"interfaceName",
                         "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
                       },                      
                       {
                      	"className":"ellipsis",
            		    "data":"messageName",
                      	"render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
                      	},
                      	{
                     	   "data":"messageType",
                     	   "render":function(data) {
                     		   return labelCreate(data.toUpperCase());
                     	   }
                         
                        },
                       {
                    	"className":"ellipsis",
              		    "data":"createTime",
              		    "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS  
              		    },
                       {
                        	"data":"status",
                        	"render":function(data, type, full, meta ){
                                return labelCreate(data);
                                }
              		    },
              		    {"data":"user.realName"},{"data":"lastModifyUser"},
              		    {
                            "data":null,
                            "render":function(data, type, full, meta){
                            	var context =
                            		[{
                          			type:"primary",
                          			size:"M",
                          			markClass:"get-params",
                          			name:"获取"
                          		}];
                                return btnTextTemplate(context);
                              }
              		    },
              		    {
              		    	"data":"sceneNum",
                            "render":function(data, type, full, meta){
                            	var context =
                            		[{
                          			type:"default",
                          			size:"M",
                          			markClass:"show-scenes",
                          			name:data
                          		}];
                                return btnTextTemplate(context);
                            }
              		    },
              		    {
                            "data":null,
                            "render":function(data, type, full, meta){
                              var context = [{
	                  	    		title:"报文编辑",
	                  	    		markClass:"object-edit",
	                  	    		iconFont:"&#xe6df;"
                  	    		},{
	                  	    		title:"报文删除",
	                  	    		markClass:"object-del",
	                  	    		iconFont:"&#xe6e2;"
                  	    		}];
                            	return btnIconTemplate(context);
                            }}
              		    ];
var currentCallParamterSpan;
var eventList = {
		//打开新增header页面
		'.add-child-call-parameter':function() {			
			var name = $(this).text();
			var context = {"key":"", "value":""};
			layer_show("添加参数-" + name, templates["message-add-call-parameter"](context), 350, 230, 1, function(layero, index) {
				$("#save-new-call-parameter").attr("layer-index", index);
				$("#save-new-call-parameter").attr("parent-parameter-name", name);
				$("#save-new-call-parameter").attr("mode", "add");
			});			
		},
		//编辑header
		'.edit-this-call-parameter':function() {
			currentCallParamterSpan = $(this);
			var name = $(this).parents('div').siblings('label').text();
			var keyValue = $(this).text();
			var context = {"key":keyValue.substring(0, keyValue.indexOf("=")), "value":keyValue.substring(keyValue.indexOf("=") + 1)};
			layer_show("修改参数-" + name, templates["message-add-call-parameter"](context), 350, 230, 1, function(layero, index) {
				$("#save-new-call-parameter").attr("layer-index", index);
				$("#save-new-call-parameter").attr("parent-parameter-name", name);
				$("#save-new-call-parameter").attr("mode", "edit");
			});			
		},
		//保存新的header
		'#save-new-call-parameter':function() {
			if ($(this).attr("mode") == "edit") {
				currentCallParamterSpan.text($("#call-parameter-name").val() + '=' +  $("#call-parameter-value").val());
			} else {
				$("label:contains('" + $(this).attr("parent-parameter-name") + "')").siblings('div')
					.append('<span class="label label-success radius edit-this-call-parameter appoint">' + $("#call-parameter-name")
					.val() + '=' +  $("#call-parameter-value").val() 
					+ '</span><i class="Hui-iconfont del-this-call-parameter appoint" style="margin-right:8px;">&#xe6a6;</i>');
			}
			
			layer.close($(this).attr("layer-index"));
			layer.msg('保存成功!', {icon:1, time:1500});
		},
		//删除header
		'.del-this-call-parameter':function() {
			$(this).prev('span').remove();
			$(this).remove();
		},
		//修改并保存调用参数
		'#change-call-parameter':function() {
			var form = $(this).parents('form');
			var callParameter = {};
			$.each(form.children('.parameter'), function(i, n) {
				var parentKey = $(this).children('label').text();
				
				var params =  $(this).children('div');
				
				if (params.children('input').length > 0) {
					callParameter[parentKey] = params.children('input').val();
					return true;
				}	
				callParameter[parentKey] = {};
				if (params.children('span')) {				
					$.each(params.children('span'), function(i1, n1) {
						var keyValue = $(this).text();
						callParameter[parentKey][keyValue.substring(0, keyValue.indexOf("="))] = keyValue.substring(keyValue.indexOf("=") + 1);
					});
				}								
			});
			$("#callParameter").val(JSON.stringify(callParameter));
			layer.close($(this).attr("layer-index"));
			layer.msg('保存成功!', {icon:1, time:1500});
		},
		'#template-call-parameter':function() {//选择配置模板
			var variableType = "";
			switch (protocolType) {
			case "HTTPS":
			case "HTTP":
				variableType = "httpCallParameter";
				break;
			case "Socket":
				variableType = "socketCallParameter";
				break;
			case "WebService":
				variableType = "webServiceCallParameter";
				break;
			default:
				break;
			}
			$.post(top.GLOBAL_VARIABLE_LIST_URL, {variableType:variableType}, function(json) {
				if (json.returnCode == 0) {
					showSelectBox(json.data, "variableId", "variableName", function(variableId, globalVariable, index) {
						$("#callParameter").val(globalVariable["value"]);
						layer.msg('已确定选择！', {icon:1, time:1800});
						layer.close(index);
					})
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
			
		},
		'#setting-call-parameter':function() {	//配置调用参数
			
			if (!strIsNotEmpty($("#callParameter").val())) {
				$("#callParameter").val(JSON.stringify(protocolJson[protocolType]));
			}
			
			var json = JSON.parse($("#callParameter").val());
			
			var callParameterViewHtml = '<article class="page-container"><form action="" method="" class="form form-horizontal">';
						
			if (json != null && !$.isEmptyObject(json)) {
				$.each(json, function(i, n) {
					callParameterViewHtml += '<div class="row cl parameter">'
						+ '<label class="form-label col-xs-4 col-sm-3';
					if (typeof n == 'object') {
						callParameterViewHtml += ' add-child-call-parameter" style="cursor: pointer;"';
					} else {
						callParameterViewHtml += '"';
					}
					callParameterViewHtml += '>' + i + '</label><div class="formControls col-xs-8 col-sm-9">';
					if (typeof n == 'object') {	
						if (!$.isEmptyObject(n)) {
							$.each(n, function(i1, n1) {
								callParameterViewHtml += '<span class="label label-success radius edit-this-call-parameter appoint">' + i1 + '=' + n1 + '</span><i class="Hui-iconfont del-this-call-parameter appoint" style="margin-right:8px;">&#xe6a6;</i>';							
							});
						}													
					} else {
						callParameterViewHtml += '<input type="text" class="input-text radius" value="' + n + '">';
					}
					callParameterViewHtml += '</div></div>';
				});
			} else {
				layer.alert("读取协议参数失败!", {icon:5});
				return false;
			}
			
			callParameterViewHtml += '<div class="row cl"><div class="col-xs-7 col-sm-8 col-xs-offset-4 col-sm-offset-3"><input class="btn btn-danger radius" type="button" value="&nbsp;&nbsp;保存更改&nbsp;&nbsp;" id="change-call-parameter"></div></div></form></article>';
			
			layer_show(protocolType + "调用参数设置", callParameterViewHtml, 780, 500, 1, function(layero, index) {
				$("#change-call-parameter").attr("layer-index", index);
			});
			
		},
		".get-params":function(){//获取入参报文
			var data = table.row( $(this).parents('tr') ).data();
			messageId = data.messageId
			createViewWindow(getParameterJson, {
				title:data.interfaceName + "-" + data.messageName + "-[入参报文]",
				copyBtn:true,
				refreshBtn:true
			});				
		},
		".show-scenes":function(){//显示场景信息
			var data = table.row( $(this).parents('tr') ).data();						
			$(this).attr("data-title", data.interfaceName + "-" + data.messageName + " " + "场景管理");
			$(this).attr("_href", "resource/message/messageScene.html?messageId=" + data.messageId
					+ "&interfaceName=" + data.interfaceName + "&messageName=" + data.messageName);
			Hui_admin_tab(this);
		},
		".add-object":function() {//添加报文
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("增加报文", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();			
		},
		".batch-del-object":function() {//批量删除报文
			var checkboxList = $(".selectMessage:checked");
			batchDelObjs(checkboxList, top.MESSAGE_DEL_URL);
		},
		".object-del":function() {//删除单个报文
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此报文？此操作同时会删除该报文下所有的场景及相关数据,请谨慎操作!", top.MESSAGE_DEL_URL, data.messageId, this);
		},
		".object-edit":function() {//报文信息编辑
			var data = table.row( $(this).parents('tr') ).data();
			messageId = data.messageId;
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = messageId;
			layer_show("编辑报文信息", editHtml, editPageWidth, 780, 1);
			publish.init();	
		},
		"#choose-business-system":function () {//选择测试环境
			$.post(top.INTERFACE_GET_URL, {id:interfaceId, messageId:$("#messageId").val()}, function (json) {
				if (json.returnCode == 0) {
					layerMultipleChoose({
						title:"请选择接口所属的测试环境(可多选,最多不超过10个)",
						customData:{//自定义数据，Array数组对象
							enable:true,
							data:json.object.systems,
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
		},
		".validate-parameter-json":function() {//验证入参报文
			var jsonStr = $(".textarea").val();
			var messageType = $("#messageType").val();
			if(jsonStr == null || jsonStr == "") {
				layer.msg('你还没有输入任何内容',{icon:5,time:1500});
				return false;
			}
			$.post(top.MESSAGE_VALIDATE_JSON_URL, {parameterJson:jsonStr,interfaceId:interfaceId, messageType:messageType, messageId:messageId},function(data){
				if(data.returnCode==0){
					layer.msg('验证通过,请执行下一步操作',{icon:1, time:1500});
					$("#parameterJson").val(jsonStr);
				}else{
					if (data.msg == null) {
						data.msg = "解析报文失败,请检查!";
					}
					layer.alert(data.msg,{icon:5});
				}
			});
		},
		//格式化报文根据报文格式
		".format-parameter-json":function(){
			var jsonStr = $(".textarea").val();
			var messageType = $("#messageType").val();
			if(jsonStr == null || jsonStr == "") {
				layer.msg('你还没有输入任何内容',{icon:5,time:1500});
				return false;
			}
			$.post(top.MESSAGE_FORMAT_URL, {parameterJson:jsonStr, messageType:messageType}, function(data) {
				if(data.returnCode == 0){
					$(".textarea").val(data.returnJson);
				} else if(data.returnCode == 912) {
					layer.msg("格式化失败：不是指定的格式!",{icon:5,time:1500});
				}else {
					layer.alert(data.msg,{icon:5});
				}
			});
		},
		//excel导入报文信息
		"#import-data-from-excel":function() {
			createImportExcelMark("Excel导入报文信息", "../../excel/upload_message_template.xlsx"
					, top.UPLOAD_FILE_URL, top.MESSAGE_IMPORT_FROM_EXCEL + "?interfaceId=" + interfaceId 
					+ "&protocolType=" + protocolType);
		},
		//设定处理类型参数
		"#setting-process-parameter":function(){
			var processType = $("#processType").val();
			if (!strIsNotEmpty(processType)) {
				layer.msg('请先选择一个处理类型!', {icon:5, time:1600});
				return false;
			}
			
			var processParameter = JSON.parse($("#processParameter").val() || processJson[processType]) ;			
			window.settingLayerIndex = layer_show("报文处理参数", templates["message-process-parameter-setting"](processParameter), 680, 400, 1, function(layero, index) {
				$("div ." + processType).removeClass('hide');
			});
		},
		//保存报文处理类型的参数
		"#save-process-parameter":function(){
			var processType = $("#processType").val();
			var value = $.extend({}, processJson[processType]);
			$.each(value, function(settingName, settingValue) {
				if ($("#" + settingName)) {
					value[settingName] = $("#" + settingName).val();
				}
			});
			$("#processParameter").val(JSON.stringify(value).replace(/\\\\r/g,"\\r"));		
			layer.close(window.settingLayerIndex);
			layer.msg('保存成功!', {icon:1, time:1600});
		},
		//切换报文处理类型时改变processParameter值
		"#processType":{
			"change":function(){
				var processType = $(this).val();
				$("#processParameter").val(strIsNotEmpty(processType) ? JSON.stringify(processJson[processType]) : '');
			}
		},
		//选择报文节点
		".choose-parameter-nodes":function(){
			chooseParameterNodePath(top.INTERFACE_GET_PARAMETERS_JSON_TREE_URL, {interfaceId:interfaceId}, {
				titleName:"选择该报文包含的节点",
				isChoosePath:true, 
				ifCheckbox:true,
				ifChooseSelf:false,
				nodeClickCallback:function(node, infoDom, zTreeObj){
					zTreeObj.checkNode(node, null, true);
				},
				choosenCallback:function (path, zTreeObj) {
					var checkedNodes = zTreeObj.getCheckedNodes(true);
					var sendNodes = {
							rootId:zTreeObj.setting.data.simpleData.rootPId
					};
					$.each(checkedNodes, function(i, node) {
						sendNodes[node.parameterId] = {
								parameterId:node.parameterId,
								parameterIdentify:node.parameterIdentify,
								defaultValue:node.defaultValue,
								parentId:node.parentId,
								type:node.type,
								path:node.path
						};
					});
					$.post(top.MESSAGE_CREATE_MESSAE_BY_NODES_URL, {messageType:$("#messageType").val(), nodes:JSON.stringify(sendNodes)}, function(json){
						if (json.returnCode == 0) {
							$(".textarea").val(json.message);								
						} else {
							layer.alert(json.msg, {icon:5});
						}
					});
				}
			});
		}		
};

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			interfaceId = GetQueryString("interfaceId");
			protocolType = GetQueryString("protocol");
			$.get("../../js/json/protocol.json", function(json) {
				protocolJson = json;
			});
			
			$.get("../../js/json/messageProcess.json", function(json) {
				processJson = json;
			});
			
			if (interfaceId != null) {
				publish.renderParams.listPage.listUrl = top.MESSAGE_LIST_URL + "?interfaceId=" + interfaceId;
			} else {
				publish.renderParams.listPage.listUrl = top.MESSAGE_LIST_URL;
				$(".add-object").hide();
				$("#import-data-from-excel").hide();
			}		
			
			//编辑页面高度重设
			editPageHeight.add = (editPageHeight.add + 60);
			editPageHeight.edit =(editPageHeight.edit + 60);
   		 	df.resolve();
   	 	},
		editPage:{
			editUrl:top.MESSAGE_EDIT_URL,
			getUrl:top.MESSAGE_GET_URL,
			beforeInit:function(df){
				$("#interfaceInfo\\.interfaceId").val(interfaceId);
				
				if (interfaceId != null && publish.renderParams.editPage.modeFlag == 0) {
					$.post(top.INTERFACE_GET_URL, {id:interfaceId}, function (json) {
						if (json.returnCode == 0) {
							appendSystem(json.object.systems);
						}
					});
				}
				
				
       		 	df.resolve();
       	 	},
			rules:{
				messageName:{
					required:true
				},				
				parameterJson:{
					required:true
				},
				status:{
					required:true
				},
				systems:{
					required:true
				}
			},
			messages:{
				messageName:"请输入报文名称",
				parameterJson:"请输入正确的报文 入参并点击验证",
				callParameter:"请点击配置按钮配置参数"
			},
			renderCallback:function(obj){
				//$("#parameterJson").val();
				$(".textarea").val(obj.parameterJson);				
				protocolType = obj.protocolType;	
				
				interfaceId = obj.interfaceId;
				$("#interfaceInfo\\.interfaceId").val(interfaceId);
				appendSystem(obj.businessSystems);
			}
		},		
		listPage:{
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 9, 11],
			dtOtherSetting:{
				"bStateSave": false
			}
		},
		templateParams:templateParams		
	};

$(function(){	
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/**************************************************************************************/


function getParameterJson(textareaObj){
	textareaObj.spinModal();
	$.get(top.MESSAGE_GET_URL, {id:messageId}, function(data) {
		textareaObj.spinModal(false);
		if (data.returnCode == 0) {						
			textareaObj.val(data.object.parameterJson);
			if (data.object.parameterJson == null || data.object.parameterJson == "") {
				textareaObj.attr("placeholder","该报文没有设置入参内容或者对应接口入参节点发生变化,请检查并重新设置！");
			}			
		} else {
			layer.alert(data.msg, {icon: 5});
		}
	});
}
