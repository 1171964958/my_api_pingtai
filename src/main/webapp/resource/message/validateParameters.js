var messageSceneId; //当前正在操作的sceneId
var currIndex;//当前正在操作的layer窗口的index
var responseExample;


var selectGetValueMethodTig = {
		"0":["常量", "请输入用于比对该参数值的字符串,如18655036394", "常量"],
		"1":["入参节点", "请输入正确的入参节点路径,程序将会自动化来获取该路径下的值,区分大小写,请参考接口信息中的参数管理", "入参节点路径"],
		"2":["SQL语句", "请输入用于查询的SQL语句。在SQL语句中,你同样可以使用节点路径: \"#ROOT.DATA.PHONE_NO#\" 来表示表示入参节点数据", "查询SQL语句"],
		"3":["全局变量", "在变量模板模块创建你要使用的全局变量", "全局变量"],
		"4":["正则表达式", "使用JAVA正则表达式模板来进行验证", "正则表达式"]
	};

/**
 * 0-左右边界取关键字验证<br>
 * 1-节点参数验证<br>
 * 2-全文返回验证<br>
 */

var index_selectDb;
var addValidateMethodFlag = 1; //添加的验证规则模式

var templateParams = {
		tableTheads:["验证方式", "节点路径/关联规则", "预期验证值类型", "预期值", "状态", "备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加验证规则"
		},{
			type:"danger",
			size:"M",
			id:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		formControls:[
		{
			edit:true,
			label:"规则ID",  	
			objText:"validateIdText",
			input:[{	
				hidden:true,
				name:"validateId"
				}]
		},
		{
			required:true,
			label:"节点路径",  
			button:[{
				style:"primary",
				value:"选择",
				name:"choose-response-node-path",
				embellish:"&nbsp;"
			},{
				style:"default",
				value:"手动输入",
				name:"input-response-node-path"
			}]
		},
		{
			name:"parameterName"
		},
		{	
			required:true,
			label:"预期验证值类型",  
			button:[{
				style:"primary",
				value:"选择",
				name:"select-get-value-method"
			}]
		},
		{
			required:true,
			label:"预期比对值",  
			input:[{	
				name:"validateValue"
				}]
		},
		{	
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{
					value:"0",
					text:"可用"
				},{
					value:"1",
					text:"禁用"
				}]
				}]
		},
		{
			name:"messageScene.messageSceneId"
		},
		{
			name:"validateMethodFlag",
			value:"1"
		},
		{
			name:"getValueMethod"
		},
		{
			label:"备注",  
			textarea:[{	
				name:"mark",
				placeholder:"验证说明"
			}]
		}
		]		
	};

var columnsSetting = [
                      {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.validateId, data.validateId, "selectValidate");
                          }},
                      {"data":"validateId"},
                      {
                    	  "data":"validateMethodFlag",
                    	  "render":function(data) {
                    		  var option = {
                          			"0":{
                          				btnStyle:"danger",
                  		  				status:"关联验证"
                          			},
                          			"1":{
                          				btnStyle:"primary",
                  		  				status:"节点验证"
                          			},
                          			"2":{
                          				btnStyle:"secondary",
                  		  				status:"全文验证"
                          			}
                          		};
                          		
                          		return labelCreate(data, option);
                    	  }
                      },
                      {
                    	  "data":"parameterName",
                    	  "className":"ellipsis",
                    	  "render":function(data) {                   		  
                    		  if (data == "1") {
                    			  return '<span title="' + data + '">' + data + '</span>';
                    		  } else {
                    			  return "";
                    		  }
                    	  }
                      },                    
                      {
                    	"data":"getValueMethod",
                    	"render":function(data, type, row, meta) {
                    		/*if (row.validateMethodFlag == "0" || row.validateMethodFlag == "2") {
                    			return "";
                    		}*/
                    		var option = {
                    			"0":{
                    				btnStyle:"primary",
            		  				status:"常量"
                    			},
                    			"1":{
                    				btnStyle:"primary",
            		  				status:"入参节点"
                    			},
                    			"default":{
                    				btnStyle:"primary",
            		  				status:"SQL语句"
                    			},
                    			"3":{
                    				btnStyle:"primary",
            		  				status:"全局变量"
                    			},
                    			"4":{
                    				btnStyle:"primary",
            		  				status:"正则表达式"
                    			}
                    		};
                    		
                    		return labelCreate(data, option);
                    	}
                      },
                      {
                    	  "data":"validateValue",
                    	  "className":"ellipsis",
                    	  "render":function(data, type, full, meta) { 
                    		  if (data != "" && data != null) {
                    			  return '<a href="javascript:;" onclick="showMark(\'预期值\', \'validateValue\', this);"><span title="' + data + '">' + data + '</span></a>';
                    		  }
            		    	return "";
                    	  }
	                   },
                      {
                    	  "data":"status",
                    	  "render":function(data, type, row, meta) {
                    		  var checked = '';
                    		  if(data == "0") {checked = 'checked';}                  	
                    		  return '<div class="switch size-MINI" data-on-label="可用" data-off-label="禁用"><input type="checkbox" ' + checked + ' value="' + row.validateId + '"/></div>';                                                   							
                    	  }
                      }, 
                      {
              		    "data":"mark",
              		    "className":"ellipsis",
              		    "render":function(data, type, full, meta) { 
              		    	if (data != "" && data != null) {
                  		    	return '<a href="javascript:;" onclick="showMark(\'验证规则\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
              		    	}
              		    	return "";
              		    }
                      },
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                            var context = [{
                	    		title:"规则编辑",
                	    		markClass:"object-edit",
                	    		iconFont:"&#xe6df;"
                	    	},{
                	    		title:"规则删除",
                	    		markClass:"object-del",
                	    		iconFont:"&#xe6e2;"
                	    	}];                           
                          	return btnIconTemplate(context);
                          }}
                  ];

var eventList = {
		"#view-response-example":function(){//查看返回示例报文
			if (responseExample != null) {
				createViewWindow(responseExample, {
					title:"返回报文示例", //标题
					copyBtn:true//是否显示复制按钮
				});
				return false;
			}
			$.post(top.SCENE_GET_URL, {id:messageSceneId}, function(json){
				if (json.returnCode == 0) {
					if (!strIsNotEmpty(json.object.responseExample)) {
						layer.msg('没有设置返回报文示例,请先设置!', {icon:2, time:1800});
						return false;
					}
					responseExample = json.object.responseExample;
					createViewWindow(responseExample, {
						title:"返回报文示例", //标题
						copyBtn:true//是否显示复制按钮
					});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
			
		},
		"#input-response-node-path":function(){//手动输入返回报文入参节点
			layer.prompt({title:"请输入需要验证的出参节点路径"}, function(value, index, elem){
				  if (strIsNotEmpty(value)) {
					 $("#parameterName").val(value);
					 $("#choose-response-node-path").siblings('span').remove();
					 $("#choose-response-node-path").before('<span>' + value + '&nbsp;</span>');
					 layer.close(index);
				  }
			});
		},
		"#choose-response-node-path":function(){//选择返回报文入参节点
			chooseParameterNodePath(top.SCENE_GET_RESPONSE_MSG_JSON_TREE_URL, {messageSceneId:messageSceneId}, {
				titleName:"出参节点选择",
				isChoosePath:true, 
				notChooseTypes:["Array", "Map", "List", "Object"],
				choosenCallback:function (path) {
					$("#parameterName").val(path);
					$("#choose-response-node-path").siblings('span').remove();
					$("#choose-response-node-path").before('<span>' + path + '&nbsp;</span>');
				}
			});
		},
		"#add-object":function() {
			publish.renderParams.editPage.modeFlag = 0;
			layer.confirm(
					'请选择需要创建的验证方式',
					{
						title:'提示',
						btn:['关联验证','节点验证']
					},function(index){ 
						layer.close(index);
						addValidateMethodFlag = 0;	
						showValidatRulePage();
					},function(index){
						layer.close(index);
						addValidateMethodFlag = 1;
											
						currIndex = layer_show("增加节点验证规则", editHtml, "700", "500", 1, function() {
							addEditPageHtml();
						});
						publish.init();
					});									
		},
		"#batch-del-object":function() {
			var checkboxList = $(".selectValidate:checked");
			batchDelObjs(checkboxList, top.VALIDATE_RULE_DEL_URL);
		},
		".object-edit":function() {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			addValidateMethodFlag = data.validateMethodFlag;
			if (data.validateMethodFlag == "1") {
				publish.renderParams.editPage.objId = data.validateId;
				layer_show("编辑规则信息", editHtml, "700", "530",1, function() {
					addEditPageHtml();
				});
				publish.init();	
			} else {				
				showValidatRulePage(data.validateId);
			}
		},
		".object-del":function() {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此验证规则？请慎重操作!", top.VALIDATE_RULE_DEL_URL, data.validateId, this);
		},
		"#select-get-value-method":function() {  //选择预期值取值方式
			layer.confirm('请选择预期比对数据类型',{btnAlign: 'l', title:'提示',btn:['常量','入参节点','SQL语句', '全局变量', '正则表达式'],
				btn3:function(index) {
					$.ajax({
						type:"POST",
						url:top.QUERY_DB_LIST_ALL_URL,
						success:function(data) {
							if(data.returnCode == 0){
								if(data.data.length < 1){
									layer.alert('没有可用的数据库连接信息,请在系统设置模块添加可用的数据库信息', {icon:5});
									return false;
								}
		
								var selectHtml = '<div class="row cl" style="width:340px;margin:15px;"><div class="form-label col-xs-2"><input type="button" class="btn btn-primary radius" onclick="selectDB();" value="选择"/></div><div class="formControls col-xs-10"><span class="select-box radius mt-0"><select class="select" size="1" name="selectDb" id="selectDb">';
								$.each(data.data, function(i,n) {
									selectHtml += '<option value="' + n.dbId + '">' + n.dbUrl + "-" + n.dbName + '</option>';									
								});
								selectHtml += '</select></span></div></div>';
								index_selectDb = layer.open({
							        type: 1,
							        title: "选择数据库",
							        area: ['355px', '110px'],
							        content:selectHtml
							    });
							}else{
								layer.alert(data.msg,{icon:5});
							}
						}							
					});			
				},
				btn4:function(){//全局变量
					changeTigs("3");
					layer.close(index);
				},
				btn5:function(){//正则表达式
					changeTigs("4");
					layer.close(index);
				}}
				,function(index){						
					changeTigs("0");
					layer.close(index);
				}
				,function(index){					
					changeTigs("1");
					layer.close(index);
				});
		},
		'#choose-validate-template':function() {//选择关联验证模板
			$.post(top.GLOBAL_VARIABLE_LIST_URL, {variableType:"relatedKeyWord"}, function(json) {
				if (json.returnCode == 0) {
					showSelectBox(json.data, "variableId", "variableName", function(variableId, globalVariable, index) {
						$.each(JSON.parse(globalVariable["value"]), function(name, value) {
							if ($("#" + name)) {
								$("#" + name).val(value);
							}
						});
						layer.msg('已加载配置!', {icon:1, time:1500});
						layer.close(index);
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
			publish.renderParams.listPage.listUrl = top.VALIDATE_RULE_LIST_URL + "?messageSceneId=" + messageSceneId;						
			df.resolve();			   		 	
   	 	},
		editPage:{
			editUrl:top.VALIDATE_RULE_EDIT_URL,
			getUrl:top.VALIDATE_RULE_GET_URL,
			rules:{
				parameterName:{
					required:true,
					minlength:1,
					maxlength:255			
				},
				validateValue:{
					required:true,
					minlength:1,
					maxlength:1000
				},
				getValueMethod:{
					required:true
				}				
			},
			beforeInit:function(df){
				$("#messageScene\\.messageSceneId").val(messageSceneId);
       		 	df.resolve();
       	 	},
       	 	renderCallback:function(obj){
       	 		$("#choose-response-node-path").before('<span>' + obj.parameterName + '&nbsp;</span>');
       	 		changeTigs(obj.getValueMethod);
       	 	}
		},		
		listPage:{
			listUrl:top.VALIDATE_RULE_LIST_URL,
			tableObj:".table-sort",
			exportExcel:false,
			columnsSetting:columnsSetting,
			columnsJson:[0, 7, 8],
			dtOtherSetting:{
				"serverSide": false,
				"initComplete":function() {
					$('.switch')['bootstrapSwitch']();
	            	$('.switch input:checkbox').change(function(){
	            		var flag = $(this).is(':checked');
	            		var validateId = $(this).attr('value');
	            		updateStatus(validateId, flag, this);
	            	});
				}
			},
			dtAjaxCallback:function() {
				$('.switch')['bootstrapSwitch']();
            	$('.switch input:checkbox').change(function(){
            		var flag = $(this).is(':checked');
            		var validateId = $(this).attr('value');
            		updateStatus(validateId, flag, this);
            	});
			}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams,mySetting);
	publish.init();
});

/*******************************************************************************************************/
/**
 * 绑定事件
 */
function bindChooseRequestNodePath(){
	$("#validateValue").bind('click', function(){
		chooseParameterNodePath(top.SCENE_GET_REQUEST_MSG_JSON_TREE_URL, {messageSceneId:messageSceneId}, {
			titleName:"入参节点选择",
			isChoosePath:true, 
			notChooseTypes:["Array", "Map", "List", "Object"],
			choosenCallback:function (path) {
				$("#validateValue").val(path);
			}
		});
	});
}

/**
 * 实时改变状态
 */
function updateStatus(validateId, flag, obj) {
	var status = '1';
	if(flag == true){
		status = '0';
	}
	$.post(top.VALIDATE_RULE_UPDATE_STATUS, {validateId:validateId, status:status}, function(json) {
		if(json.returnCode != 0){
			$(obj).click();
			layer.alert(json.msg, {icon:5});
		}
	});
}

/**
 * 选择查询数据库
 */
function selectDB() {
	changeTigs($("#selectDb").val());
	layer.close(index_selectDb);
	
}
/**
 * 节点验证编辑时，根据取值方式的选择不同，改变页面提示
 * @param type 0 - 字符串 1 - 入参节点 其他为数据库的id
 */
function changeTigs(type) {
	$("#select-get-value-method").siblings('strong').remove();
	$("#getValueMethod").val(type);
	
	if (/^9999/.test(type)) {
		type = "2"; 		
		selectGetValueMethodTig[type][2] = "数据库  " + $("#selectDb option:selected").text();
	}
		
	if (type == "1") {
		bindChooseRequestNodePath();
	} else {
		$("#validateValue").unbind('click');
	}
	
	$("#validateValue").attr("placeholder", selectGetValueMethodTig[type][0]);
	$("#tipMsg").text(selectGetValueMethodTig[type][1]);
	//$("#getValueMethodText").text(selectGetValueMethodTig[type][2]);
	$("#select-get-value-method").before('<strong>' + selectGetValueMethodTig[type][2] + '&nbsp;&nbsp;</strong>');
}

/**
 * 节点验证时,附加的页面渲染工作
 */
function addEditPageHtml() {	
	$("#form-edit").append('<div class="row cl"><div class="col-xs-8 col-sm-9 col-xs-offset-4' 
			+ ' col-sm-offset-3"><span id="tipMsg" style="color:red;"></span></div></div>');
}

/**
 * 展示不同的类型验证规则的编辑页面
 * @param type
 */
function showValidatRulePage(validateId) {
	//关联验证 根据publish.renderParams.editPage.modeFlag 0为增加  1为编辑
	if (addValidateMethodFlag == 0) {
		layer_show('关联验证', htmls["messageScene-validateKeyword"], '840', '490', 1, function() {
			if (publish.renderParams.editPage.modeFlag == 1) {
				$.get(top.VALIDATE_RULE_GET_URL, {id:validateId},function(data){
					if(data.returnCode == 0) {
						data = data.object;
						if (data.parameterName != "") {
							var relevanceObject = JSON.parse(data.parameterName);
							$("#ORDER").val(relevanceObject.ORDER);
							$("#LB").val(relevanceObject.LB);
							$("#RB").val(relevanceObject.RB);
							$("#OFFSET").val(relevanceObject.OFFSET);
							$("#LENGHT").val(relevanceObject.LENGHT);
							$("#objectSeqText").text(relevanceObject.ORDER);
						}
						$("#parameterName").val(data.parameterName);
						$("#validateValue").val(data.validateValue);
						$("#validateId").val(data.validateId);
						$("#getValueMethod").val(data.getValueMethod || "0");
						$("#messageScene\\.messageSceneId").val(messageSceneId);
						$("#status").val(data.status);
					} else {
						layer.alert(data.msg,{icon:5});
					}
				});
			}		
		});
	}
	//全文验证 不存在就新建  存在就编辑已存在的
	if (addValidateMethodFlag == 2) {
		layer_show('全文验证管理', htmls["messageScene-validateFullJson"], '800', '520', 1, function() {
			$.get(top.VALIDATE_FULL_RULE_GET_URL, {messageSceneId:messageSceneId, validateMethodFlag:"2"},function(data){
				if(data.returnCode == 0) {
					$("#validateValue").val(data.validateValue);
					$("#validateId").val(data.validateId);
					$("#messageScene\\.messageSceneId").val(messageSceneId);
					$("#status").val(data.status);
				} else {
					layer.alert(data.msg,{icon:5});
				}
			});
		});
	}
	
}


/**
 * 保存验证内容
 * 全文验证或者关联验证
 */
function saveValidateJson(){	
	var sendData = {};
	if ($("#parameterName").length > 0) {
		var parameterName = '{"LB":"' + ($("#LB").val()).replace(/\"/g, "\\\"") 
		+ '","RB":"' + ($("#RB").val()).replace(/\"/g, "\\\"") + '","ORDER":"' + $("#ORDER").val() 
		+ '","OFFSET":"' + ($("#OFFSET").val()).replace(/\"/g, "\\\"") 
		+ '","LENGHT":"' + ($("#LENGHT").val()).replace(/\"/g, "\\\"") + '"}';
		sendData.parameterName = parameterName;
	}	
	sendData.validateMethodFlag = addValidateMethodFlag;
	sendData.validateValue = $("#validateValue").val();
	sendData.validateId = $("#validateId").val();
	sendData.getValueMethod = $("#getValueMethod").val();
	sendData["messageScene.messageSceneId"] = messageSceneId;
	sendData["status"] = $("#status").val();
	
	$.post(top.VALIDATE_RULE_EDIT_URL, sendData, function(data){
		if(data.returnCode == 0){
			refreshTable();
			layer.closeAll('page');
			layer.msg('已保存!', {icon:1, time:1500});
		} else {
			layer.alert(data.msg,{icon:5});
		}
	});		
}

