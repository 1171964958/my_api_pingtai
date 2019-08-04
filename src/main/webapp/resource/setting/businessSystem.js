var templateParams = {
		tableTheads:["名称", "IP", "端口", "协议", "应用", "接口数量", "状态", "最后修改", "创建时间", "备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加信息"
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
			objText:"systemIdText",
			input:[{	
				hidden:true,
				name:"systemId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"测试环境名称",  			
			input:[{	
				name:"systemName"
				}]
		},
		{
			edit:false,
			required:true,
			label:"IP地址",  			
			input:[{	
				name:"systemHost"
				}]
		},
		{
			edit:false,
			required:true,
			label:"业务端口",  			
			input:[{	
				name:"systemPort"
				}]
		},
		{
			edit:false,
			required:true,
			label:"协议类型",  			
			select:[{	
				name:"protocolType",
				option:[{value:"HTTP", text:"HTTP", selected:true},
				        {value:"HTTPS", text:"HTTPS"},
				        {value:"Socket", text:"Socket"},
				        {value:"WebService", text:"WebService"}
				        ]
				}]
		},
		{
			label:"应用",  			
			input:[{	
				name:"softwareName",
				placeholder:"对应的软硬件应用,仅做参考使用"
				}]
		},
		{
			label:"默认路径",
			reminder:"<span class=\"parameter-reminder\">如果在接口或者报文中没有配置请求路径或者配置的路径不是以\"/\"开始(不是绝对路径),则会使用该默认路径：</span><br>使用<strong>${name}</strong>表示接口名称,<strong>${path}</strong>表示在接口中或者报文中定义的路径,例如：/opt/${path}/${name}",
			input:[{	
				name:"defaultPath"
				}]
		},
		{
			edit:false,
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{value:"0", text:"可用", selected:true},
				        {value:"1", text:"禁用"}]
				}]
		},
		{
			edit:true,
			label:"最后一次修改",  	
			objText:"lastModifyUserText",
			input:[{	
				hidden:true,
				name:"lastModifyUser"
				}]
		},
		{
			 name:"createTime",
			 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
			
		 },
		 {
			edit:false,
			label:"&nbsp;&nbsp;备注",
			textarea:[{
				name:"mark"	
			}]
		}	
		]		
	};

var columnsSetting = [{"data":null,
	 	"render":function(data, type, full, meta){
		  		return checkboxHmtl(data.systemName, data.systemId, "selectSystem");
	          }},
	{"data":"systemId"},{"data":"systemName"},{"data":"systemHost"},{"data":"systemPort"}, 
	{
        "data":"protocolType",
        "render":function(data) {
        	return labelCreate(data.toUpperCase());
        }
    },
	{"data":"softwareName"},
	{
		"data":"infoCount",
		"render":function (data) {
			var context =
          		[{
        			type:"secondary",
        			size:"M",
        			markClass:"view-interface-infos",
        			name:data
        		}];
              return btnTextTemplate(context);
		}
	},
	{
		"data":"status",
		"render":function(data){
			return labelCreate(data, {
				"0":{
					btnStyle:"success",
					status:"可用"
					},
				"1":{
					btnStyle:"danger",
					status:"禁止"
					}
			})
		}
	},
	{"data":"lastModifyUser"},
	ellipsisData("createTime"),
	{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta ){
	    	if (data != "" && data != null) {
  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.systemName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
		    	}
		    return "";
	    }
	},
	{
		"data":null,
	    "render":function(data, type, full, meta){	    	
	    	var context = [{
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
			layer_show("添加测试环境信息", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();
			
		},
		".batch-del-object":function(){
			var checkboxList = $(".selectSystem:checked");
			batchDelObjs(checkboxList, top.BUSINESS_SYSTEM_DEL_URL);
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.systemId;
			layer_show("编辑测试环境信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			if (Number(data.infoCount) > 0) {
				layer.alert("该测试环境下尚有接口信息,请先解除关联再删除!", {icon:0, title:'警告'});
				return false;
			}
			delObj("确认要删除此测试环境信息吗<br><span class=\"c-red\">(删除同时可能会导致配置在该系统下的接口测试失败)</span>？", top.BUSINESS_SYSTEM_DEL_URL, data.systemId, this);
		},
		".view-interface-infos":function () {//打开属于该测试环境的接口信息
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.protocolType + "-" + data.systemName + "[" + data.systemHost + ":" + data.systemPort + "]", "manageSystemInterface.html?systemId=" + data.systemId + "&procotolType=" + data.procotolType, null,null,2);			
		}
		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.BUSINESS_SYSTEM_EDIT_URL,
			getUrl:top.BUSINESS_SYSTEM_GET_URL,
			rules:{
				systemName:{
					required:true,
					minlength:2,
					maxlength:255
				},
				systemHost:{
					required:true
				},
				systemPort:{
					required:true
				}
			}
		},
		listPage:{
			listUrl:top.BUSINESS_SYSTEM_LIST_ALL_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 11, 12],
			dtOtherSetting:{serverSide:false}
		},
		templateParams:templateParams		
	};

$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});