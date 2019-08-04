var interfaceId; //当前正在编辑的interface的id
var currIndex;//当前正在操作的layer窗口的index

var templateParams = {
		tableTheads:["名称","报文", "中文名","类型","协议","创建时间","状态","创建用户","最后修改","参数", "备注","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加接口"
		},{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		},{
			type:"success",
			size:"M",
			id:"import-data-from-excel",
			iconFont:"&#xe642;",
			name:"Excel导入"
		},{
			type:"primary",
			size:"M",
			id:"export-interface-document",
			iconFont:"&#xe644;",
			name:"下载接口文档"
		}],
		formControls:[
		{
			edit:true,
			label:"接口ID",  	
			objText:"interfaceIdText",
			input:[{	
				hidden:true,
				name:"interfaceId"
				}]
		},
		{
			required:true,
			label:"接口名称",  
			input:[{	
				name:"interfaceName"
				}]
		},
		{
			required:true,
			label:"接口类型",  	
			select:[{	
				name:"interfaceType",
				option:[{
					value:"SL",
					text:"受理类"
				},{
					value:"CX",
					text:"查询类",
					selected:"selected"
				}]
				}]
		},
		{
			required:true,
			label:"协议类型",  	
			select:[{	
				name:"interfaceProtocol",
				option:[{
					value:"HTTP",
					text:"HTTP",
					selected:"selected"
				},{
					value:"HTTPS",
					text:"HTTPS"					
				},{
					value:"WebService",
					text:"WebService"					
				},{
					value:"Socket",
					text:"Socket"					
				}]
				}]
		},
		{
			required:true,
			label:"中文名称",  	
			input:[{	
				name:"interfaceCnName"
				}]
		},
		{
			label:"测试环境", 
			required:true,
			input:[{	
				name:"ids",
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
			reminder:"<span class=\"parameter-reminder\">该请求路径的优先级小于报文和场景中设置的请求路径。<br>如该值为空或者路径不是以 <strong>/</strong> 起始的绝对路径,则会使用测试环境中配置的默认路径!</span>",
			input:[{	
				name:"requestUrlReal",
				placeholder:""
				}]
		},
		{	
			required:true,
			label:"接口状态",  			
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
			name:"user.userId"
							
		},
		{
			name:"oldIds"
							
		},
		{
			edit:true,			
			label:"最后修改",  
			objText:"lastModifyUserText",
			input:[{	
				hidden:true,
				name:"lastModifyUser"
				}]
		},{
			label:"备注",  
			textarea:[{	
				name:"mark"
				}]
		}
		],
		advancedQuery:{
			enable:true, 
			formTitle:"接口信息-高级查询",
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
	                       {label:"接口名",
	                       	input:true,
	                       	name:"interfaceName"},
	                       	{label:"中文名",
	                       	  input:true,
	                       	  name:"interfaceCnName"} 
	                       ], [
	                        {label:"接口类型",
	                       	 select:true,
	                       	 name:"interfaceType",
	                       	 option:[{value:"CX", text:"查询类"}, {value:"SL", text:"受理类"}]}, 
	                       	{label:"接口协议",
	                       	 select:true,
	                       	 name:"interfaceProtocol",
	                       	 option:[{value:"HTTP", text:"HTTP"},{value:"HTTPS", text:"HTTPS"}, {value:"WebService", text:"WebService"}, {value:"Socket", text:"Socket"}]} 
	                        ], [
	                        {label:"状态",
	                       	 select:true,
	                       	 name:"status",
	                       	 option:[{value:"0", text:"可用"}, {value:"1", text:"禁用"}]},  
	                       	{label:"创建时间",
	                       	 input:true,
	                       	 name:"createTimeText",
	                       	 datetime:true}
	                        ], [
	                        {label:"创建用户",
	                       	 input:true,
	                       	 name:"createUserName"},
	                       	{label:"测试环境",
		                       	 select:true,
		                       	 name:"systemId",
		                       	 option:[]}	                       	
	                        ], [{label:"备注",
	                       	 input:true,
	                       	 name:"mark"}	                        
	                        ]]
		}
	};

var columnsSetting = [
          {
          	"data":null,
          	"render":function(data, type, full, meta){                       
                  return checkboxHmtl(data.interfaceName+'-'+data.interfaceCnName,data.interfaceId,"selectInterface");
              }},
          {"data":"interfaceId"},
          {
          	"className":"ellipsis",
		    "data":"interfaceName",
          	"render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
          	},
          	{
                "data":"messagesNum",
                "render":function(data, type, full, meta){
                	var context =
                		[{
              			type:"default",
              			size:"M",
              			markClass:"show-interface-messages",
              			name:data
              		}];
                    return btnTextTemplate(context);
                }},
          {
      		"className":"ellipsis",
		    "data":"interfaceCnName",
		    "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
          },
          {
          	"data":"interfaceType",
          	"render":function(data, type, full, meta){
          		var option = {
          				"SL":{
          					btnStyle:"warning",
          					status:"受理类"
          					},
          				"CX":{
          					btnStyle:"success",
          					status:"查询类"
          				}
          		};                  
          		return labelCreate(data, option);
              }},
          {
            "data":"interfaceProtocol",
            "render":function(data) {
            	return labelCreate(data.toUpperCase());
            }
          },
          ellipsisData("createTime"),
          {
          	"data":"status",
          	"render":function(data, type, full, meta ){
                  return labelCreate(data);
              }},
          ellipsisData("createUserName"),ellipsisData("lastModifyUser"),
          
          {
              "data":"parametersNum",
              "render":function(data, type, full, meta){
              	var context =
              		[{
            			type:"secondary",
            			size:"M",
            			markClass:"edit-params",
            			name:data
            		}];
                  return btnTextTemplate(context);
              }},
          {
  		    "data":"mark",
  		    "className":"ellipsis",
  		    "render":function(data, type, full, meta) { 
  		    	if (data != "" && data != null) {
      		    	return '<a href="javascript:;" onclick="showMark(\'' + full.interfaceName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
  		    	}
  		    	return "";
  		    }
            },
          {
              "data":null,
              "render":function(data, type, full, meta){
                var context = [{
    	    		title:"接口编辑",
    	    		markClass:"object-edit",
    	    		iconFont:"&#xe6df;"
    	    	},{
    	    		title:"接口删除",
    	    		markClass:"object-del",
    	    		iconFont:"&#xe6e2;"
    	    	}];
              	return btnIconTemplate(context);
              }}
      ];	

var eventList = {
		".show-interface-messages":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			$(this).attr("data-title", data.interfaceName + "-" + data.interfaceCnName + " " + "报文管理");
			$(this).attr("_href", "resource/message/message.html?interfaceId=" + data.interfaceId + "&protocol=" + data.interfaceProtocol);
			Hui_admin_tab(this);			
		},
		"#add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("增加接口", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();
			
		},
		'#batch-op':function() {
			layer.confirm(
					'请选择你需要进行的批量操作:',
					{
						title:'批量操作',
						content:'<strong class="c-red">更新测试环境：接口下属的报文、场景、数据全部置为一致</strong>',
						btn:['更新测试环境','删除接口'],
						anim:5
					},function(index){ 
						layer.close(index);						
						batchOp($(".selectInterface:checked"), top.INTERFACE_UPDATE_CHILDREN_BUSINESS_SYSTEMS_URL, "更新", null, "interfaceId");
					},function(index){
						layer.close(index);
						batchDelObjs($(".selectInterface:checked"), top.INTERFACE_DEL_URL);
					});	
		},
		".edit-params":function(){
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.interfaceName + "-" + data.interfaceCnName +" 接口参数管理", "interfaceParameter.html?interfaceId=" + data.interfaceId
					, null, null, 2, null, function() {
					refreshTable();
			});	
		},
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.interfaceId;
			interfaceId = data.interfaceId;
			layer_show("编辑接口信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此接口？此操作同时会删除该接口下所有的报文以及场景相关数据,请谨慎操作!", top.INTERFACE_DEL_URL,data.interfaceId,this);
		},
		"#import-data-from-excel":function() {
			createImportExcelMark("Excel导入接口信息", "../../excel/upload_interface_template.xlsx"
					, top.UPLOAD_FILE_URL, top.INTERFACE_IMPORT_FROM_EXCEL_URL);
		},
		"#choose-business-system":function () {//选择测试环境
			$.post(top.BUSINESS_SYSTEM_LIST_ALL_URL, {protocolType:$("#interfaceProtocol").val()}, function (json) {
				if (json.returnCode == 0) {
					if (json.data.length < 1) {
						layer.msg('无此协议的测试环境可供选择，请联系管理添加!', {icon:0, time:1800});
						return false;
					}
					layerMultipleChoose({
						title:"请选择接口所属的测试环境(可多选,最多不超过10个)",
						customData:{//自定义数据，Array数组对象
							enable:true,
							data:json.data,
							textItemName:"systemName",
							valueItemName:"systemId"
						},
						choosedValues:$("#ids").val().split(","),//已被选择的数据合集		
						closeLayer:true,//是否在确认之后自动关闭窗口
						maxChooseCount:10,
						confirmCallback:function (chooseValues, chooseObjects, index) {
							$("#choose-business-system").siblings('p').remove();
							$.each(chooseObjects, function (i, n) {								
								$("#choose-business-system").before('<p>' + n.systemName + "[" + n.systemHost + ":" + n.systemPort + ']</p>');
							});							
							$("#ids").val(chooseValues.join(','));
						} //选择之后的回调						
				});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});	
		},
		"#interfaceProtocol":{
			'change':function () {
				$("#choose-business-system").siblings('p').remove();
				$("#ids").val("");
			}
		},
		"#export-interface-document":function() {//导出详细的接口文档			
			var checkboxList = $(".selectInterface:checked");
			if (checkboxList.length < 1) {
				return false;
			}
			
			layer.confirm('确认导出选中的' + checkboxList.length + "条接口的详细文档?", {title:'提示', anim:5}, function (index) {
				var loadindex = layer.msg('正在批量导出接口文档...', {icon:16, time:60000, shade:0.35});
				var ids = [];
				$.each(checkboxList, function (i, n) {
					ids.push($(n).val());
				});
				
				$.post(top.INTERFACE_EXPORT_DOCUMENT_EXCEL_URL, {ids:ids.join(",")}, function (json) {
					layer.close(loadindex);
					if (json.returnCode == 0) {
						window.open("../../" + json.path)
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});
				layer.close(index);
			});
		}
		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.INTERFACE_EDIT_URL,
			getUrl:top.INTERFACE_GET_URL,
			renderCallback:function (obj) {
				var systemIds = [];
				$.each(obj.systems, function (i, n) {								
					$("#choose-business-system").before('<p>' + n.systemName + "[" + n.systemHost + ":" + n.systemPort + ']</p>');
					systemIds.push(n.systemId);
				});							
				$("#ids").val(systemIds.join(','));
				$("#oldIds").val(systemIds.join(','));
			},
			ajaxCallbackFun:function (data) {
				if (data.returnCode == 0) {	
					refreshTable();
					if (data.msg != null) {
						layer.confirm(data.msg, {icon:0, btn:['确认更新', '暂不更新']}, function (index, layero) {
							var loadIndex = layer.msg('正在更新(数据量较多请耐心等待)...', {icon:16, time:999999, shade:0.4});
							$.post(top.INTERFACE_UPDATE_CHILDREN_BUSINESS_SYSTEMS_URL, {interfaceId:interfaceId, updateSystems:JSON.stringify(data.updateSystems)}, function (json) {
								layer.close(loadIndex);
								if (json.returnCode == 0) {
									layer.closeAll('page');
									layer.close(index);
									layer.msg("更新成功!", {icon:1, time:1800});
								} else {
									layer.alert(json.msg, {icon:5});
								}
							});
						}, function (index) {						
							layer.closeAll('page');	
						});
					} else {
						layer.closeAll('page');
					}
				} else {
					layer.alert(data.msg, {icon: 5});
				}
			},
			rules:{
				interfaceName:{
					required:true,
					remote:{
						url:top.INTERFACE_CHECK_NAME_URL,
						type:"post",
						dataType: "json",
						data: {                   
					        interfaceName: function() {
					            return $("#interfaceName").val();
					        },
					        interfaceId:function(){
					        	return $("#interfaceId").val();
					        }
					}}
				},				
				interfaceType:{
					required:true
				},
				interfaceCnName:{
					required:true,
					minlength:2,
					maxlength:100
				},
				status:{
					required:true
				},
				ids:{
					required:true
				}
			}

		},		
		listPage:{
			listUrl:top.INTERFACE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 12, 13]
		},
		templateParams:templateParams		
	};

$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/******************************************************************************************************/