var templateParams = {
		tableTheads:["用例名称", "测试步骤","浏览器", "用例类型", "状态","创建时间", "创建用户", "最近一次运行", "最近一次运行结果", "备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加测试用例"
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
					objText:"caseIdText",
					input:[{	
						hidden:true,
						name:"caseId"
						}]
					},
					{
						required:true,
						label:"用例名称",  
						input:[{	
							name:"caseName"
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
						required:true,
						label:"用例类型",  
						select:[{	
							name:"caseType",
							option:[
								{
									value:"common",
									text:"通用"
								},
								{
									value:"snippet",
									text:"用例片段"
								}]
							}]					
					 },	
					 {
							required:true,
							label:"状态",  
							select:[{	
								name:"status",
								option:[
									{
										value:"1",
										text:"可用"
									},
									{
										value:"0",
										text:"禁用"
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
						 name:"lastRunStatus"
					 },
					 {
						 name:"lastRunTime"											
					 },
					 {
						 name:"configJson"											
					 },
					 {
						label:"备注",  			
						textarea:[{
							name:"mark"
						}]
					}],
		advancedQuery:{
			enable:true, 
			formTitle:"Web自动化测试用例-高级查询",
			pageOpenSuccessCallback:function (layero, index) {
				
			},
			formControls:[[
	                       {label:"用例名称",
	                       	input:true,
	                       	name:"caseName"},
	                       	{label:"浏览器",
	                       	 select:true,
	                       	 name:"browserType",
	                       	 option:[{value:"chrome", text:"Chrome"}, {value:"ie", text:"IE"}, {value:"firefox", text:"Firefox"}, {value:"opera", text:"Opera"}]}, 
	                        ], [
	                        {label:"用例类型",
	                          select:true,
	                       	  name:"caseType",
	                       	  option:[{value:"common", text:"通用"}, {value:"snippet", text:"用例片段"}]}, 
	                       	{label:"创建时间",
	                       	 input:true,
	                       	 name:"createTimeText",
	                       	 datetime:true} 
	                        ], [
	                        {label:"创建用户",
	                       	 input:true,
	                       	 name:"createUserName"},
	                       	{label:"最近一次运行时间",
	                       	 input:true,
	                       	 name:"lastRunTimeText",
	                       	 datetime:true}
	                        ], [
	                        {label:"最近一次运行结果",
	                         select:true,
	                       	 name:"lastRunStatus",
	                       	 option:[{value:"1", text:"成功"}, {value:"0", text:"失败"}]},
	                       	{label:"备注",
	                       	 input:true,
	                       	 name:"mark"}
		                    ], [
	                        {label:"状态",
	                         select:true,
	                       	 name:"status",
	                       	 option:[{value:"1", text:"可用"}, {value:"0", text:"禁用"}]}
			                ]]
		}
};

var columnsSetting = [
              {
			  	  	"data":null,
			  	  	"render":function(data, type, full, meta){                       
			          return checkboxHmtl(data.caseName, data.caseId, "selectCase");
			  }},
			  {"data":"caseId"},
			  ellipsisData("caseName"),
			  {
				  "data":"stepNum",
	                "render":function(data, type, full, meta){
	                	var context =
	                		[{
	              			type:"default",
	              			size:"M",
	              			markClass:"show-case-step",
	              			name:data
	              		}];
	                    return btnTextTemplate(context);
	                }
			  },
			  {
				   	"data":"browserType",
					"render":function(data) {
						return '<img src="../../img/broswer/' + data + '.png"  titlt="' + data + '" alt = "' + data + '" height="20px" width="20px"/>';
			  }},
			  {
				   	"data":"caseType",
					"render":function(data) {
						var option = {
      		  			"common":{
      		  				btnStyle:"primary",
      		  				status:"通用"
      		  				},
      		  			"snippet":{
      		  				btnStyle:"primary",
      		  				status:"用例片段"
      		  				}
						};	
						return labelCreate(data, option);
			  }},
			  {
				   	"data":"status",
					"render":function(data) {
						var option = {
    		  			"1":{
    		  				btnStyle:"success",
    		  				status:"可用"
    		  				},
    		  			"0":{
    		  				btnStyle:"danger",
    		  				status:"禁用"
    		  				}
						};	
						return labelCreate(data, option);
			  }},
			  ellipsisData("createTime"),
			  ellipsisData("createUser.realName"),
			  ellipsisData("lastRunTime"),
			  {
				  "data":"lastRunStatus",
				  "render":function(data) {
					  if (!strIsNotEmpty(data)) {
						  return "";
					  }
					  var option = {
      		  			"1":{
      		  				btnStyle:"success",
      		  				status:"成功"
      		  				},
      		  			"0":{
	      		  			btnStyle:"danger",
	  		  				status:"失败"
      		  			}};	
					 return labelCreate(data, option);
				  }
			  },
			  longTextData("mark", "caseName", "备注信息"),		 
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
			layer_show("添加测试用例", editHtml, editPageWidth, editPageHeight.add, 1);
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
						batchOp($(".selectCase:checked"), top.WEB_CASE_CHANGE_BROSWER_TYPE_URL, "修改", null, "caseId", {browserType:id});
						layer.close(index);
					}, "请选择一个浏览器类型:")		
				},function(index){
					layer.close(index);
					batchOp($(".selectCase:checked"), top.WEB_CASE_DEL_URL, "删除", null, "caseId");
				});
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.caseId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑测试用例信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此测试用例吗？", top.WEB_CASE_DEL_URL, {id:data.caseId}, this, "删除成功!");
		},
		".setting-config":function() {
			var data = table.row( $(this).parents('tr') ).data();			
			layer_show('测试用例配置', templates['web-test-config-view'](JSON.parse(data.configJson)), 800, 500, 1, function(layero, index){
				$(layero).find('#layerIndex').val(index);
				$(layero).find('#objectId').val(data.caseId);
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
		"#update-web-test-config":function(){//更新用例配置
			var configObj = {caseVariables:{}};
			$("#web-test-config-variables").children('.row').each(function(i){
				var key = $(this).find('input').eq(0).val();
				if (strIsNotEmpty(key)) {
					configObj['caseVariables'][key] = $(this).find('input').eq(1).val();
				}
			});
			$.post(top.WEB_CASE_UPDATE_CONFIG_JSON_URL, {caseId:$('#objectId').val(), configJson:JSON.stringify(configObj)}, function(json){
				if (json.returnCode == 0) {
					layer.close($('#layerIndex').val());
					refreshTable();
					layer.msg('更新成功!', {icon:1});
				} else {
					layer.alert(json.msg, {icon:5});
				}				
			});
		},
		".show-case-step":function() {//该测试用例的测试步骤
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.caseName + ' <strong>' + data.browserType + '</strong> 测试步骤', 'webStep.html?caseId=' + data.caseId, null, null, 2
					, function(layero, index){
				layer.full(index);
			}, null, function(){
				refreshTable();
			}, {maxmin: true});			
		}
};

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){			
			df.resolve();			   		 	
   	 	},
		listPage:{
			listUrl:top.WEB_CASE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 12]			
		},
		editPage:{
			editUrl:top.WEB_CASE_EDIT_URL,
			getUrl:top.WEB_CASE_GET_URL,
			rules:{
				caseName:{
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
