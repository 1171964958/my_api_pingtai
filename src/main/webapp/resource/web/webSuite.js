var templateParams = {
		tableTheads:["名称", "浏览器", "测试用例", "运行次数","最近一次运行", "创建用户", "创建时间", "备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加用例集"
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
					objText:"suiteIdText",
					input:[{	
						hidden:true,
						name:"suiteId"
						}]
					},
					{
						required:true,
						label:"用例集名称",  
						input:[{	
							name:"suiteName"
							}]					
					},{
						required:true,
						label:"浏览器",  
						select:[{	
							name:"browserType",
							option:[{
								value:"chrome",
								text:"Chrome"
								},
								{
									value:"ie",
									text:"IE"
								},
								{
									value:"firefox",
									text:"Firefox"
								},
								{
									value:"opera",
									text:"Opera"
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
						 name:"testConfig.configId"											
					 },
					 {
						 name:"lastRunTime"											
					 },
					 {
						 name:"configJson"
					 },
					 {
						 name:"runCount",
						 value:"0"
					 },
					 {
						label:"备注",  			
						textarea:[{
							name:"mark"
						}]
					}],
		advancedQuery:{
			enable:true, 
			formTitle:"Web自动化用例集-高级查询",
			pageOpenSuccessCallback:function (layero, index) {
				
			},
			formControls:[[
	                       {label:"用例集名称",
	                       	input:true,
	                       	name:"suiteName"},
	                       	{label:"浏览器",
	                       	 select:true,
	                       	 name:"browserType",
	                       	 option:[{value:"chrome", text:"Chrome"}, {value:"ie", text:"IE"}, {value:"firefox", text:"Firefox"}, {value:"opera", text:"Opera"}]}, 
	                        ], [
	                        {label:"创建用户",
	                       	 input:true,
	                       	 name:"createUserName"}, 
	                       	{label:"创建时间",
	                       	 input:true,
	                       	 name:"createTimeText",
	                       	 datetime:true} 
	                        ], [
	                        {label:"运行次数不少于",
	                       	 input:true,
	                       	 name:"runCount",
	                       	 datatype:"number"},
	                       	{label:"最近一次运行时间",
	                       	 input:true,
	                       	 name:"lastRunTimeText",
	                       	 datetime:true}
	                        ], [
	                       	{label:"备注",
	                       	 input:true,
	                       	 name:"mark"}
		                    ]]
		}
};

var columnsSetting = [
              {
			  	  	"data":null,
			  	  	"render":function(data, type, full, meta){                       
			  	  		return checkboxHmtl(data.suiteName, data.suiteId, "selectSuite");
			  }},
			  {"data":"suiteId"},
			  ellipsisData("suiteName"),		  
			  {
				   	"data":"browserType",
					"render":function(data) {
						return '<img src="../../img/broswer/' + data + '.png"  titlt="' + data + '" alt = "' + data + '" height="20px" width="20px"/>';
			  }},
			  {
				  	"data":"caseNum",
	                "render":function(data, type, full, meta){
	                	var context =
	                		[{
		              			type:"default",
		              			size:"M",
		              			markClass:"show-suite-case",
		              			name:data
	                		}];
	                    return btnTextTemplate(context);
	                }
			  },
			  ellipsisData("runCount"),
			  ellipsisData("lastRunTime"),
			  ellipsisData("createUser.realName"),
			  ellipsisData("createTime"),			  			  
			  longTextData("mark", "suiteName", "备注信息"),		 
			  {
				  	"data":null,
                    "render":function(data, type, full, meta){                    		                        	
                    	var context = [{
	                			title:"配置变量",
	                    		markClass:"setting-config",
	                    		iconFont:"&#xe62e;"
            				},{
	                			title:"运行时配置",
	                    		markClass:"run-setting",
	                    		iconFont:"&#xe63c;"
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
			layer_show("添加测试用例集", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();			
		},
		"#batch-op":function(){
			layer.confirm(
				'请选择你需要进行的批量操作:',
				{
					title:'批量操作',
					btn:['更换浏览器','删除'],
					shadeClose:true
				},function(index){ 
					layer.close(index);
					showSelectBox ([{id:"chrome", name:"Chrome"}, {id:"ie", name:"IE"}, {id:"firefox", name:"Firefox"}, {id:"opera", name:"Opera"}], "id", "name", function(id, obj, index){
						batchOp($(".selectSuite:checked"), top.WEB_SUITE_CHANGE_BROSWER_TYPE_URL, "修改", null, "suiteId", {browserType:id});
						layer.close(index);
					}, "请选择一个浏览器类型:")		
				},function(index){
					layer.close(index);
					batchOp($(".selectSuite:checked"), top.WEB_SUITE_DEL_URL, "删除", null, "suiteId");
				});
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.suiteId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑用例集信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此测试用例集吗？", top.WEB_SUITE_DEL_URL, {id:data.suiteId}, this, "删除成功!");
		},
		".setting-config":function() {
			var data = table.row( $(this).parents('tr') ).data();			
			layer_show('测试用例集配置', templates['web-test-config-view'](JSON.parse(data.configJson)), 800, 500, 1, function(layero, index){
				$(layero).find('#objectId').val(data.suiteId);
				
				$(layero).find('#update-web-test-config').one('click', function(){  //更新配置
					var configObj = {caseVariables:{}};
					$("#web-test-config-variables").children('.row').each(function(i){
						var key = $(this).find('input').eq(0).val();
						if (strIsNotEmpty(key)) {
							configObj['caseVariables'][key] = $(this).find('input').eq(1).val();
						}
					});
					layer.close(index);
					if (!CompareJsonObj(configObj, JSON.parse(data.configJson))) {
						$.post(top.WEB_SUITE_UPDATE_CONFIG_JSON_URL, {suiteId:$('#objectId').val(), configJson:JSON.stringify(configObj)}, function(json){							
							if (json.returnCode == 0) {								
								data.configJson = JSON.stringify(configObj);
								layer.msg('更新成功!', {icon:1, time:1800});
							} else {
								layer.alert(json.msg, {icon:5});
							}				
						});
					} else {
						layer.msg('未有改动!', {icon:5, time:1800});
					}										
				});
			}, null, null, {shadeClose:false})
		},
		"#web-test-config-add-variable":function(){//增加一对key-value
			$("#web-test-config-variables").append('<div class="row cl"><div class="form-label col-xs-5 col-sm-5">' 
					+ '<input type="text" class="input-text radius"></div>' 
					+ '<div class="formControls col-xs-5 col-sm-5"><input type="text" class="input-text radius"></div>' 
					+ '<div class="formControls col-xs-2 col-sm-2"><a class="btn btn-default radius">' 
					+ '<i class="Hui-iconfont">&#xe60b;</i></a></div></div>');
		},
		"#web-test-config-clear-all-varibale":function(){//清除全部
			layer.confirm('确认删除下列全部的自定义变量吗？', {title:'警告'}, function(index){
				$("#web-test-config-variables").html('');
				layer.msg('全部删除成功!', {icon:1, time:1800});
				layer.close(index);
			});			
		},
		"#web-test-config-variables a":function(){//删除该项
			var that = this;
			layer.confirm('确认删除该项自定义变量吗?', {title:'警告'}, function(index) {
				$(that).parent('.formControls').parent('.row').remove();
				layer.msg('删除成功!', {icon:1, time:1800});
				layer.close(index);
			});
		},
		".show-suite-case":function() {//该测试用例集的测试用例
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.suiteName + ' <strong>' + data.browserType + '</strong> 测试用例', 'webSuiteCase.html?suiteId=' + data.suiteId, null, null, 2
					, null, null, function(){
				refreshTable();
			}, {maxmin: true});			
		},
		".run-setting":function(){
			var data = table.row( $(this).parents('tr') ).data();
			var settingConfig = $.extend(true, {}, data.testConfig);
			layer_show(data.suiteName + '-运行时配置', templates['web-test-config-setting'](settingConfig), 880, 425, 1, function(layero, index){				
				$(layero).find('#update-suite-run-setting').one('click', function(){
					$.each(settingConfig, function(key, value){
						if ($(layero).find('#' + key).length > 0) {
							settingConfig[key] = $(layero).find('#' + key).val();
						}						
					});
					console.log(settingConfig);
					console.log(data.testConfig);
					if (!CompareJsonObj(settingConfig, data.testConfig)) {
						$.post(top.WEB_CONFIG_EDIT_URL, settingConfig, function(json){
							layer.close($(layero).find('#layerIndex').val());
							if (json.returnCode == 0) {								
								data.testConfig = settingConfig;								
								layer.msg('更新运行时设置成功!', {icon:1, time:1800});
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

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){			
			df.resolve();			   		 	
   	 	},
		listPage:{
			listUrl:top.WEB_SUITE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 10]			
		},
		editPage:{
			editUrl:top.WEB_SUITE_EDIT_URL,
			getUrl:top.WEB_SUITE_GET_URL,
			rules:{
				suiteName:{
					required:true,
					minlength:2,
					maxlength:255
				}
			}
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});
