var nodeName = "接口自动化";
var templateParams = {
		tableTheads:["接口名称","调用地址", "是否为目录节点","父节点","当前状态","备注", "操作"],
		btnTools:[{
			type:"success",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加操作接口"
		}],
		formControls:[
		{
			edit:true,
			required:false,
			label:"&nbsp;&nbsp;ID",  	
			objText:"opIdText",
			input:[{	
				hidden:true,
				name:"opId"
				}]
		},
		{
			edit:false,
			required:true,
			label:"操作接口名称",  			
			input:[{	
				name:"opName"
				}]
		},
		{
			edit:false,
			label:"调用路径",  			
			input:[{	
				name:"callName"
				}]
		},
		{
			edit:false,
			required:true,
			label:"是否为目录节点",  			
			select:[{	
				name:"isParent",
				option:[{value:"false", text:"否", selected:true},
				        {value:"true", text:"是"}]
				}]
		},
		{
			edit:false,
			required:true,
			label:"父节点",  			
			input:[{	
				name:"parentOpId",
				hidden:true
				}],
			button:[{
				style:"danger",
				value:"选择",
				name:"choose-parent-op"
				}]
		},
		{
			edit:false,
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{value:"0", text:"正常", selected:true},
				        {value:"1", text:"禁止"}]
				}]
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
		  		return checkboxHmtl(data.opName,data.opId,"selectOp");
	           }},
	{"data":"opId"},{"data":"opName"},                                 
	ellipsisData("callName"),
	{
		"data":"isParent",
		"render":function(data) {
			return labelCreate(data, {
				"true":{
					btnStyle:"success",
					status:"是"
					},
				"false":{
					btnStyle:"default",
					status:"否"
					}
			});
		}
	},{"data":"parentOpName"},
	{
		"data":"status",
	    "render":function(data, type, full, meta){
            return labelCreate(data);	    	
	    }
	},{
	    "data":"mark",
	    "className":"ellipsis",
	    "render":function(data, type, full, meta) { 
	    	if (data != "" && data != null) {
		    	return '<a href="javascript:;" onclick="showMark(\'' + full.callName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
	    	}
	    	return "";
	    }
      },{
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
		"#choose-parent-op":function () {
			chooseParentOp();
		},
		".object-edit":function () {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.opId;
			layer_show("编辑操作接口信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();
		},
		".object-del":function () {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("警告：确认要删除此操作接口信息吗(删除的接口信息将会作为公用接口，调用时将不作校验!)？", top.OP_INTERFACE_DEL_URL, data.opId, this);
		},
		"#add-object":function() {//添加操作接口
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加操作接口信息", editHtml, editPageWidth, editPageHeight.add, 1);
			publish.init();
		}		
};

var mySetting = {
		eventList:eventList,
		editPage:{
			editUrl:top.OP_INTERFACE_EDIT_URL,
			getUrl:top.OP_INTERFACE_GET_URL,
			renderCallback:function (obj) {
				$("#choose-parent-op").before('<span>' + obj.parentOpName + '&nbsp;&nbsp;&nbsp;</span>');
			},
			rules:{
				opName:{
					required:true,
					minlength:2,
					maxlength:255
				}
			}
		},
		listPage:{
			listUrl:top.OP_INTERFACE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 8],
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
			onClick:function(event, treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj(treeId);
				zTree.expandNode(treeNode);
			},
			onDblClick:function (event, treeId, treeNode) {//双击之后关闭窗口
				//检查是否是目录节点
				if (treeNode.isParent == false) {
					layer.msg('这不是一个目录节点!', {icon:5, time:1500});
					return false;
				}
				//选择完成
				$("#choose-parent-op").siblings('span').remove();
				$("#choose-parent-op").before('<span>' + treeNode.opName + '&nbsp;&nbsp;&nbsp;</span>');
				$("#parentOpId").val(treeNode.opId);
				layer.close(viewIndex);
			}
		}
	};


var viewIndex;	
function chooseParentOp () {
	layer_show(nodeName + "-请双击选择操作接口父节点", '<div class="page-container"><ul id="node-tree" class="ztree"></ul></div>', 400, 450, 1, function (layero, index) {
		layero.spinModal(false);
		viewIndex = index;
		$.get(top.OP_INTERFACE_LIST_URL, function(json) {			 	 			
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