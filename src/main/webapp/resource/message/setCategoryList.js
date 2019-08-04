var templateParams = {
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加目录或测试集"
		},{
			type:"success",
			size:"M",
			id:"show-all-set",
			iconFont:"&#xe667;",
			name:"测试集列表"
		}],
		formControls:[
		{
			edit:true,
			label:"ID",  	
			objText:"setIdText",
			input:[{	
				hidden:true,
				name:"setId"
				}]
		},            
		{
			required:true,
			label:"名称",  
			input:[{	
				name:"setName"
				}]
		},
		{
			required:true,
			label:"类型",  	
			select:[{	
				name:"parented",
				option:[{
					value:"0",
					text:"目录"
				},{
					value:"1",
					text:"测试集"
				}]
				}]
		},
		{
			required:true,
			label:"父目录",  	
			select:[{	
				name:"parentId",
				option:[{
					value:"0",
					text:"最顶级"
				}]
				}]
		},
		{	
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{
					value:"0",
					text:"可用",
					selected:"selected"
				},{
					value:"1",
					text:"禁用"
				}]
				}]
		},
		{
			edit:true,
			name:"user.userId"
							
		},
		{
			label:"备注",  
			textarea:[{	
				name:"mark"
				}]
		},
		{
			 name:"createTime",
			 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
			
		 }]		
	};
var currentSetId;
var eventList = {
	"#add-object":function() {
		publish.renderParams.editPage.modeFlag = 0;		
		var callback = publish.renderParams.editPage.renderCallback;
		layer_show("增加目录或测试集", editHtml, editPageWidth, editPageHeight.add, 1, function() {
			callback();
		});
		publish.init();			
	},
	"#show-all-set":function() {
		$("#current-set-name").html('<span class="c-gray en">&gt;</span> 测试集列表');
		$("#set-list-iframe").attr("src", "testSet.html");
	    $("#set-list-iframe").click();
	    currentSetId = null;
	    $('li').attr("style", "");
	}
};

var mySetting = {
		eventList:eventList,
		renderType:"edit",
		templateParams:templateParams,
		editPage:{
       	 	editUrl:top.SET_EDIT_URL,
			getUrl:top.SET_GET_URL,
			rules:{
				setName:{
					required:true,
					minlength:2,
					maxlength:100
				}				
			},
       	 	renderCallback:function(obj){
       	 		$.get(top.SET_GET_CATEGORY_NODES_URL, function(json) {
       	 			if (json.returnCode == 0) {
       	 				$.each(json.nodes, function(i, node) {
       	 					createOption(node, $("#parentId"), "");
       	 				});
	       	 			if (obj != null) {
	       	 				$("#parented").parents(".row").hide();
	           	 			$("#parentId").val(obj.parentId);
	           	 		}
       	 			} else {
       	 				layer.alert(json.msg, {icon:5});
       	 			}
       	 		});
       	 		
       	 		
       	 	},
       	 ajaxCallbackFun:function (data) {
       		 if (data.returnCode == 0) {
       			 if (data.object.parented == 1) { //如果是测试集代表编辑的是当前测试集信息
       				 //更新导航条测试集名称
       				$("#current-set-name").html('<span class="c-gray en">&gt;</span> 当前测试集 - ' + data.object.setName);
       			 }
       			 createNodeTree();//更新目录树结构
       			 layer.closeAll('page');
       		 } else {
       			 layer.alert(data.msg, {icon:5});
       		 }
       	 }
		},
		templateCallBack:function(df) {
			createNodeTree();
   	 		df.resolve();
   	 	}	
	};


$(function(){			
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

/**
 * 父目录选择下拉框
 * @param node
 * @param selectObj
 * @param sign
 * @returns {Boolean}
 */
function createOption(node, selectObj, sign) {
	//不能将当前节点移动到自己节点下和自己的子节点下
	if (node.id == $("#setId").val()) {		
		return false;
	}
	if (!node.parented) {
		return false;
	}
	selectObj.append('<option value="' + node.id + '">' + sign + "╟&nbsp;" + node.name + '</option>');
	if (node.children != null && node.children.length > 0) {
		$.each(node.children, function(i, n) {
			createOption(n, selectObj, sign + "&nbsp;&nbsp;");
		});
	}
}

/**
 * 生成左侧目录树
 */
function createNodeTree () {
	$.get(top.SET_GET_CATEGORY_NODES_URL, function(json) {
			$(".page-container").spinModal(false); 	 			
			if (json.returnCode == 0) {
				$("#setTree").html('');
	 			layui.use('tree', function(){
	 			  layui.tree({
	 			  elem: '#setTree' //传入元素选择器
	 			  ,nodes: json.nodes
	 			  ,callback:function () {
	 				 currentSetId != null && $("#" + currentSetId) 
	 				 	&&　 $("#" + currentSetId).parent("li").attr("style", "background-color:#C6C6C6");
	 			  }
	 			  ,click: function(elem, node){//node即为当前点击的节点数据	   	 				
		   	 		    if (!node.parented) {
		   	 		    	$("#set-list-iframe").attr("src", "setScene.html?setId=" + node.id + "&setName=" + node.name + "&flag=true");
		   	 		    	$("#set-list-iframe").click();
		   	 		    	$('li').attr("style", "");		   	 		    	
		   	 		    	$(elem).attr("style", "background-color:#C6C6C6");
		   	 		    	currentSetId = node.id;
		   	 		    	$("#current-set-name").html('<span class="c-gray en">&gt;</span> 当前测试集 - ' + node.name)
		   	 		    } else {
		   	 		    	opSetFolder(node.id);
		   	 		    }
	 			  }  
	 			});
	 			});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});
}

/**
 * 目录操作
 * @param id
 */
function opSetFolder(id) {
	layer.confirm('请选择你需要的操作:<br>点击其他位置关闭对话框', {
		title:'提示',
		btn:['删除目录','编辑信息', '返回'],
		anim:5,
		shadeClose:true,
		btn3:function(index) {
			layer.close(index);
		}
	}, function(index) {
		delSet('确认删除此目录吗？<br><span class="c-red">删除之后该目录下所有的测试集和子目录将会移动到被删除目录的上层目录下</span>', id);
		layer.close(index);
	}, function(index) {
		publish.renderParams.editPage.modeFlag = 1;
		publish.renderParams.editPage.objId = id;
		layer_show("编辑目录信息", editHtml, editPageWidth, editPageHeight.edit, 1);
		publish.init();	
		layer.close(index);
	});
}

/**
 * 删除目录或测试集
 * @returns
 */
function delSet (tip, id, callback) {
	layer.confirm(tip, {title:'警告', anim:5}, function(index) {	
				$(".page-container").spinModal(); 
				$.post(top.SET_DEL_URL, {id:id}, function(json) {
					if (json.returnCode == 0) {
						createNodeTree();
						callback && callback(json);
						layer.msg('删除成功!', {icon:1, time:1800});
					} else {
						layer.alert(json.msg, {icon: 5});
					}
				});
				layer.close(index);					
	});
}