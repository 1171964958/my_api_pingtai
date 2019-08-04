var templateParams = {
		tableTheads:["业务编码", "模块名称","文件夹名称", "框架类型","创建者", "创建时间","脚本作者", "备注","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"object-add",
			iconFont:"&#xe600;",
			name:"添加模块"
		},{
			type:"danger",
			size:"M",
			markClass:"object-batch-del",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		formControls:[
		      		{
		      			edit:true,
		      			label:"ID",  	
		      			objText:"moduleIdText",
		      			input:[{	
		      				hidden:true,
		      				name:"moduleId"
		      				}]
		      		},	
		      		{
		      			required:true,
		      			label:"业务编码",  
		      			input:[{	
		      				name:"moduleCode",
		      				placehodler:"对应业务的op_code或编码"
		      				}]
		      		},
		      		{
		      			required:true,
		      			label:"模块名称",  
		      			input:[{	
		      				name:"moduleName",
		      				}]
		      		},
		      		{	
		      			label:"文件夹名称", 
		      			required:true,
		      			input:[{	
		      				name:"folderName"
		      				}]
		      		},
		      		{
						required:true,
						label:"框架类型",  			
						select:[{	
							name:"frameworkType",
							option:[{
								value:"watir-cucmber",
								text:"Watir-Cucmber"
							}]
							}]				
					 },
		      		{
		      			name:"createUser"
		      		},
		      		{
		      			name:"createTime",
		      			value:new Date().Format("yyyy-MM-dd hh:mm:ss")
		      		},
		      		{	
		      			label:"脚本作者", 
		      			required:true,
		      			input:[{	
		      				name:"author"
		      				}]
		      		},
		      		{
		    			label:"&nbsp;&nbsp;备注",  			
		    			textarea:[{
		    				placeholder:"相关说明:比如所属测试环境，包含测试用例场景等",
		    				name:"mark"				
		    			}]
		    		}
		      		]
	};


var columnsSetting = [
	  {
	  	"data":null,
	  	"render":function(data, type, full, meta){
	  			return checkboxHmtl(data.moduleName, data.moduleId, "selectModule");
	          }
	  },
	{"data":"moduleId"},  
	{"data":"moduleCode"},
	ellipsisData("moduleName"),
	ellipsisData("folderName"),
	{
		"data":"frameworkType",
		"render":function(data, type, full, meta){
			return '<span class="label label-primary radius">' + data + '</span>';
		}
	},
	ellipsisData("createUser"),
	ellipsisData("createTime"),
	ellipsisData("author"),
	{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta) { 
	    	if (data != "" && data != null) {
		    	return '<a href="javascript:;" onclick="showMark(\'' + full.moduleName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
	    	}
	    	return "";
	    }
      },
	{
        "data":null,
        "render":function(data, type, full, meta){
          var context = [{
	    		title:"模块编辑",
	    		markClass:"object-edit",
	    		iconFont:"&#xe6df;"
	    	},{
	    		title:"模块删除",
	    		markClass:"object-del",
	    		iconFont:"&#xe6e2;"
	    	}];                           
        	return btnIconTemplate(context);
        }}
	
	];



var eventList = {
		".object-batch-del":function(){
			var checkboxList = $(".selectModule:checked");
			batchDelObjs(checkboxList, top.WEB_SCRIPT_MODULE_DEL_URL);
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确认要删除此模块吗？", top.WEB_SCRIPT_MODULE_DEL_URL, data.moduleId, this);			
		},
		".object-add":function() {
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("添加模块", editHtml, editPageWidth, editPageHeight.add, 1);
			//layer.full(index);
			publish.init();			
		},
		".object-edit":function() {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.moduleId;
			layer_show("编辑模块信息", editHtml, editPageWidth, editPageHeight.edit,1);
			publish.init();	
		},
};

var mySetting = {
		eventList:eventList,
		listPage:{
			listUrl:top.WEB_SCRIPT_MODULE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 9, 10],
			exportExcel:true
		},
		editPage:{
			editUrl:top.WEB_SCRIPT_MODULE_EDIT_URL,
			getUrl:top.WEB_SCRIPT_MODULE_GET_URL,
			rules:{
				moduleName:{
					required:true				
				},
				folderName:{
					required:true
		        },
		        author:{
		        	required:true
		        },
		        moduleCode:{
		        	maxlength:255,
		        	minlength:1,
		        	remote:{
		        		url:top.WEB_SCRIPT_MODULE_CHECK_NAME_URL,
		        		data: {                    
		        			moduleCode: function() {
		        	            return $("#moduleCode").val();
		        	        },
		        	        moduleId:function() {
		        	        	return $("#moduleId").val();
		        	        }
		        	    }
		        	}
		        }
			}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});