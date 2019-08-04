var templateParams = {
		tableTheads:["步骤名称", "执行顺序", "操作", "元素/用例片段", "必要值", "预期值", "跳过执行", "出错中断", "创建时间", "创建用户","备注","更新日志","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加测试步骤"
		},{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		}],
		formControls:[{
					edit:true,
					label:"ID",  	
					objText:"stepIdText",
					input:[{	
						hidden:true,
						name:"stepId"
						}]
					},
					{
						required:true,
						label:"步骤名称名称",  
						input:[{	
							name:"stepName"
							}]					
					},
					{
						required:true,
						label:"执行顺序",  
						input:[{	
							name:"execSeq"
							}]					
					},
					{
						required:true,
						label:"操作类型",  
						input:[{	
							name:"opType",
							hidden:true
							}],
						button:[{
							 style:"success",
							 value:"选择",
							 name:"choose-op-type"
						},{
							 style:"danger",
							 value:"清除",
							 markClass:"clear-choose"
						}]
					},
					{
						label:"元素/用例片段",  
						input:[{	
								name:"element.elementId",
								hidden:true
							},
							{	
								name:"snippetCase.caseId",
								hidden:true
								}],
						button:[{
							 style:"success",
							 value:"选择",
							 name:"choose-element-snippet-case"
						},{
							 style:"danger",
							 value:"清除",
							 markClass:"clear-choose"
						}]
					},
					{
						label:"必要值类型",  
						input:[{	
							name:"requiredDataType",
							hidden:true
						}],
						button:[{
							 style:"success",
							 value:"选择",
							 markClass:"choose-data-type"
						},{
							 style:"danger",
							 value:"清除",
							 markClass:"clear-choose"
						}]				
					 },
					 {
						label:"必要值",  
						input:[{	
							name:"requiredDataValue"
							}]					
					 },
					 {
						label:"预期值类型",  
						input:[{	
							name:"validateDataType",
							hidden:true
						}],
						button:[{
							 style:"success",
							 value:"选择",
							 markClass:"choose-data-type"
						},{
							 style:"danger",
							 value:"清除",
							 markClass:"clear-choose"
						}]				
					 },
					 {
						label:"预期值",  
						input:[{	
							name:"validateDataValue"
							}]					
					 },
					 {	
						required:true,
						label:"跳过执行",  			
						select:[{	
							name:"skipFlag",
							option:[{
								value:"0",
								text:"否"
							},{
								value:"1",
								text:"是"
							}]
							}]
					},
					{	
						required:true,
						label:"出错中断执行",  			
						select:[{	
							name:"errorInterruptFlag",
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
						 name:"createTime",
						 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
						
					 },
					 {
						 name:"createUser.userId"											
					 },	
					 {
						 name:"webTestCase.caseId"											
					 },
					 {
						 name:"configJson"											
					 },
					 {
						 name:"modifyLog",
						 value:"[]"
					 },
					 {
						label:"备注",  			
						textarea:[{
							name:"mark"
						}]
					}]
};
var columnsSetting = [
              {
			  	  	"data":null,
			  	  	"render":function(data, type, full, meta){                       
			          return checkboxHmtl(data.stepName, data.stepId, "selectStep");
			  }},
			  {"data":"stepId"},
			  ellipsisData("stepName"),
			  {"data":"execSeq"},
			  {
				   	"data":"opType",
					"render":function(data) {
						var option = {
	      		  			"default":{
	      		  				btnStyle:"success",
	      		  				status:stepParameters["opType"][data]["text"]
	      		  				}};	
						 return labelCreate(data, option);
			  }},
			  ellipsisData("objectName"),
			  longTextData("requiredDataValue", "stepName", "请求值"),
			  longTextData("validateDataValue", "stepName", "预期值"),
			  {
				   	"data":"skipFlag",
					"render":function(data) {
						var option = {
      		  			"0":{
      		  				btnStyle:"success",
      		  				status:"否"
      		  				},
      		  			"1":{
      		  				btnStyle:"default",
      		  				status:"是"
      		  				}
						};	
						return labelCreate(data, option);
			  }},
			  {
				   	"data":"errorInterruptFlag",
					"render":function(data) {
						var option = {
    		  			"1":{
    		  				btnStyle:"success",
    		  				status:"是"
    		  				},
    		  			"0":{
    		  				btnStyle:"default",
    		  				status:"否"
    		  				}
						};	
						return labelCreate(data, option);
			  }},
			  ellipsisData("createTime"),
			  ellipsisData("createUser.realName"),		  
			  longTextData("mark", "caseName", "备注信息"),	
			  longTextData("modifyLogText", "stepName", "更新日志(最新10条)"),
			  {
				  	"data":null,
                    "render":function(data, type, full, meta){                    		                        	
                    	var context = [{
	                			title:"配置变量",
	                    		markClass:"setting-config",
	                    		iconFont:"&#xe62e;"
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

var eventList = {
		"#add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加测试步骤", editHtml, editPageWidth, editPageHeight.add, 1, function(layero, index) {
				$("#webTestCase\\.caseId").val(caseId);
			});
			publish.init();			
		},
		"#batch-op":function(){
			layer.confirm(
				'请选择你需要进行的批量操作:',
				{
					title:'批量操作',
					btn:['取消','删除'],
					shadeClose:true
				},function(index){ 
					layer.close(index);	
				},function(index){
					layer.close(index);
					batchOp($(".selectStep:checked"), top.WEB_STEP_DEL_URL, "删除", null, "stepId");
				});
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.stepId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑测试步骤信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此测试步骤吗？", top.WEB_STEP_DEL_URL, {id:data.stepId}, this, "删除成功!");
		},
		".choose-data-type":function(){//选择值类型的页面
			var that = this;
			layer_show('选择值类型', templates['web-step-data-type'](stepParameters['dataType']), 830, 330, 1, function(layero, index){
				$(layero).find('#parent-type').val($(that).siblings('input').attr('id'));
				var value = $(that).siblings('input').val();
				if (strIsNotEmpty(value)) {
					/^999999/.test(value) && (value = 'dbSql'); 
					$(layero).find('input[data-type="' + value + '"]').click();
				} else {
					$(layero).find('input:eq(0)').click();	
				}							
			});
		},
		"#choose-op-type":function(){//打开选择操作类型的页面
			var that = this;
			layer_show('选择操作类型', templates['web-step-data-type'](stepParameters['opType']), 830, 330, 1, function(layero, index){
				var value = $(that).siblings('input').val();
				if (strIsNotEmpty(value)) {
					$(layero).find('input[data-type="' + value + '"]').click();
				} else {
					$(layero).find('input:eq(0)').click();	
				}				
			});
		},
		".choose-this-data-type":{//选择数据类型或者操作类型
			'click':function(){
				$(this).addClass('btn-primary').siblings('input').removeClass('btn-primary');
				$('#' + $(this).attr('data-type')).show().siblings('div').hide();
			},
			'dblclick':function(){
				var parentType = $(this).parents('form').find('#parent-type').val();
				var dom =$("#" + parentType);
				if (!strIsNotEmpty(parentType)) {
					dom = $('#opType');
				} 
				var setting = function(dataType, dataText) {
					dom.val(dataType);
					dom.siblings('span').remove();
					dom.after('<span>' + dataText + '</span>');
				};
				
				if ($(this).attr('data-type') == 'dbSql') {
					//选择数据库
					$.ajax({
						type:"POST",
						url:top.QUERY_DB_LIST_ALL_URL,
						async: false,
						success:function(data) {
							if (data.returnCode == 0){
								showSelectBox (data.data, 'dbId', ['dbName', 'dbUrl'], function(id, obj, index){					
									setting(id, '数据库取值' + '-' + obj.dbName + '[' + obj.dbUrl + ']');
									layer.close(index);
								}, '请选择数据源:', '无可用数据源,请至 数据源管理 模块添加!');														
							} else {
								setting('', '');
								layer.alert(data.msg,{icon:5});
							}
						}							
					});	
				} else {
					setting($(this).attr('data-type'), $(this).val());
				}
				
				layer.close($(this).parents('form').find('#layerIndex').val());
			}
		},
		'#choose-element-snippet-case':function(){//选择元素或者用例片段
			layer.confirm(
				'请选择需要关联的对象类型:',
				{
					title:'关联对象',
					btn:['页面元素','用例片段'],
					shadeClose:true
				},function(index){ //页面对象
					layer.close(index);
					layer_show("选择页面元素对象", "chooseElement.html?callbackFun=chooseElement", null, null, 2);
				},function(index){//用例片段
					layer.close(index);
					layer_show("选择用例片段", "chooseCase.html?callbackFun=chooseElement&caseId=" + caseId, 1000, null, 2);
				});			
		},
		'.clear-choose':function(){//清除已选择
			$(this).siblings('span').remove();
			$(this).siblings('input[type="hidden"]').val('');
		},
		'.setting-config':function(){//配置测试步骤
			var data = table.row( $(this).parents('tr') ).data();
			var config = JSON.parse(data.configJson);
			layer_show('测试步骤  ' + data.stepName + '  配置', templates['web-step-config'](config), 920, 620, 1, function(layero, index){
				$(layero).find('#stepId').val(data.stepId);
				$(layero).find('select').each(function(){					
					var key = $(this).attr('name');
					if (config[key] != null) {
						$(this).val(config[key]);
					}
				});
				$(layero).find('#update-web-step-config').one('click', function(){
					$.each(config, function(key, value){
						if ($(layero).find('#' + key)) {
							config[key] = $(layero).find('#' + key).val();
						}
					});
					var configJson = JSON.stringify(config);
					if (!CompareJsonObj(config, JSON.parse(data.configJson))) {
						$.post(top.WEB_STEP_UPDATE_CONFIG_URL, {stepId:$(layero).find('#stepId').val(), configJson:configJson}, function(json){
							if (json.returnCode == 0) {								
								data.configJson = configJson;
								layer.close($(layero).find('#layerIndex').val());
								layer.msg('更新配置成功!', {icon:1, time:1800});
							} else {
								layer.alert(json.msg, {icon:5});
							}
						});
					} else {
						layer.close($(layero).find('#layerIndex').val());
						layer.msg('未改动!', {icon:1800});
					}
				});
			});
		}
};

var stepParameters;
var caseId;

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){	
			caseId = GetQueryString("caseId");
			publish.renderParams.listPage.listUrl = top.WEB_STEP_LIST_URL + "?caseId=" + caseId;	
			$.getJSON('../../js/json/webStepParameter.json', function(json){
				stepParameters = json;
			});
			df.resolve();			   		 	
   	 	},
		listPage:{
			listUrl:top.WEB_STEP_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 13, 14]			
		},
		editPage:{
			editUrl:top.WEB_STEP_EDIT_URL,
			getUrl:top.WEB_STEP_GET_URL,
			renderCallback:function(obj){
				var a = function(type){
					if (type == null) {
						return '';
					}
					var v = stepParameters['dataType'][type];
					if (v) return v.text;
					return obj.requiredDbName || '';
					
				};
				$("#requiredDataType").after('<span>' + a(obj['requiredDataType']) + '  </span>');
				$("#validateDataType").after('<span>' + a(obj['validateDataType']) + '  </span>');
				
				$("#snippetCase\\.caseId").after('<span>' + obj.objectName || '' + '  </span>');
				$("#opType").after('<span>' + stepParameters['opType'][obj.opType]['text'] + '</span>');
			}, 
			rules:{
				stepName:{
					required:true,
					minlength:2,
					maxlength:255
				},
				execSeq:{
					required:true,
					digits:true
				},
				opType:{
					required:true,
				}
				
			}
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});
/*******************************************************************************************************/
function chooseElement(obj, type) {
	$('#choose-element-snippet-case').siblings('span').remove();
	var name = '';
	if (type == 'element') {
		$("#snippetCase\\.caseId").val('');
		$("#element\\.elementId").val(obj.elementId);	
		name = '页面元素-' + obj.elementName;
	} else {
		$("#snippetCase\\.caseId").val(obj.caseId);
		$("#element\\.elementId").val('');	
		name = '用例片段-' + obj.caseName;
	}
	$('#choose-element-snippet-case').before('<span>' + name + '  </span>');
}