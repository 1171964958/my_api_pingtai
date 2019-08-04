var templateParams = {
		tableTheads:["菜单名", "菜单路径", "节点等级", "节点图标", "排序", "父节点", "状态","创建时间", "创建用户", "备注", "操作"],
		btnTools:[{
			type:"success",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加菜单"
		}],
		formControls:[
		{
			edit:true,
			required:false,
			label:"&nbsp;&nbsp;ID",  	
			objText:"menuIdText",
			input:[{	
				hidden:true,
				name:"menuId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"菜单名称",  			
			input:[{	
				name:"menuName"
				}]
		},
		{
			edit:false,
			label:"菜单路径",  
			input:[{	
				name:"menuUrl"
				}]
		},
		{
			edit:false,
			required:true,
			label:"节点等级",  			
			select:[{	
				name:"nodeLevel",
				option:[{value:"0", text:"系统"},
						{value:"1", text:"模块"},
				        {value:"2", text:"页面", selected:true}]
				}]
		},
		{
			edit:false,
			label:"节点图标",  
			input:[{	
				name:"iconName"
				}]
		},
		{
			edit:false,
			label:"父节点",  			
			input:[{	
				name:"parentNodeId",
				hidden:true
				}],
			button:[{
				style:"primary",
				value:"选择",
				name:"choose-parent-menu"
				},
				{
				style:"danger",
				value:"清除",
				name:"clean-parent-menu"
				}]
		},
		{
			edit:false,
			label:"排序",  
			required:true,
			input:[{	
				name:"seq"
				}]
		},
		{
			edit:false,
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{value:"1", text:"可用", selected:true},
						{value:"0", text:"禁用"}
				]}]
		},
		{
			 name:"createTime",
			 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
			
		 },
		 {
			 name:"createUser.userId"											
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

var columnsSetting = [
    {
    	"data":null,
    	"render":function(data, type, full, meta){
		  		return checkboxHmtl(data.menuName,data.menuId,"selectMenu");
	           }},
	{"data":"menuId"},{"data":"menuName"},                                 
	ellipsisData("menuUrl"),
	{	
		"data":"nodeLevel",
		"render":function(data){
			return labelCreate(data, {
				"0":{
					btnStyle:"success",
					status:"系统"
					},
				"1":{
					btnStyle:"primary",
					status:"模块"
					},
				"2":{
					btnStyle:"danger",
					status:"页面"
					}
			});
		}
		
	},
	{
		"data":"iconName",
		"render":function(data) {
			return '<i class="Hui-iconfont ' + data + '"></i>';
		}
	},{"data":"seq"},
	ellipsisData("parentNodeName"),
	{	
		"data":"status",
		"render":function(data){
			return labelCreate(data, {
				"1":{
					btnStyle:"success",
					status:"可用"
					},
				"0":{
					btnStyle:"danger",
					status:"禁用"
					}
			});
		}
		
	},
	ellipsisData("createTime"),ellipsisData("createUser.realName"),
	{
		    "data":"mark",
		    "className":"ellipsis",
		    "render":function(data, type, full, meta ){
		    	if (data != "" && data != null) {
	  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.menuName + '-\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
			    	}
			    return "";
		    }
	  }
	,{
      	"data":null,
      	"render":function (data) {
      		var context = [{
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
      }
];

var eventList = {
		"#choose-parent-menu":function () { //打开父节点选择页面
			chooseParentMenu();
		},
		"#clean-parent-menu":function(){ //清除已选择的父节点
			$(this).siblings('input[type="hidden"]').val('');
			$(this).siblings('span').remove();
		},
		".object-edit":function () {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.menuId;
			layer_show("编辑菜单信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();
		},
		".object-del":function () {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("警告：确认要删除此菜单信息吗？", top.BUSI_MENU_DEL_URL, data.menuId, this);
		},
		"#add-object":function() {//添加菜单
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加菜单信息", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();
		}		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.BUSI_MENU_EDIT_URL,
			getUrl:top.BUSI_MENU_GET_URL,
			renderCallback:function (obj) {
				$("#choose-parent-menu").before('<span>' + obj.parentNodeName + '&nbsp;&nbsp;&nbsp;</span>');
			},
			rules:{
				menuName:{
					required:true,
					minlength:2,
					maxlength:100
				},
				menuUrl:{
					minlength:2,
					maxlength:255
				},
				iconName:{
					minlength:2,
					maxlength:100
				},
				seq:{
					required:true,
					digits:true
				}
				
			}
		},
		listPage:{
			listUrl:top.BUSI_MENU_LIST_ALL_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 11],
			dtOtherSetting:{serverSide:false}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/***********************************************************************************/
var zTreeSetting = {
		view: {showIcon: true},
		data: {
			simpleData: {
				enable:true,
				idKey: "menuId",
				pIdKey: "parentNodeId"
			},
			key: {
				name:"menuName",
				title:"menuUrl"
			}
		},
		callback:{
			onClick:function(event, treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj(treeId);
				zTree.expandNode(treeNode);
			},
			onDblClick:function (event, treeId, treeNode) {//双击之后关闭窗口
				if (treeNode.nodeLevel == 2) {
					layer.msg('这不是一个目录节点', {icon:5, time:1500});
					return false;
				}
				//选择完成
				$("#choose-parent-menu").siblings('span').remove();
				$("#choose-parent-menu").before('<span>' + treeNode.menuName + '&nbsp;&nbsp;&nbsp;</span>');
				$("#parentNodeId").val(treeNode.menuId);
				layer.close(viewIndex);
			}
		}
	};


var viewIndex;	
function chooseParentMenu () {
	layer_show("请双击选择操作父节点", '<div class="page-container"><ul id="node-tree" class="ztree"></ul></div>', 400, 450, 1, function (layero, index) {
		layero.spinModal(false);
		viewIndex = index;
		$.get(top.BUSI_MENU_LIST_ALL_URL, function(json) {			 	 			
			if (json.returnCode == 0) {				
				$.fn.zTree.init(layero.find("#node-tree"), zTreeSetting, json.data);
				layero.spinModal(false);
			} else {
				layer.alert(json.msg, {icon:5}, function (){
					layer.close(index);
				});
			}
		});		
	});
}