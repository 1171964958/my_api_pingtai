var templateParams = {
		tableTheads:["角色组","角色名","接口权限","菜单权限","备注","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加角色"
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
			objText:"roleIdText",
			input:[{	
				hidden:true,
				name:"roleId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"角色组",  			
			input:[{	
				hidden:false,
				name:"roleGroup",
				placeholder:"输入一个组别名称"
				}]
		},
		{
			edit:false,
			required:true,
			label:"角色名",  			
			input:[{	
				hidden:false,
				name:"roleName",
				placeholder:"4至20个英文字母"
				}]
		},
		{
			edit:false,
			required:true,
			label:"&nbsp;&nbsp;备注",  			
			textarea:[{
				placeholder:"角色备注,角色说明",
				name:"mark"				
			}]
		}]		
	};

var columnsSetting = [
    {
    	"data":null,
    	"render":function(data, type, full, meta){
		  		return checkboxHmtl(data.roleName, data.roleId, "selectRole");
	           }},
	{"data":"roleId"}, {"data":"roleGroup"}, {"data":"roleName"},                                  
	{
	   "data":"oiNum",
	   "render":function(data, type, full, meta ){
		   var context = [{
			   type:"default",
			   size:"S",
			   markClass:"show-role-power",  
			   iconFont:"", 
			   name:data
		   }];
		   return btnTextTemplate(context);		   
		   }
	},
	{
		   "data":"menuNum",
		   "render":function(data, type, full, meta ){
			   var context = [{
				   type:"default",
				   size:"S",
				   markClass:"show-role-menu",  
				   iconFont:"", 
				   name:data
			   }];
			   return btnTextTemplate(context);		   
			   }
		},
	{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta) { 
	    	if (data != "" && data != null) {
		    	return '<a href="javascript:;" onclick="showMark(\'' + full.roleName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
	    	}
	    	return "";
	    }
      },
	{
		"data":null,
	    "render":function(data, type, full, meta){	    	
	    	var context = [
	    	    {
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
	    }
	}
];

var eventList = {
		".batch-del-object":function(){
			var checkboxList = $(".selectRole:checked");
			batchDelObjs(checkboxList, top.ROLE_DEL_URL);
		},
		".add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("增加角色", editHtml, "600", "340",1);
			publish.init();			
		},	
		".show-role-menu":function(){
			var data = table.row( $(this).parents('tr') ).data();
			showRolePower(data, '角色菜单权限编辑', top.ROLE_GET_NODES_MENU_URL, 'menus', {
				data: {
					simpleData: {
						enable:true,
						idKey: "menuId",
						pIdKey: "parentNodeId",
						rootPId: null
					},
					key: {
						name:"menuName",
						title:"menuUrl"
					}
				}
			});			
		},
		".show-role-power":function() {
			var data = table.row( $(this).parents('tr') ).data();
			showRolePower(data, '角色接口权限编辑', top.ROLE_GET_NODES_INTERFACE_URL, 'interfaces', {
				data: {
					simpleData: {
						enable:true,
						idKey: "opId",
						pIdKey: "parentOpId",
						rootPId: 1
					},
					key: {
						name:"opName",
						title:"mark"
					}
				}
			});	
		},
		"#save-role-power":function(){
			var url = top.ROLE_UPDATE_POWER_URL;
			if ($(this).attr('power-type') == 'menus') {
				url = top.ROLE_UPDATE_MENU_URL;
			}
			saveChange(url);
		},
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			if(data.roleName == "admin" || data.roleName == "default"){
	  			layer.msg('不能修改预置管理员角色或者默认角色信息!',{time:1500});
	  		}else{
	  			publish.renderParams.editPage.modeFlag = 1;	
	  			publish.renderParams.editPage.objId = data.roleId;
				layer_show("编辑用户信息", editHtml, "600", "380",1);
				publish.init();	
	  		}
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();
			if(data.roleName == "admin" || data.roleName == "default"){
				layer.msg('不能删除预置管理员角色或者默认角色信息!',{time:1500});
			}else{
				delObj("确认要删除此角色信息吗？", top.ROLE_DEL_URL, data.roleId, this);
			}
			
		}
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.ROLE_EDIT_URL,
			getUrl:top.ROLE_GET_URL,
			rules:{roleName:{isEnglish:true,minlength:4,maxlength:20},roleGroup:{required:true}}
		},
		listPage:{
			listUrl:top.ROLE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0 ,7]
		},
		templateParams:templateParams		
	};

$(function(){			
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

/**************************************************************************/
//当前编辑权限的roleId
var roleId;
//初始的被checked的opId的数组
var initCheckOpId;
//操作的被取消或者删除的op
var currDelCheckOpId;
//操作的增加的op
var currAddCheckOpId;

var zTreeSetting = {
		view: {showIcon: false},
		check: {
			enable: true,
			chkboxType:  { "Y" : "s", "N" : "ps" },
			autoCheckTrigger: true
			},
		data: {
			simpleData: {
				enable:true,
				idKey: "opId",
				pIdKey: "parentOpId",
				rootPId: 1
			},
			key: {
				name:"opName",
				title:"mark",
				checked:"isOwn"
			}
		},
		callback:{
			onCheck:zTreeOnCheck
		}
	};

//Ztree中checkBox被选中或者取消时的回调
function zTreeOnCheck (event, treeId, treeNode) {
	var idName = zTreeSetting.data.simpleData.idKey;
	//判断是否是根节点
	if (treeNode.isParent == "false" || treeNode.isParent == false) {
		//判断是被勾选还是取消勾选
		if (treeNode.isOwn) {
			//被勾选,判断是否为初始的数据		
			if (initCheckOpId.indexOf(treeNode[idName]) == -1) {
				currAddCheckOpId.push(treeNode[idName]);
			} else {
				currDelCheckOpId.splice(currDelCheckOpId.indexOf(treeNode[idName]), 1);
			}	
		} else {
			//取消勾选
			if (initCheckOpId.indexOf(treeNode[idName]) == -1) {
				currAddCheckOpId.splice(currAddCheckOpId.indexOf(treeNode[idName]), 1);
			} else {
				currDelCheckOpId.push(treeNode[idName]);
			}
		}
	}	
}

function showRolePower(data, title, url, nodesName, ztreeOptions) {	
	$.extend(true, zTreeSetting, ztreeOptions);	
	initCheckOpId = [];
	currDelCheckOpId = [];
	currAddCheckOpId = [];	
	roleId = data.roleId;
	
	var idName = zTreeSetting.data.simpleData.idKey;
	
	layer_show(title, htmls["role-power"], "400", "600", 1, function(layero, index) {
		$(layero).find("#roleTable").spinModal();
		$.get(url + "?roleId=" + roleId, function(data) {
			if (data.returnCode == 0) {
				var nodes = data[nodesName];						
				$.each(nodes, function(i,n) {
					if(n.isParent == "true" || n.isParent == true){
						n["open"] = "true";
					}
					if(n.isOwn == true){
						initCheckOpId.push(n[idName]);
					}
				});
				var t = $("#treeDemo");
				t = $.fn.zTree.init(t, zTreeSetting, nodes);
				$("#roleTable").spinModal(false);
				$(layero).find('#save-role-power').attr('power-type', nodesName);
			}else{
				layer.alert(data.msg,{icon:5});
			}		
		});
	});
}


//保存信息发送到服务端
function saveChange(url) {	
	//判断是否需要发送更新请求到后台
	if (currDelCheckOpId.length < 1 && currAddCheckOpId.length < 1) {
		layer.closeAll('page');
		return;
	}
	var sendData = {"roleId":roleId};
	if (currDelCheckOpId.length > 0) {
		sendData["delOpIds"] = currDelCheckOpId.join(",");
	}
	if(currAddCheckOpId.length>0){
		sendData["addOpIds"] = currAddCheckOpId.join(",");
	}
	$.get(url, sendData, function(data) {
		if (data.returnCode == 0) {
			refreshTable();
			layer.closeAll('page');
		} else {
			layer.alert(data.msg,{icon:5});
		}
	});
}