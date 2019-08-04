var templateParams = {
		tableTheads:["类型","数据库名","用户名","密码","地址","备注","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加数据源信息"
		},{
			type:"danger",
			size:"M",
			markClass:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		formControls:[
		{
			edit:true,
			required:false,
			label:"&nbsp;&nbsp;ID",  	
			objText:"dbIdText",
			input:[{	
				hidden:true,
				name:"dbId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"数据库类型",  			
			select:[{	
				name:"dbType",
				option:[{value:"mysql", text:"MYSQL", selected:true},
				        {value:"oracle", text:"ORACLE"}]
				}]
		},
		{
			edit:false,
			required:true,
			label:"数据库主机地址",  			
			input:[{	
				hidden:false,
				name:"dbUrl",
				placeholder:"主机IP:端口号"
				}]
		},
		{
			edit:false,
			required:true,
			label:"数据库名",  			
			input:[{	
				hidden:false,
				name:"dbName"
				}]
		},
		{
			edit:false,
			required:true,
			label:"用户名",  			
			input:[{	
				hidden:false,
				name:"dbUsername"
				}]
		},
		{
			label:"&nbsp;&nbsp;密码",
			input:[{	
				name:"dbPasswd"
				}]
		},
		{
			edit:false,
			label:"&nbsp;&nbsp;备注",
			textarea:[{
				placeholder:"数据库备注",
				name:"dbMark"	
			}]
		}	
		]		
	};

var columnsSetting = [{"data":null,
	 "render":function(data, type, full, meta){
		  		return checkboxHmtl(data.dbName,data.dbId,"selectDb");
	           }},
	{"data":"dbId"},{"data":"dbType"},{"data":"dbName"},{"data":"dbUsername"},                                       
	{
    "data":null,
    "render":function(data, type, full, meta ){
    	if (data.dbPasswd != "" && data.dbPasswd != null) {
    		return '<a href="javascript:;" onclick="layer.alert(\''+data.dbPasswd+'\',{icon:4,title:\'数据库密码查看\'});">*******</a>';
    	}
        return ""; 
    }
	},
	{
	    "data":"dbUrl",
	    "className":"ellipsis",
	    "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
	
	},
	{
	    "data":"dbMark",
	    "render":function(data, type, full, meta ){
	    	if (data != "" && data != null) {
  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.dbUrl + '-' + full.dbName + '\', \'dbMark\', this);"><span title="' + data + '">' + data + '</span></a>';
		    	}
		    return "";
	    }
		},
	{
		"data":null,
	    "render":function(data, type, full, meta){	    	
	    	var context = [{
	    		title:"测试连接",
	    		markClass:"db-test",
	    		iconFont:"&#xe6f1;"
	    	},{
	    		title:"编辑",
	    		markClass:"object-edit",
	    		iconFont:"&#xe6df;"
	    	},{
	    		title:"删除",
	    		markClass:"object-del",
	    		iconFont:"&#xe6e2;"
	    	}
	    	];	    		
	    	return btnIconTemplate(context);	    	
	    }}];	

var eventList = {
		".add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("增加数据库信息", editHtml, "800", "550",1);
			publish.init();
			
		},
		".batch-del-object":function(){
			var checkboxList = $(".selectDb:checked");
			batchDelObjs(checkboxList, top.QUERY_DB_DEL_URL);
		},
		".db-test":function(){
			var data = table.row( $(this).parents('tr') ).data();
			$('#div-table-container').spinModal();
	  		$.get(top.QUERY_DB_LINK_TEST_URL, {id:data.dbId}, function(data){
	  			$('#div-table-container').spinModal(false);
	  			if (data.returnCode == 0) { 				
	  				layer.alert("测试连接成功!", {icon: 1});
	  			} else {
	    			layer.alert(data.msg, {icon: 5});
	  			}			
	  		});
		},
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.dbId;
			layer_show("编辑数据源信息", editHtml, "800", "550",1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确认要删除此数据源信息吗？", top.QUERY_DB_DEL_URL, data.dbId, this);
		}
		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.QUERY_DB_EDIT_URL,
			getUrl:top.QUERY_DB_GET_URL,
			rules:{
				dbName:{
					required:true,
					minlength:1,
					maxlength:200
				},
				dbUrl:{
					required:true
				},
				dbUsername:{
					required:true
				}
			},
		},
		listPage:{
			listUrl:top.QUERY_DB_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 5, 7, 8]
		},
		templateParams:templateParams		
	};

$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});